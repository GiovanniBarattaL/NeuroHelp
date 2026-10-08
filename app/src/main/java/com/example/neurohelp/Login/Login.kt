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


        // O seletor agora decide qual área do app abre (Home do responsável ou do profissional).
        // O papel é gravado na hora do toque, assim sobrevive à rotação da tela.
        fun aplicarSeletor(profissional: Boolean) {
            val ativo = if (profissional) btnProfissional else btnPai
            val inativo = if (profissional) btnPai else btnProfissional

            ativo.setBackgroundResource(R.drawable.botao_gradiente)
            ativo.setTextColor(Color.WHITE)

            inativo.setBackgroundResource(R.drawable.botao_branco)
            inativo.setTextColor("#04344E".toColorInt())
        }

        fun escolher(papel: PapelUsuario) {
            SessionStore(this).salvarPapel(papel)
            aplicarSeletor(papel == PapelUsuario.PROFISSIONAL)
        }

        btnPai.setOnClickListener { escolher(PapelUsuario.RESPONSAVEL) }
        btnProfissional.setOnClickListener { escolher(PapelUsuario.PROFISSIONAL) }

        if (SessionStore(this).token() != null) {
            startActivity(Intent(this, PrincipalActivity::class.java))
            finish()
            return
        }

        // Sem sessão: começa sempre em "Sou pai/mãe"; depois da rotação mantém o que foi escolhido.
        if (savedInstanceState == null) {
            escolher(PapelUsuario.RESPONSAVEL)
        } else {
            aplicarSeletor(SessionStore(this).papel() == PapelUsuario.PROFISSIONAL)
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