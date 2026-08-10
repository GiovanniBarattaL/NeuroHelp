package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CriarContaResponsavel3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.criarcontaresponsavel3)

        val txtLogin3 = findViewById<TextView>(R.id.fazerlogin3)
        txtLogin3.setOnClickListener {
            val intent = Intent(this, LoginResponsavel::class.java)
            startActivity(intent)
        }

        val btnVoltar4 = findViewById<Button>(R.id.btnvoltar4)
        btnVoltar4.setOnClickListener {
            val intent = Intent(this, CriarContaResponsavel2::class.java)
            startActivity(intent)
        }
    }
}