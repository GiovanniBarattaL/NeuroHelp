package com.example.neurohelp.Login

import com.example.neurohelp.auth.*
import android.widget.EditText
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Cadastro.Cadastro
import com.example.neurohelp.PrincipalActivity
import com.example.neurohelp.R
import androidx.core.graphics.toColorInt

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login)

        // A API autentica ambos os perfis por e-mail e senha; o seletor é apenas visual.

        val btnPai =
            findViewById<Button>(R.id.btnPai)

        val btnProfissional =
            findViewById<Button>(R.id.btnProfissional)

        val btnEntrar =
            findViewById<Button>(R.id.btnEntrar)

        val btnCriarConta =
            findViewById<TextView>(R.id.txtCriarConta)


        btnPai.setOnClickListener {


            btnPai.setBackgroundResource(
                R.drawable.botao_gradiente
            )

            btnPai.setTextColor(Color.WHITE)

            btnProfissional.setBackgroundResource(
                R.drawable.botao_branco
            )

            btnProfissional.setTextColor("#04344E".toColorInt())
        }


        btnProfissional.setOnClickListener {


            btnProfissional.setBackgroundResource(
                R.drawable.botao_gradiente
            )

            btnProfissional.setTextColor(Color.WHITE)

            btnPai.setBackgroundResource(
                R.drawable.botao_branco
            )

            btnPai.setTextColor("#04344E".toColorInt())
        }


        if (SessionStore(this).token() != null) {
            startActivity(Intent(this, PrincipalActivity::class.java))
            finish()
            return
        }
        findViewById<EditText>(R.id.edtSenha).isSaveEnabled = false
        bindSubmission(btnEntrar) {
            findViewById<EditText>(R.id.edtSenha).text.clear()
            startActivity(Intent(this, PrincipalActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
        btnEntrar.setOnClickListener {
            if (!credentials(R.id.edtEmail, R.id.edtSenha)) return@setOnClickListener
            val email = input(R.id.edtEmail)
            val senha = findViewById<EditText>(R.id.edtSenha).text.toString()
            submit(btnEntrar) { it.login(email, senha) }
        }

        findViewById<TextView>(R.id.txtEsqueciSenha).setOnClickListener {
            startActivity(Intent(this, RecuperarSenha::class.java))
        }

        btnCriarConta.setOnClickListener {

            val intent = Intent(
                this,
                Cadastro::class.java
            )

            startActivity(intent)
        }
    }
}