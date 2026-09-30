package com.example.neurohelp.Cadastro

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

        setContentView(R.layout.cadastro_profissional1)

        // Credenciais coletadas apenas na segunda etapa, sem senhas em extras do Intent.
        hideFields(R.id.etEmail, R.id.etSenha, R.id.etConfirmarSenha, R.id.etCep,
            R.id.etCidade, R.id.etBairro, R.id.etLogradouro, R.id.etNumero)
        val btnProximo = findViewById<Button>(R.id.btnProximo)

        btnProximo.setOnClickListener {
            if (!validatePersonal()) return@setOnClickListener
            startActivity(Intent(this, Cadastro_Profissional2::class.java).apply {
                putExtra("nome", input(R.id.etNome))
                putExtra("cpf", input(R.id.etCpf))
                putExtra("telefone", input(R.id.etTelefone))
                putExtra("estado", input(R.id.etUf))
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