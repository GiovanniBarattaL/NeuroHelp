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
        val accepted = setOf("nome", "email", "senha", "cpf", "telefone", "estado", "codigoEmail") +
                ADDRESS_KEYS.toSet() + (if (profissional) setOf("numRegistro", "formacao") else emptySet())
        require(fields.keys.all { it in accepted })
        request(if (profissional) "/cadastro/profissional" else "/cadastro/responsavel", "POST", JSONObject(fields))
    }
    /**
     * Pede o e-mail de redefinição de senha.
     * Rota descrita na documentação de recuperação incluída no projeto.
     * A entrega de e-mail depende do backend e da configuração do provedor.
     */
    suspend fun requestPasswordReset(email: String) {
        request("/auth/esqueci-senha", "POST", JSONObject().put("email", email))
    }
    // Contratos descritos em docs/CPF_E_RECUPERACAO_SENHA.md e CONFIRMACAO_EMAIL.md.
    suspend fun requestOwnPasswordReset() {
        protectedRequest("/api/conta/recuperacao-senha", "POST")
    }
    suspend fun resetPassword(email: String, codigo: String, novaSenha: String) {
        request("/auth/redefinir-senha", "POST", JSONObject()
            .put("email", email).put("codigo", codigo).put("novaSenha", novaSenha))
    }
    suspend fun requestRegistrationEmail(email: String) {
        request("/cadastro/email/solicitar", "POST", JSONObject().put("email", email))
    }
    suspend fun protectedRequest(path: String, method: String = "GET", body: JSONObject? = null): String =
        request(path, method, body, true)
    /** Aceita confirmação com foto, ou consulta o recurso após um PUT sem foto no corpo. */
    suspend fun saveAccountPhoto(data: String): String {
        val response = protectedRequest("/api/conta/foto", "PUT", JSONObject().put("fotoPerfil", data))
        val returned = photoData(response)
        if (returned != null) return returned
        // Não repete PUT: uma resposta vazia/texto pode representar um envio concluído.
        val persisted = photoData(protectedRequest("/api/conta/foto"))
        if (persisted == null || persisted != data) throw PhotoConfirmationException()
        return persisted
    }

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
                throw ApiException(status, validationMessage(status, response) ?: errorMessage(status, isProtected))
            }
            response
        } finally { connection.disconnect() }
    }
    companion object {
        // Somente mensagens de validação conhecidas; nunca exibir payloads, valores ou stack traces.
        fun validationMessage(status: Int, response: String): String? {
            if (status == 422) return "E-mail não confirmado. Confira o código de 8 dígitos ou solicite outro."
            if (status != 400) return null
            val known = setOf("CPF inválido. Confira o número informado.", "A senha deve ocupar no máximo 72 bytes.",
                "Informe CRM-SP 1234567: UF e número de até 7 dígitos (sufixo -P se provisório).",
                "Informe nome completo, UF e número de 1 a 7 dígitos, diferente de zero.")
            if (response.trim() in known) return response.trim()
            val fields = try { JSONObject(response).optJSONObject("campos") } catch (_: Exception) { null } ?: return null
            val labels = mapOf("nome" to "Nome", "email" to "E-mail", "senha" to "Senha", "cpf" to "CPF",
                "telefone" to "Telefone", "cep" to "CEP", "estado" to "Estado", "cidade" to "Cidade",
                "bairro" to "Bairro", "logradouro" to "Logradouro", "numero" to "Número",
                "formacao" to "Profissão", "numRegistro" to "Registro", "codigoEmail" to "Código de e-mail")
            val allowed = setOf("Preencha este campo.", "O tamanho informado não é permitido.",
                "Confira este campo.", "CPF deve possuir exatamente 11 dígitos.",
                "Telefone deve possuir 10 ou 11 dígitos.", "CEP deve possuir 8 dígitos.",
                "A senha deve ter ao menos 8 caracteres, com maiúscula, minúscula, número e caractere especial.")
            return labels.mapNotNull { (key, label) ->
                fields.optString(key).takeIf { it in allowed }?.let { "$label: $it" }
            }.joinToString("\n").takeIf { it.isNotBlank() }
        }

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

/** PUT aceito, mas a leitura não confirmou a foto enviada. */
class PhotoConfirmationException : java.io.IOException()
internal fun photoData(response: String): String? {
    val value = response.trim()
    val data = if (value.startsWith("data:image/")) value else try {
        JSONObject(value).optString("fotoPerfil")
    } catch (_: org.json.JSONException) { "" }
    return data.takeIf { it.startsWith("data:image/") }
}

fun photoFailureMessage(error: Exception): String = when (error) {
    is ApiException -> when (error.status) {
        401 -> "Sua sessão expirou. Entre novamente para alterar a foto."
        403 -> "O servidor não autorizou a alteração da foto nesta conta (403)."
        404, 405 -> "O recurso de foto não está disponível no servidor (${error.status}). Informe esse código ao suporte."
        413 -> "O servidor recusou o tamanho da foto. Tente uma imagem menor."
        400, 415, 422 -> "O servidor recusou o formato enviado (${error.status}). Informe esse código ao suporte."
        429 -> "Muitas tentativas. Aguarde um pouco antes de tentar novamente."
        in 500..599 -> "O servidor apresentou uma falha ao salvar a foto (${error.status}). Tente novamente mais tarde."
        else -> "Não foi possível salvar a foto (${error.status}). Tente novamente."
    }
    is PhotoConfirmationException -> "O envio foi aceito, mas não foi possível confirmar a foto salva na conta. Reabra o perfil para conferir antes de tentar de novo."
    is SecurityException -> "Não foi possível acessar a foto escolhida. Abra a galeria e selecione a foto novamente."
    is java.net.SocketTimeoutException -> "A conexão demorou demais. Reabra o perfil para conferir se a foto foi salva antes de tentar novamente."
    is IllegalArgumentException -> "Não foi possível processar a imagem. Escolha outra foto de até 15 MB."
    is java.io.IOException -> "Falha de conexão ou de leitura da foto. Verifique sua internet e selecione a imagem novamente."
    else -> "Não foi possível concluir a alteração da foto. Tente outra imagem."
}
