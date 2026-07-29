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
            overridePendingTransition(0, 0)
        }

        val btnVoltar6 = findViewById<Button>(R.id.btnvoltar6)
        btnVoltar6.setOnClickListener {
            val intent = Intent(this, CriarConta::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val pBtnProx2 = findViewById<Button>(R.id.Pbtnprox2)
        pBtnProx2.setOnClickListener {
            val intent = Intent(this, CriarContaProfissional3::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        val upload = findViewById<LinearLayout>(R.id.layoutUpload)
        upload.setOnClickListener {
            // Aqui depois colocaremos a seleção de arquivos.
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
}