package com.example.neurohelp.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// Persistir somente texto criptografado; a chave permanece no Android Keystore.
class SessionStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey("espectrocare_session", null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder("espectrocare_session", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    fun save(token: String) {
        require(isValid(token)) { "Resposta de autenticação inválida ou expirada." }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key()) }
        val encrypted = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        check(prefs.edit().putString("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString("token", Base64.encodeToString(encrypted, Base64.NO_WRAP)).commit())
    }
    fun token(): String? = try {
        val encrypted = prefs.getString("token", null)
        if (encrypted == null) null else {
            val iv = Base64.decode(prefs.getString("iv", null), Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv)) }
            val value = String(cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)), Charsets.UTF_8)
            if (isValid(value)) value else { clear(); null }
        }
    } catch (_: Exception) { clear(); null }
    fun clear() { prefs.edit().clear().commit() }

    // Papel (responsável/profissional): não é segredo, mas some junto com a sessão (clear()).
    fun papel(): PapelUsuario = try {
        PapelUsuario.valueOf(prefs.getString("papel", null).orEmpty())
    } catch (_: IllegalArgumentException) { PapelUsuario.RESPONSAVEL }
    fun salvarPapel(papel: PapelUsuario) { prefs.edit().putString("papel", papel.name).commit() }
    companion object {
        // Expiração local melhora o fluxo; assinatura e autorização são verificadas pelo servidor.
        fun isValid(token: String, nowSeconds: Long = System.currentTimeMillis() / 1000): Boolean = try {
            val parts = token.split('.')
            parts.size == 3 && JSONObject(String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP), Charsets.UTF_8))
                .getLong("exp") > nowSeconds
        } catch (_: Exception) { false }
    }
}
