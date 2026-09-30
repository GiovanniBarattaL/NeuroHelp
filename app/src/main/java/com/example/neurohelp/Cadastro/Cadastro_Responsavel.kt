package com.example.neurohelp.Cadastro

import com.example.neurohelp.auth.*
import android.widget.EditText
import android.widget.CheckBox
import android.widget.Toast
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R

class Cadastro_Responsavel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cadastro_responsavel)

        hideFields(R.id.etDataNascimento, R.id.etCidade, R.id.etSexo)
        findViewById<EditText>(R.id.etSenha).isSaveEnabled = false
        findViewById<EditText>(R.id.etConfirmarSenha).isSaveEnabled = false
        val btnVoltar =
            findViewById<ImageView>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val txtFazerLogin =
            findViewById<TextView>(R.id.txtFazerLogin)

        txtFazerLogin.setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java
            )

            startActivity(intent)
        }

        val btnProximo =
            findViewById<Button>(R.id.btnProximo)

        btnProximo.text = "Cadastre-se"
        bindSubmission(btnProximo) {
            Toast.makeText(this, "Cadastro realizado. Entre com seu e-mail e senha.", Toast.LENGTH_LONG).show()
            goLogin()
        }
        btnProximo.setOnClickListener {
            val personal = validatePersonal()
            val credentialsOk = credentials(R.id.etEmail, R.id.etSenha, R.id.etConfirmarSenha)
            val emailOk = validate(R.id.etConfirmarEmail, input(R.id.etEmail) == input(R.id.etConfirmarEmail), "Os e-mails não coincidem")
            if (!personal || !credentialsOk || !emailOk) return@setOnClickListener
            if (!findViewById<CheckBox>(R.id.checkTermos).isChecked) {
                Toast.makeText(this, "Aceite os termos para continuar.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val fields = registrationFields()
            submit(btnProximo) { it.register(false, fields) }
        }
    }
}
