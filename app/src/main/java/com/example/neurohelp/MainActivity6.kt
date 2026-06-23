package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity6 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        val txtLogin3 = findViewById<TextView>(R.id.fazerlogin3)
        txtLogin3.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val btnVoltar4 = findViewById<Button>(R.id.btnvoltar4)
        btnVoltar4.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}