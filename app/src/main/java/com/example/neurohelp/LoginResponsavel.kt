package com.example.neurohelp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LoginResponsavel : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginresponsavel)

        // Variável de controle: inicia como PAI_MAE porque no XML o seu btnPai começa ativo com o gradiente
        var tipoUsuarioSelecionado = "PAI_MAE"

        // Mapeamento usando exatamente os IDs do seu XML funcional
        val btnPai = findViewById<Button>(R.id.btnPai)
        val btnProfissional = findViewById<Button>(R.id.btnProfissional)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val btnCriarConta = findViewById<TextView>(R.id.txtCriarConta)

        // Clique no botão "Sou pai/mãe"
        btnPai.setOnClickListener {
            tipoUsuarioSelecionado = "PAI_MAE"

            // Ativa o botão Pai (Ganha o gradiente e texto branco)
            btnPai.setBackgroundResource(R.drawable.botao_gradiente)
            btnPai.setTextColor(Color.WHITE)

            // Desativa o botão Profissional (Ganha fundo branco e texto preto)
            btnProfissional.setBackgroundResource(R.drawable.botao_branco)
            btnProfissional.setTextColor(Color.BLACK)
        }

        // Clique no botão "Sou profissional"
        btnProfissional.setOnClickListener {
            tipoUsuarioSelecionado = "PROFISSIONAL"

            // Ativa o botão Profissional (Ganha o gradiente e texto branco)
            btnProfissional.setBackgroundResource(R.drawable.botao_gradiente)
            btnProfissional.setTextColor(Color.WHITE)

            // Desativa o botão Pai (Ganha fundo branco e texto preto)
            btnPai.setBackgroundResource(R.drawable.botao_branco)
            btnPai.setTextColor(Color.BLACK)
        }

        // Clique no botão Entrar
        btnEntrar.setOnClickListener {
            if (tipoUsuarioSelecionado == "PAI_MAE") {
                Toast.makeText(this, "Logando como Pai/Mãe", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PrincipalActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Logando como Profissional", Toast.LENGTH_SHORT).show()
                // Insira aqui a Intent para a Home do Profissional
            }
        }

        btnCriarConta.setOnClickListener {
            val intent = Intent(this, CriarConta::class.java)
            startActivity(intent)
        }
    }
}
