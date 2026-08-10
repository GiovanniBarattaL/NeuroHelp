package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CriarContaProfissional1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.criarcontaprofissional1)

        val pBtnProx = findViewById<Button>(R.id.pbtnprox)
        pBtnProx.setOnClickListener {
            val intent = Intent(this, CriarContaProfissional2::class.java)
            startActivity(intent)
        }

        val txtLogin4 = findViewById<TextView>(R.id.fazerlogin4)
        txtLogin4.setOnClickListener {
            val intent = Intent(this, LoginResponsavel::class.java)
            startActivity(intent)
        }

        val btnVoltar5 = findViewById<Button>(R.id.btnvoltar5)
        btnVoltar5.setOnClickListener {
            val intent = Intent(this, CriarConta::class.java)
            startActivity(intent)
        }
    }
}