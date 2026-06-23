package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity4 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        val btnVoltar2 = findViewById<Button>(R.id.btnvoltar2)
        btnVoltar2.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val txtLogin = findViewById<TextView>(R.id.fazerlogin)
        txtLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val btnProx = findViewById<Button>(R.id.btnprox)
        btnProx.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}