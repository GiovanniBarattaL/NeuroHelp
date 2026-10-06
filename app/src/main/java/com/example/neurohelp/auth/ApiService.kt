package com.example.neurohelp.auth

import com.example.neurohelp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

class ApiException(val status: Int, message: String) : IOException(message)
class ApiService(private val session: SessionStore, private val baseUrl: String = BuildConfig.API_BASE_URL,
                 private val openConnection: (java.net.URL) -> HttpURLConnection = { it.openConnection() as HttpURLConnection }) {
    suspend fun login(email: String, senha: String) {
        val body = request("/auth/login", "POST", JSONObject().put("email", email).put("senha", senha))
        val token = try { JSONObject(body).getString("token") } catch (_: Exception) {
            throw ApiException(0, "O servidor retornou uma resposta de login inválida.")
        }
        session.save(token)
    }
    suspend fun register(profissional: Boolean, fields: Map<String, String>) {
        val accepted = setOf("nome", "email", "senha", "cpf", "telefone", "estado") +
                if (profissional) setOf("numRegistro") else emptySet()
        require(fields.keys.all { it in accepted })
        request(if (profissional) "/cadastro/profissional" else "/cadastro/responsavel", "POST", JSONObject(fields))
    }
    suspend fun protectedRequest(path: String, method: String = "GET", body: JSONObject? = null): String =
        request(path, method, body, true)
    suspend fun publicRequest(path: String): String = request(path, "GET", null)

    suspend fun professionalPhoto(path: String): ByteArray = withContext(Dispatchers.IO) {
        require(Regex("/api/profissionais/[0-9]+/foto").matches(path))
        val base = URI(baseUrl)
        require(base.scheme == "https")
        val connection = openConnection(base.resolve(path).toURL())
        try {
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 30000; connection.readTimeout = 60000
            connection.useCaches = false
            connection.setRequestProperty("Cache-Control", "no-cache")
            if (connection.responseCode !in 200..299) throw ApiException(connection.responseCode, "Foto não disponível.")
            require(connection.contentType?.startsWith("image/jpeg") == true)
            val bytes = connection.inputStream.use { it.readBytesLimited(256 * 1024) }
            bytes
        } finally { connection.disconnect() }
    }

    private suspend fun request(path: String, method: String, body: JSONObject?, isProtected: Boolean = false): String = withContext(Dispatchers.IO) {
        require(path.startsWith("/") && !path.startsWith("//") && !path.contains('\\'))
        val base = URI(baseUrl)
        val target = base.resolve(path)
        require(base.scheme == "https" && target.host == base.host && target.scheme == base.scheme)
        val token = if (isProtected) session.token() ?: throw ApiException(401, "Sua sessão expirou. Entre novamente.") else null
        val connection = openConnection(target.toURL())
        try {
            connection.requestMethod = method
            connection.instanceFollowRedirects = false
            connection.useCaches = false
            connection.connectTimeout = 30000
            connection.readTimeout = 60000
            connection.setRequestProperty("Accept", "application/json, text/plain")
            token?.let { connection.setRequestProperty("Authorization", "Bearer $it") }
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (status !in 200..299) {
                if (isProtected && status == 401) session.clear()
                throw ApiException(status, errorMessage(status, isProtected))
            }
            response
        } finally { connection.disconnect() }
    }
    companion object {
        // Não exibir corpos de erro: podem conter dados pessoais ou detalhes internos.
        fun errorMessage(status: Int, isProtected: Boolean): String = when (status) {
            401 -> if (isProtected) "Sua sessão expirou. Entre novamente." else "E-mail ou senha inválidos."
            403 -> "Você não tem permissão para acessar este recurso."
            400, 422 -> "Confira os dados informados e tente novamente."
            409 -> "Já existe um cadastro com os dados informados."
            429 -> "Muitas tentativas. Aguarde e tente novamente."
            else -> "Não foi possível concluir a solicitação. Tente novamente mais tarde."
        }
    }
}

internal fun java.io.InputStream.readBytesLimited(limit: Int): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(8192)
    while (true) {
        val count = read(buffer)
        if (count == -1) break
        require(output.size() + count <= limit) { "Arquivo maior que o limite permitido." }
        output.write(buffer, 0, count)
    }
    return output.toByteArray()
}
