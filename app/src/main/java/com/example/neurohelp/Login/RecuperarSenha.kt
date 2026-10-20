package com.example.neurohelp.Login

import com.example.neurohelp.ativarTelaCheia
import com.example.neurohelp.aplicarInsetsNaRaiz
import com.example.neurohelp.limitarLarguraDoConteudo
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.neurohelp.R
import com.example.neurohelp.auth.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject

class RecuperarSenha : AppCompatActivity() {
    private var requestedEmail: String? = null
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("requestedEmail", requestedEmail)
        super.onSaveInstanceState(outState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ativarTelaCheia()
        requestedEmail = savedInstanceState?.getString("requestedEmail")
        setContentView(R.layout.recuperar_senha)
        aplicarInsetsNaRaiz(findViewById(R.id.recuperarSenhaRoot))
        limitarLarguraDoConteudo(findViewById(R.id.conteudoRecuperarSenha))
        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        val btnEnviar = findViewById<Button>(R.id.btnEnviarLink)
        val emailField = findViewById<EditText>(R.id.etEmail)
        val perfil = intent.getBooleanExtra("perfil", false)
        var profileReady = !perfil
        if (perfil) {
            // O e-mail é lido da API e o envio usa a identidade autenticada, sem destinatário no corpo.
            emailField.isEnabled = false
            btnEnviar.isEnabled = false
            lifecycleScope.launch {
                try {
                    val user = JSONObject(ApiService(SessionStore(applicationContext)).protectedRequest("/api/auth/me"))
                    emailField.setText(user.getString("email"))
                    profileReady = true
                    btnEnviar.isEnabled = true
                } catch (e: CancellationException) { throw e }
                catch (_: Exception) { emailField.error = "Não foi possível carregar seu perfil. Volte e tente novamente." }
            }
        }
        bindSubmission(btnEnviar, stateChange = { state ->
            emailField.isEnabled = !perfil && !state.busy
            btnEnviar.isEnabled = profileReady && !state.busy
        }) {
            startActivity(Intent(this, RedefinirSenha::class.java).putExtra("email", requestedEmail ?: input(R.id.etEmail)))
            finish()
        }
        btnEnviar.setOnClickListener {
            if (!profileReady) return@setOnClickListener
            val email = input(R.id.etEmail)
            if (!validate(R.id.etEmail, Patterns.EMAIL_ADDRESS.matcher(email).matches(), "Informe um e-mail válido")) return@setOnClickListener
            // O e-mail não pode mudar enquanto a requisição está em andamento.
            requestedEmail = email
            emailField.isEnabled = false
            submit(btnEnviar) { if (perfil) it.requestOwnPasswordReset() else it.requestPasswordReset(email) }
        }
        findViewById<android.widget.TextView>(R.id.txtJaTenhoCodigo).setOnClickListener {
            if (!profileReady) return@setOnClickListener
            val email = input(R.id.etEmail)
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                startActivity(Intent(this, RedefinirSenha::class.java).putExtra("email", email))
            } else emailField.error = "Informe o e-mail cadastrado"
        }
    }
}
