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

        val btnvoltar2 = findViewById<Button>(R.id.btnvoltar2)
        btnvoltar2.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

    val txtlogin= findViewById<TextView>(R.id.fazerlogin)
    txtlogin.setOnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
        val btnprox= findViewById<Button>(R.id.btnprox)
        btnprox.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)


    }

    }
}