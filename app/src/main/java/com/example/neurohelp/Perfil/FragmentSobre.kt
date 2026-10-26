package com.example.neurohelp.Perfil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao

class FragmentSobre : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.perfil_sobre,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtPoliticaPrivacidade = view.findViewById<TextView>(R.id.txtPoliticaPrivacidade)
        val txtTermosUso = view.findViewById<TextView>(R.id.txtTermosUso)
        val txtLgpd = view.findViewById<TextView>(R.id.txtLgpd)

        // Cabeçalho padrão (voltar fecha esse fragment e volta pro anterior na back stack)
        configurarCabecalhoPadrao(view)
        view.findViewById<View>(R.id.itemEmailSobre).setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.email_suporte))))
            } catch (_: android.content.ActivityNotFoundException) {
                android.widget.Toast.makeText(requireContext(), "Nenhum aplicativo de e-mail disponível.", android.widget.Toast.LENGTH_LONG).show()
            }
        }

        // Links do rodapé: abrem uma URL externa no navegador
        txtPoliticaPrivacidade.setOnClickListener {
            abrirLink("https://www.neurohelp.com/politica-de-privacidade")
        }

        txtTermosUso.setOnClickListener {
            abrirLink("https://www.neurohelp.com/termos-de-uso")
        }

        txtLgpd.setOnClickListener {
            abrirLink("https://www.neurohelp.com/lgpd")
        }
    }

    private fun abrirLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (_: android.content.ActivityNotFoundException) {
            android.widget.Toast.makeText(requireContext(), "Nenhum navegador disponível para abrir este link.", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}