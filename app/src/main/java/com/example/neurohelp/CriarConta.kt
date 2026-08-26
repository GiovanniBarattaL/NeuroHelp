package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CriarConta : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.criarconta)

        val btnVoltar1 = findViewById<Button>(R.id.btnvoltar1)
        btnVoltar1.setOnClickListener {
            val intent = Intent(this, LoginResponsavel::class.java)
            startActivity(intent)
        }

        val btnEntrarRes = findViewById<TextView>(R.id.btnentrares)
        btnEntrarRes.setOnClickListener {
            val intent = Intent(this, CriarContaResponsavel1::class.java)
            startActivity(intent)
        }

        val btnEntrarPro = findViewById<TextView>(R.id.btnentrarpro)
        btnEntrarPro.setOnClickListener {
            val intent = Intent(this, CriarContaProfissional1::class.java)
            startActivity(intent)
        }
    }
}