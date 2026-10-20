package com.example.neurohelp.Login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neurohelp.R
import com.example.neurohelp.auth.*

class RedefinirSenha : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redefinir_senha)
        val email = intent.getStringExtra("email").orEmpty()
        if (email.isBlank()) { finish(); return }
        findViewById<TextView>(R.id.txtDestinoCodigo).text = "Se o e-mail estiver cadastrado, você receberá um código em $email. Verifique o spam. O código expira em 15 minutos. Para solicitar outro, aguarde ao menos 60 segundos. Há limites de solicitações por período."
        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        val senha = findViewById<EditText>(R.id.etNovaSenha)
        val confirmar = findViewById<EditText>(R.id.etConfirmarNovaSenha)
        val codigo = findViewById<EditText>(R.id.etCodigoSenha)
        // Código e senhas não entram no estado salvo, extras, logs ou preferências.
        listOf(senha, confirmar, codigo).forEach { it.isSaveEnabled = false }
        val button = findViewById<Button>(R.id.btnRedefinirSenha)
        bindSubmission(button) {
            senha.text.clear(); confirmar.text.clear(); codigo.text.clear()
            Toast.makeText(this, "Senha alterada. Entre novamente em seus dispositivos.", Toast.LENGTH_LONG).show()
            goLogin()
        }
        button.setOnClickListener {
            val pass = senha.text.toString()
            val strong = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9\\s]).{8,}$").matches(pass)
                    && pass.toByteArray(Charsets.UTF_8).size <= 72
            val validCode = validate(R.id.etCodigoSenha, Regex("[0-9]{8}").matches(codigo.text.toString()), "Informe o código de 8 dígitos")
            val validPass = validate(R.id.etNovaSenha, strong, "Use ao menos 8 caracteres: maiúscula, minúscula, número e símbolo; até 72 bytes")
            val validConfirm = validate(R.id.etConfirmarNovaSenha, pass == confirmar.text.toString(), "As senhas não coincidem")
            if (!validCode || !validPass || !validConfirm) return@setOnClickListener
            val value = codigo.text.toString()
            submit(button) { it.resetPassword(email, value, pass) }
        }
    }
}
