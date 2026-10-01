package com.example.neurohelp.Home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.neurohelp.Perfil.FragmentPerfil
import com.example.neurohelp.PrincipalActivity
import com.example.neurohelp.R
import com.example.neurohelp.notificacoes.abrirNotificacoes

class FragmentHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.home_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val imgPerfil =
            view.findViewById<ImageView>(R.id.imgPerfil)

        imgPerfil.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    FragmentPerfil()
                )
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<ImageView>(R.id.imgNotificacao).setOnClickListener {
            abrirNotificacoes()
        }

        // Encontrar profissionais -> aba Profissionais
        configurarAtalho(view.findViewById(R.id.btnProfissionais)) {
            irParaAba(R.id.nav_profissionais)
        }

        // Agendar consulta -> aba Agenda
        configurarAtalho(view.findViewById(R.id.btnAgendar)) {
            irParaAba(R.id.nav_agenda)
        }

        // Aprendizagem -> página de aprendizagem do site
        configurarAtalho(view.findViewById(R.id.btnAprendizagem)) {
            abrirAprendizagem()
        }
    }

    /** O clique vale para o ícone e também para o texto embaixo dele. */
    private fun configurarAtalho(icone: View, acao: () -> Unit) {
        icone.setOnClickListener { acao() }
        (icone.parent as? View)?.setOnClickListener { acao() }
    }

    /** Troca de aba pelo menu de baixo, então o ícone verde acompanha. */
    private fun irParaAba(itemId: Int) {
        (requireActivity() as? PrincipalActivity)?.irParaAba(itemId)
    }

    private fun abrirAprendizagem() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, URL_APRENDIZAGEM.toUri()))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                R.string.home_erro_abrir_link,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private companion object {
        const val URL_APRENDIZAGEM =
            "https://espectro-care.onrender.com/pages/aprendizagem.html"
    }
}
