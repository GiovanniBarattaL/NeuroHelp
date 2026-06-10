package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity8 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main8)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtlogin5= findViewById<TextView>(R.id.fazerlogin5)
        txtlogin5.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        val btnvoltar6= findViewById<Button>(R.id.btnvoltar6)
        btnvoltar6.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        val upload = findViewById<LinearLayout>(R.id.layoutUpload)

        upload.setOnClickListener {

        }
        val profissao = findViewById<AutoCompleteTextView>(R.id.spProfissao)

        val itens = arrayOf(
            "Psicólogo",
            "Fonoaudiólogo",
            "Terapeuta Ocupacional",
            "Pedagogo",
            "Neuropediatra"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            itens
        )

        profissao.setAdapter(adapter)
    }
}}