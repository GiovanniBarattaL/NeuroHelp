package com.example.neurohelp.Cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.R

class Cadastro : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastro)

        val btnVoltar = findViewById<ImageView>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnResponsavel =
            findViewById<Button>(R.id.btnResponsavel)

        btnResponsavel.setOnClickListener {
            val intent = Intent(
                this,
                Cadastro_Responsavel::class.java
            )
            startActivity(intent)
        }

        val btnProfissional =
            findViewById<com.google.android.material.button.MaterialButton>(
                R.id.btnProfissional
            )

        btnProfissional.setOnClickListener {
            val intent = Intent(
                this,
                Cadastro_Profissional1::class.java
            )
            startActivity(intent)
        }
    }
}