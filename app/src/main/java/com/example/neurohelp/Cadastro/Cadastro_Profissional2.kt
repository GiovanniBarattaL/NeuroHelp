package com.example.neurohelp.Cadastro

import com.example.neurohelp.ativarTelaCheia
import com.example.neurohelp.aplicarInsetsNaRaiz
import com.example.neurohelp.limitarLarguraDoConteudo
import com.example.neurohelp.auth.*
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R

class Cadastro_Profissional2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ativarTelaCheia()
        setContentView(R.layout.cadastro_profissional2)
        aplicarInsetsNaRaiz(findViewById(R.id.cadastroProfissionalRoot))
        limitarLarguraDoConteudo(findViewById(R.id.conteudoCadastro))

        hideFields(R.id.etProfissao, R.id.etAreaAtuacao)
        findViewById<EditText>(R.id.etSenha).isSaveEnabled = false
        findViewById<EditText>(R.id.etConfirmarSenha).isSaveEnabled = false
        if (intent.getStringExtra("cpf").isNullOrBlank()) { finish(); return }
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

        val btnCadastrar =
            findViewById<Button>(R.id.btnCadastrar)

        bindCadastroEmail(btnCadastrar)
        btnCadastrar.setOnClickListener {
            if (!credentials(R.id.etEmail, R.id.etSenha, R.id.etConfirmarSenha)) return@setOnClickListener
            val fields = mapOf(
                "nome" to intent.getStringExtra("nome").orEmpty(),
                "cpf" to intent.getStringExtra("cpf").orEmpty(),
                "telefone" to intent.getStringExtra("telefone").orEmpty(),
                "estado" to intent.getStringExtra("estado").orEmpty(),
                "email" to input(R.id.etEmail), "senha" to password(),
                "numRegistro" to input(R.id.etRegistroProfissional)
            )
            iniciarConfirmacaoCadastro(true, fields)
        }
    }
}
