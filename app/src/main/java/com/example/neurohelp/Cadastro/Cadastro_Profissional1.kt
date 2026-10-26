package com.example.neurohelp.Cadastro

import com.example.neurohelp.ativarTelaCheia
import com.example.neurohelp.aplicarInsetsNaRaiz
import com.example.neurohelp.limitarLarguraDoConteudo
import com.example.neurohelp.auth.*
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R

class Cadastro_Profissional1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ativarTelaCheia()
        setContentView(R.layout.cadastro_profissional1)
        aplicarInsetsNaRaiz(findViewById(R.id.cadastroProfissionalRoot))
        limitarLarguraDoConteudo(findViewById(R.id.conteudoCadastro))

        // Credenciais coletadas apenas na segunda etapa, sem senhas em extras do Intent.
        hideFields(R.id.etEmail, R.id.etSenha, R.id.etConfirmarSenha)
        val btnProximo = findViewById<Button>(R.id.btnProximo)

        btnProximo.setOnClickListener {
            val personalOk = validatePersonal()
            val addressOk = validateAddress()
            if (!personalOk || !addressOk) return@setOnClickListener
            startActivity(Intent(this, Cadastro_Profissional2::class.java).apply {
                putExtra("nome", input(R.id.etNome))
                putExtra("cpf", input(R.id.etCpf))
                putExtra("telefone", input(R.id.etTelefone))
                addressFields().forEach { (key, value) -> putExtra(key, value) }
            })
        }

        val txtFazerLogin =
            findViewById<TextView>(R.id.txtFazerLogin)

        txtFazerLogin.setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java
            )

            startActivity(intent)
        }

        val btnVoltar =
            findViewById<ImageView>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}