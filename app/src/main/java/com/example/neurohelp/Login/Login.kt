package com.example.neurohelp.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.Cadastro.Cadastro
import com.example.neurohelp.PrincipalActivity
import com.example.neurohelp.R
import androidx.core.graphics.toColorInt

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login)

        var tipoUsuarioSelecionado = "PAI_MAE"

        val btnPai =
            findViewById<Button>(R.id.btnPai)

        val btnProfissional =
            findViewById<Button>(R.id.btnProfissional)

        val btnEntrar =
            findViewById<Button>(R.id.btnEntrar)

        val btnCriarConta =
            findViewById<TextView>(R.id.txtCriarConta)


        btnPai.setOnClickListener {

            tipoUsuarioSelecionado = "PAI_MAE"

            btnPai.setBackgroundResource(
                R.drawable.botao_gradiente
            )

            btnPai.setTextColor(Color.WHITE)

            btnProfissional.setBackgroundResource(
                R.drawable.botao_branco
            )

            btnProfissional.setTextColor("#04344E".toColorInt())
        }


        btnProfissional.setOnClickListener {

            tipoUsuarioSelecionado = "PROFISSIONAL"

            btnProfissional.setBackgroundResource(
                R.drawable.botao_gradiente
            )

            btnProfissional.setTextColor(Color.WHITE)

            btnPai.setBackgroundResource(
                R.drawable.botao_branco
            )

            btnPai.setTextColor("#04344E".toColorInt())
        }


        btnEntrar.setOnClickListener {

            if (tipoUsuarioSelecionado == "PAI_MAE") {

                Toast.makeText(
                    this,
                    "Logando como Pai/Mãe",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(
                    this,
                    PrincipalActivity::class.java
                )

                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Logando como Profissional",
                    Toast.LENGTH_SHORT
                ).show()


            }
        }


        btnCriarConta.setOnClickListener {

            val intent = Intent(
                this,
                Cadastro::class.java
            )

            startActivity(intent)
        }
    }
}