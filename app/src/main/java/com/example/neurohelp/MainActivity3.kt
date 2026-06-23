package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val btnVoltar1 = findViewById<Button>(R.id.btnvoltar1)
        btnVoltar1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val btnEntrarRes = findViewById<TextView>(R.id.btnentrares)
        btnEntrarRes.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val btnEntrarPro = findViewById<TextView>(R.id.btnentrarpro)
        btnEntrarPro.setOnClickListener {
            val intent = Intent(this, MainActivity7::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}