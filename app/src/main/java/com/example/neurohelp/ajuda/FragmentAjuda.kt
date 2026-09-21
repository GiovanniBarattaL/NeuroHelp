package com.example.neurohelp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.ajuda.FaqItem

class FragmentAjuda : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_ajuda,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgVoltar = view.findViewById<ImageView>(R.id.imgVoltar)
        val rvFaq = view.findViewById<RecyclerView>(R.id.rvFaq)
        val itemEmailSuporte = view.findViewById<LinearLayout>(R.id.itemEmailSuporte)
        val itemInstagramSuporte = view.findViewById<LinearLayout>(R.id.itemInstagramSuporte)

        // Voltar: fecha esse fragment e volta pro anterior na back stack
        imgVoltar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // TODO: trocar essa lista fixa por dados vindos da sua API/Firestore/Room
        val listaFaq = mutableListOf(
            FaqItem(
                pergunta = "Como faço para agendar uma consulta?",
                resposta = "Acesse a aba Agenda, escolha o profissional desejado e selecione um horário disponível."
            ),
            FaqItem(
                pergunta = "Como funciona a consulta online?",
                resposta = "As consultas online acontecem por videochamada, direto pelo app, no horário marcado."
            ),
            FaqItem(
                pergunta = "Posso cancelar ou remarcar uma consulta?",
                resposta = "Sim, você pode cancelar ou remarcar até 24h antes pela tela de detalhes da consulta."
            ),
            FaqItem(
                pergunta = "Profissão",
                resposta = "Selecione a profissão do especialista que você procura para ver dúvidas específicas dessa área."
            )
        )

        rvFaq.layoutManager = LinearLayoutManager(requireContext())
        rvFaq.adapter = FaqAdapter(listaFaq)

        // Abre o app de e-mail com o destinatário já preenchido
        itemEmailSuporte.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:neurohelp.suporte@gmail.com")
            }
            startActivity(intent)
        }

        // Abre o perfil do Instagram no navegador/app
        itemInstagramSuporte.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/neurohelp"))
            startActivity(intent)
        }
    }
}
