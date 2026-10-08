package com.example.neurohelp.Profissionais

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.neurohelp.R

/**
 * "Agendar consulta": abre o WhatsApp do profissional com a mensagem já escrita.
 * Usado na lista de Profissionais e no perfil do profissional.
 *
 * @param whatsapp número com DDI + DDD + número (ex.: 5511999999999)
 */
fun Fragment.abrirWhatsAppProfissional(nome: String, whatsapp: String) {
    val mensagem = getString(R.string.profissional_mensagem_whatsapp, nome)
    val uri = Uri.parse("https://wa.me/$whatsapp?text=${Uri.encode(mensagem)}")

    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            requireContext(),
            getString(R.string.profissional_whatsapp_indisponivel),
            Toast.LENGTH_SHORT
        ).show()
    }
}
