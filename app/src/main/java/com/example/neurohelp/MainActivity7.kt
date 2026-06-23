package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity7 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main7)

        val pBtnProx = findViewById<Button>(R.id.pbtnprox)
        pBtnProx.setOnClickListener {
            val intent = Intent(this, MainActivity8::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val txtLogin4 = findViewById<TextView>(R.id.fazerlogin4)
        txtLogin4.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val btnVoltar5 = findViewById<Button>(R.id.btnvoltar5)
        btnVoltar5.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}