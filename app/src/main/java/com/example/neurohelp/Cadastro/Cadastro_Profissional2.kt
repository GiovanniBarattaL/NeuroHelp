package com.example.neurohelp.Cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R

class Cadastro_Profissional2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cadastro_profissional2)

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
            findViewById<Button>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            val intent = Intent(
                this,
                Cadastro_Profissional1::class.java
            )

            startActivity(intent)
        }

        val btnCadastrar =
            findViewById<Button>(R.id.btnCadastrar)

        btnCadastrar.setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java
            )

            startActivity(intent)
        }
    }
}