package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity9 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Não usar enableEdgeToEdge()
        setContentView(R.layout.activity_main9)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val txtLogin6 = findViewById<TextView>(R.id.fazerlogin6)
        txtLogin6.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }

        val btnVoltar7 = findViewById<Button>(R.id.btnvoltar7)
        btnVoltar7.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
            overridePendingTransition(0, 0)
        }
    }
}