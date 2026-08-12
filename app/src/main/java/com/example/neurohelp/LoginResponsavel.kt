package com.example.neurohelp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginResponsavel : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginresponsavel)

        val btnProfessional = findViewById<Button>(R.id.btnProfissional)
        btnProfessional.setOnClickListener {
            val intent = Intent(this, LoginProfissional::class.java)
            startActivity(intent)
        }

        val btnCriarConta = findViewById<TextView>(R.id.criarconta)
        btnCriarConta.setOnClickListener {
            val intent = Intent(this, CriarConta::class.java)
            startActivity(intent)
        }
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        btnEntrar.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }
    }
}