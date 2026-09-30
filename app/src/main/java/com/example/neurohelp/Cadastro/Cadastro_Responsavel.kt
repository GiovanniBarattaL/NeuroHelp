package com.example.neurohelp.Cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R

class Cadastro_Responsavel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cadastro_responsavel)

        val btnVoltar =
            findViewById<ImageView>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

        val btnProximo =
            findViewById<Button>(R.id.btnProximo)

        btnProximo.setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java
            )

            startActivity(intent)
        }
    }
}