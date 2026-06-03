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

        val btnvoltar3= findViewById<Button>(R.id.btnvoltar3)
        btnvoltar3.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        val btnprox2 = findViewById<Button>(R.id.btnProx2)
        btnprox2.setOnClickListener{
            val intent = Intent(this, MainActivity6::class.java)
            startActivity(intent)
        }
        val txtlogin2 = findViewById<TextView>(R.id.fazerlogin2)
        txtlogin2.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }
}