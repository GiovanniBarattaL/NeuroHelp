package com.example.neurohelp.Login

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.R
import com.example.neurohelp.auth.bindSubmission
import com.example.neurohelp.auth.input
import com.example.neurohelp.auth.submit
import com.example.neurohelp.auth.validate

/** Tela "Recuperar senha": envia o link de redefinição para o e-mail cadastrado. */
class RecuperarSenha : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recuperar_senha)

        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnEnviar = findViewById<Button>(R.id.btnEnviarLink)

        bindSubmission(btnEnviar) {
            Toast.makeText(
                this,
                "Se o e-mail estiver cadastrado, você receberá o link de redefinição.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }

        btnEnviar.setOnClickListener {
            val email = input(R.id.etEmail)
            val valido = validate(
                R.id.etEmail,
                Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                "Informe um e-mail válido"
            )
            if (!valido) return@setOnClickListener
            submit(btnEnviar) { it.requestPasswordReset(email) }
        }
    }
}
