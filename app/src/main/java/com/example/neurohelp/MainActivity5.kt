package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity5 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        val btnVoltar3 = findViewById<Button>(R.id.btnvoltar3)
        btnVoltar3.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val btnProx2 = findViewById<Button>(R.id.btnProx2)
        btnProx2.setOnClickListener {
            val intent = Intent(this, MainActivity6::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val txtLogin2 = findViewById<TextView>(R.id.fazerlogin2)
        txtLogin2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}