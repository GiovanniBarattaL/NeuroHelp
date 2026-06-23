package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val btnProfessional = findViewById<Button>(R.id.pbtnProfissional)
        btnProfessional.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val btnCriarConta = findViewById<TextView>(R.id.pcriarconta)
        btnCriarConta.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}