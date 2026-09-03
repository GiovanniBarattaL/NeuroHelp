package com.example.neurohelp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentSobre : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragmentsobre,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgVoltar = view.findViewById<ImageView>(R.id.imgVoltar)
        val txtPoliticaPrivacidade = view.findViewById<TextView>(R.id.txtPoliticaPrivacidade)
        val txtTermosUso = view.findViewById<TextView>(R.id.txtTermosUso)
        val txtLgpd = view.findViewById<TextView>(R.id.txtLgpd)

        // Botão voltar: fecha esse fragment e volta pro anterior na back stack
        imgVoltar.setOnClickListener {
            parentFragmentManager.popBackStack()
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
        startActivity(intent)
    }
}