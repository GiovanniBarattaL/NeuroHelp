package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CriarContaResponsavel2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.criarcontaresponsavel2)

        val btnVoltar3 = findViewById<Button>(R.id.btnvoltar3)
        btnVoltar3.setOnClickListener {
            val intent = Intent(this, CriarContaResponsavel1::class.java)
            startActivity(intent)
        }

        val btnProx2 = findViewById<Button>(R.id.btnProx2)
        btnProx2.setOnClickListener {
            val intent = Intent(this, CriarContaResponsavel3::class.java)
            startActivity(intent)
        }

        val txtLogin2 = findViewById<TextView>(R.id.fazerlogin2)
        txtLogin2.setOnClickListener {
            val intent = Intent(this, LoginResponsavel::class.java)
            startActivity(intent)
        }
    }
}