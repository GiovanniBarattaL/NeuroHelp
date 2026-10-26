package com.example.neurohelp.Cadastro

import com.example.neurohelp.ativarTelaCheia
import com.example.neurohelp.aplicarInsetsNaRaiz
import com.example.neurohelp.limitarLarguraDoConteudo
import com.example.neurohelp.auth.*
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R

class Cadastro_Profissional2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ativarTelaCheia()
        setContentView(R.layout.cadastro_profissional2)
        aplicarInsetsNaRaiz(findViewById(R.id.cadastroProfissionalRoot))
        limitarLarguraDoConteudo(findViewById(R.id.conteudoCadastro))

        val profissoes = resources.getStringArray(R.array.profissoes_cadastro)
        val campoProfissao = findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.etProfissao)
        campoProfissao.setAdapter(android.widget.ArrayAdapter(this, R.layout.item_profissao_dropdown, profissoes))
        campoProfissao.showSoftInputOnFocus = false
        campoProfissao.setDropDownBackgroundResource(R.drawable.bg_profissao_dropdown)
        campoProfissao.setOnItemClickListener { _, _, position, _ ->
            campoProfissao.setText(profissoes[position], false)
            campoProfissao.error = null
        }

        hideFields(R.id.etAreaAtuacao)
        findViewById<EditText>(R.id.etSenha).isSaveEnabled = false
        findViewById<EditText>(R.id.etConfirmarSenha).isSaveEnabled = false
        if (intent.getStringExtra("cpf").isNullOrBlank()) { finish(); return }
        val txtFazerLogin =
            findViewById<TextView>(R.id.txtFazerLogin)

        txtFazerLogin.setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java
            )

            startActivity(intent)
        }

        val btnVoltar =
            findViewById<ImageView>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnCadastrar =
            findViewById<Button>(R.id.btnCadastrar)

        bindSubmission(btnCadastrar) {
            Toast.makeText(this, "Cadastro realizado. Entre com seu e-mail e senha.", Toast.LENGTH_LONG).show()
            goLogin()
        }
        btnCadastrar.setOnClickListener {
            val professionOk = validate(R.id.etProfissao, input(R.id.etProfissao) in profissoes, "Selecione uma profissão da lista")
            val registroOk = validate(R.id.etRegistroProfissional, input(R.id.etRegistroProfissional).isNotBlank(), "Informe o número do registro profissional")
            if (!professionOk || !registroOk) return@setOnClickListener
            if (!credentials(R.id.etEmail, R.id.etSenha, R.id.etConfirmarSenha)) return@setOnClickListener
            val fields = mapOf(
                "nome" to intent.getStringExtra("nome").orEmpty(),
                "cpf" to intent.getStringExtra("cpf").orEmpty(),
                "telefone" to intent.getStringExtra("telefone").orEmpty(),
                "estado" to intent.getStringExtra("estado").orEmpty(),
                "email" to input(R.id.etEmail), "senha" to password(),
                "numRegistro" to input(R.id.etRegistroProfissional),
                "profissao" to input(R.id.etProfissao)
            )
            val completeFields = fields + ADDRESS_KEYS.associateWith { intent.getStringExtra(it).orEmpty() }
            submit(btnCadastrar) { it.register(true, completeFields) }
        }
    }
}
