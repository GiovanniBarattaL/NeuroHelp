package com.example.neurohelp.auth

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun AppCompatActivity.input(id: Int): String = findViewById<EditText>(id).text.toString().trim()
fun AppCompatActivity.password(): String = findViewById<EditText>(R.id.etSenha).text.toString()
fun AppCompatActivity.validate(id: Int, condition: Boolean, message: String): Boolean {
    findViewById<EditText>(id).error = if (condition) null else message
    return condition
}
fun AppCompatActivity.credentials(emailId: Int, passwordId: Int, confirmation: Int? = null): Boolean {
    val email = validate(emailId, android.util.Patterns.EMAIL_ADDRESS.matcher(input(emailId)).matches(), "Informe um e-mail válido")
    val senha = findViewById<EditText>(passwordId).text.toString()
    val pass = validate(passwordId, senha.isNotBlank(), "Informe a senha")
    val confirm = confirmation?.let { validate(it, findViewById<EditText>(it).text.toString() == senha, "As senhas não coincidem") } ?: true
    return email && pass && confirm
}
fun AppCompatActivity.goLogin() {
    startActivity(Intent(this, Login::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
    finish()
}
data class SubmissionState(val busy: Boolean = false, val success: Boolean = false, val error: String? = null)

class AuthSubmissionViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(SubmissionState())
    val state = mutableState.asStateFlow()
    fun consume() { mutableState.value = SubmissionState() }
    fun submit(service: ApiService, action: suspend (ApiService) -> Unit) {
        if (mutableState.value.busy) return
        mutableState.value = SubmissionState(busy = true)
        viewModelScope.launch {
            try {
                action(service)
                mutableState.value = SubmissionState(success = true)
            } catch (e: CancellationException) { throw e
            } catch (e: ApiException) {
                mutableState.value = SubmissionState(error = e.message)
            } catch (_: java.io.IOException) {
                mutableState.value = SubmissionState(error = "Falha de conexão. Verifique a internet e tente novamente.")
            } catch (_: Exception) {
                mutableState.value = SubmissionState(error = "Não foi possível concluir a operação com segurança. Tente novamente.")
            }
        }
    }
}

// A requisição sobrevive à rotação; o ViewModel não mantém referências às telas.
fun AppCompatActivity.bindSubmission(button: Button, success: () -> Unit) {
    val model = ViewModelProvider(this)[AuthSubmissionViewModel::class.java]
    val label = button.text
    val parent = button.parent as ViewGroup
    val progress = ProgressBar(this).apply {
        contentDescription = "Carregando"
        isIndeterminate = true
        visibility = View.GONE
    }
    val tamanho = (32 * resources.displayMetrics.density).toInt()
    val progressParams = if (parent is android.widget.LinearLayout) {
        android.widget.LinearLayout.LayoutParams(tamanho, tamanho).apply {
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            topMargin = (8 * resources.displayMetrics.density).toInt()
        }
    } else ViewGroup.LayoutParams(tamanho, tamanho)
    parent.addView(progress, parent.indexOfChild(button) + 1, progressParams)
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.state.collect { state ->
                button.isEnabled = !state.busy
                button.text = if (state.busy) "Aguarde…" else label
                progress.visibility = if (state.busy) View.VISIBLE else View.GONE
                if (state.success) { model.consume(); success() }
                state.error?.let { model.consume(); Toast.makeText(this@bindSubmission, it, Toast.LENGTH_LONG).show() }
            }
        }
    }
}
fun AppCompatActivity.submit(button: Button, action: suspend (ApiService) -> Unit) {
    if (!button.isEnabled) return
    ViewModelProvider(this)[AuthSubmissionViewModel::class.java].submit(ApiService(SessionStore(applicationContext)), action)
}
fun AppCompatActivity.registrationFields(): Map<String, String> = mapOf(
    "nome" to input(R.id.etNome), "email" to input(R.id.etEmail), "senha" to password(),
    "cpf" to input(R.id.etCpf), "telefone" to input(R.id.etTelefone), "estado" to input(R.id.etUf)
)
fun AppCompatActivity.validatePersonal(): Boolean {
    val name = validate(R.id.etNome, input(R.id.etNome).isNotBlank(), "Informe o nome")
    val cpf = validate(R.id.etCpf, input(R.id.etCpf).replace(Regex("[^0-9]"), "").length == 11, "Informe um CPF com 11 dígitos")
    val telefone = input(R.id.etTelefone)
    val phoneOk = validate(R.id.etTelefone, telefone.isBlank() || telefone.length in 10..11,
        "Informe o telefone com DDD (10 ou 11 dígitos)")
    return name && cpf && phoneOk
}
fun AppCompatActivity.hideFields(vararg ids: Int) {
    ids.forEach { id ->
        var current: View = findViewById(id)
        while (current.parent is View && current !is com.google.android.material.textfield.TextInputLayout) {
            current = current.parent as View
        }
        current.visibility = View.GONE
    }
}

fun goLoginFromPrincipal(activity: AppCompatActivity) { activity.goLogin() }

// Mesmas chaves no Intent entre etapas e no corpo de cadastro da API.
val ADDRESS_KEYS = listOf("cep", "estado", "cidade", "bairro", "logradouro", "numero")
fun AppCompatActivity.addressFields(): Map<String, String> = mapOf(
    "cep" to input(R.id.etCep).filter { it.isDigit() },
    "estado" to input(R.id.etUf).uppercase(java.util.Locale.ROOT),
    "cidade" to input(R.id.etCidade), "bairro" to input(R.id.etBairro),
    "logradouro" to input(R.id.etLogradouro), "numero" to input(R.id.etNumero)
)
fun AppCompatActivity.validateAddress(): Boolean {
    val cep = validate(R.id.etCep, input(R.id.etCep).filter { it.isDigit() }.length == 8, "Informe um CEP com 8 dígitos")
    val ufs = setOf("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO")
    val uf = validate(R.id.etUf, input(R.id.etUf).uppercase(java.util.Locale.ROOT) in ufs, "Informe uma UF válida")
    val required = listOf(R.id.etCidade to "Informe a cidade", R.id.etBairro to "Informe o bairro",
        R.id.etLogradouro to "Informe o logradouro", R.id.etNumero to "Informe o número residencial")
    val complete = required.map { (id, message) -> validate(id, input(id).isNotBlank(), message) }.all { it }
    return cep && uf && complete
}
