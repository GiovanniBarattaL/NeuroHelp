package com.example.neurohelp.ajuda

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao

class FragmentAjuda : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.perfil_ajuda_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvFaq = view.findViewById<RecyclerView>(R.id.rvFaq)
        val itemEmailSuporte = view.findViewById<LinearLayout>(R.id.itemEmailSuporte)

        // Cabeçalho padrão (voltar fecha esse fragment e volta pro anterior na back stack)
        configurarCabecalhoPadrao(view)

        // TODO: trocar essa lista fixa por dados vindos da sua API/Firestore/Room
        val listaFaq = mutableListOf(
            FaqItem(
                pergunta = "Como faço para agendar uma consulta?",
                resposta = "Acesse a aba Profissionais e use Agendar Consulta para entrar em contato, quando o profissional tiver um contato disponível."
            ),
            FaqItem(
                pergunta = "Como funciona a consulta online?",
                resposta = "O formato, o horário e o meio de atendimento são combinados com o profissional."
            ),
            FaqItem(
                pergunta = "Posso cancelar ou remarcar uma consulta?",
                resposta = "Entre em contato com o profissional para combinar um cancelamento ou uma nova data."
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
                data = Uri.parse("mailto:" + getString(R.string.email_suporte))
            }
            try {
                startActivity(intent)
            } catch (_: android.content.ActivityNotFoundException) {
                android.widget.Toast.makeText(requireContext(), "Nenhum aplicativo disponível para abrir este link.", android.widget.Toast.LENGTH_LONG).show()
            }
        }

    }
}
