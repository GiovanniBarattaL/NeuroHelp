package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity4 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)

        val btnvoltar2 = findViewById<TextView>(R.id.btnvoltar2)
        btnvoltar2.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }

        val txtlogin = findViewById<TextView>(R.id.fazerlogin)
        txtlogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val btnprox = findViewById<Button>(R.id.btnprox)
        btnprox.setOnClickListener {
            startActivity(Intent(this, MainActivity5::class.java))
        }
    }
}