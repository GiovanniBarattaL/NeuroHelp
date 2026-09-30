package com.example.neurohelp.auth

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.*
import org.junit.Assert.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.util.Base64

class AuthIntegrationTest {
    private lateinit var session: SessionStore
    @Before fun setup() { session = SessionStore(InstrumentationRegistry.getInstrumentation().targetContext); session.clear() }
    @After fun teardown() { session.clear() }
    private fun jwt(exp: Long = System.currentTimeMillis() / 1000 + 3600): String =
        "header." + Base64.encodeToString("{\"exp\":$exp}".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING) + ".signature"
    private class Response(val code: Int, val body: String) : HttpURLConnection(URL("https://test.invalid")) {
        val sent = ByteArrayOutputStream()
        override fun connect() {}
        override fun disconnect() {}
        override fun usingProxy() = false
        override fun getResponseCode() = code
        override fun getInputStream() = body.byteInputStream()
        override fun getErrorStream() = body.byteInputStream()
        override fun getOutputStream() = sent
    }
    private fun api(response: Response) = ApiService(session, "https://test.invalid", { response })
    @Test fun validLoginAndBearer() = runBlocking {
        val token = jwt()
        val response = Response(200, JSONObject().put("token", token).put("email", "user@example.com").toString())
        api(response).login("user@example.com", "senha")
        assertEquals(token, session.token())
        assertEquals(setOf("email", "senha"), JSONObject(response.sent.toString("UTF-8")).keys().asSequence().toSet())
        val protectedResponse = Response(200, "ok")
        api(protectedResponse).protectedRequest("/perfil")
        assertEquals("Bearer $token", protectedResponse.getRequestProperty("Authorization"))
    }
    @Test fun invalidLoginWithPlainText() = runBlocking {
        try { api(Response(401, "Email ou senha inválidos.")).login("a@b.com", "errada"); fail() }
        catch (e: ApiException) { assertEquals(401, e.status); assertNull(session.token()) }
    }
    @Test fun bothRegistrationContractsAndNoAutomaticLogin() = runBlocking {
        for (professional in listOf(false, true)) {
            val response = Response(200, "Cadastrado com sucesso")
            var path = ""
            val service = ApiService(session, "https://test.invalid", { path = it.path; response })
            service.register(professional, mapOf("nome" to "Teste", "cpf" to "12345678901", "email" to "a@b.com", "senha" to "senha"))
            assertEquals(if (professional) "/cadastro/profissional" else "/cadastro/responsavel", path)
            assertNull(session.token())
            assertNull(response.getRequestProperty("Authorization"))
        }
    }
    @Test fun networkFailure() = runBlocking {
        try { ApiService(session, "https://test.invalid", { throw IOException("offline") }).login("a@b.com", "senha"); fail() }
        catch (_: IOException) { assertNull(session.token()) }
    }
    @Test fun logoutAndExpiredToken() = runBlocking {
        session.save(jwt()); session.clear(); assertNull(session.token())
        assertFalse(SessionStore.isValid(jwt(1)))
        try { api(Response(200, "ok")).protectedRequest("/perfil"); fail() }
        catch (e: ApiException) { assertEquals(401, e.status) }
    }
    @Test fun unauthorizedClearsSessionButForbiddenKeepsIt() = runBlocking {
        session.save(jwt())
        try { api(Response(403, "<html>Forbidden</html>")).protectedRequest("/perfil"); fail() }
        catch (e: ApiException) { assertEquals(403, e.status); assertNotNull(session.token()) }
        try { api(Response(401, "")).protectedRequest("/perfil"); fail() }
        catch (e: ApiException) { assertEquals(401, e.status); assertNull(session.token()) }
    }
    @Test fun invalidTokenAndServerFailure() = runBlocking {
        try { api(Response(200, "{}")).login("a@b.com", "senha"); fail() }
        catch (_: ApiException) { assertNull(session.token()) }
        try { api(Response(500, "internal details")).register(false, mapOf("email" to "a@b.com")); fail() }
        catch (e: ApiException) { assertFalse(e.message!!.contains("internal details")) }
    }
    @Test fun storedTokenIsEncryptedAndCorruptionClearsSession() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("auth_session", android.content.Context.MODE_PRIVATE)
        val token = jwt()
        session.save(token)
        assertNotEquals(token, prefs.getString("token", null))
        prefs.edit().putString("token", "corrompido").commit()
        assertNull(session.token())
        assertTrue(prefs.all.isEmpty())
    }
    @Test fun duplicateSubmissionIsBlocked() {
        val gate = kotlinx.coroutines.CompletableDeferred<Unit>()
        val calls = java.util.concurrent.atomic.AtomicInteger()
        val model = AuthSubmissionViewModel()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val service = api(Response(200, "ok"))
            model.submit(service) { calls.incrementAndGet(); gate.await() }
            model.submit(service) { calls.incrementAndGet() }
            assertTrue(model.state.value.busy)
            assertEquals(1, calls.get())
            gate.complete(Unit)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue(model.state.value.success)
    }
    @Test fun confirmationIsNeverPartOfContract() = runBlocking {
        try {
            api(Response(200, "ok")).register(false, mapOf("confirmarSenha" to "senha"))
            fail()
        } catch (_: IllegalArgumentException) { assertNull(session.token()) }
    }

}
