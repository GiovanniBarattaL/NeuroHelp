package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CriarContaProfissional2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.criarcontaprofissional2)

        val txtLogin5 = findViewById<TextView>(R.id.fazerlogin5)
        txtLogin5.setOnClickListener {
            val intent = Intent(this, LoginResponsavel::class.java)
            startActivity(intent)
        }

        val btnVoltar6 = findViewById<Button>(R.id.btnvoltar6)
        btnVoltar6.setOnClickListener {
            val intent = Intent(this, CriarConta::class.java)
            startActivity(intent)
        }

        val pBtnProx2 = findViewById<Button>(R.id.Pbtnprox2)
        pBtnProx2.setOnClickListener {
            val intent = Intent(this, LoginResponsavel::class.java)
            startActivity(intent)
        }}
}