package com.example.neurohelp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnprofessional = findViewById<Button>(R.id.btnProfissional)
        btnprofessional.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        val btncriarconta = findViewById<TextView>(R.id.criarconta)
        btncriarconta.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)

    }
}
}
