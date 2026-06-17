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
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Fazer Login
        val txtLogin = findViewById<TextView>(R.id.fazerlogin5)
        txtLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Voltar
        val btnVoltar = findViewById<Button>(R.id.btnvoltar6)
        btnVoltar.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
            finish()
        }

        // Upload
        val upload = findViewById<LinearLayout>(R.id.layoutUpload)
        upload.setOnClickListener {
            // Depois vamos abrir o seletor de arquivos aqui
        }

        // Spinner Profissão
        val profissao = findViewById<AutoCompleteTextView>(R.id.spProfissao)

        val listaProfissoes = arrayOf(
            "Psicólogo",
            "Fonoaudiólogo",
            "Terapeuta Ocupacional",
            "Pedagogo",
            "Neuropediatra"
        )

        val adapterProfissao = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listaProfissoes
        )

        profissao.setAdapter(adapterProfissao)

        // Spinner Área de Atuação
        val areaAtuacao = findViewById<AutoCompleteTextView>(R.id.areaatuacao)

        val listaAreas = arrayOf(
            "Autismo",
            "TDAH",
            "Dislexia",
            "Desenvolvimento Infantil",
            "Neuropsicologia"
        )

        val adapterArea = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listaAreas
        )

        areaAtuacao.setAdapter(adapterArea)
    }
}