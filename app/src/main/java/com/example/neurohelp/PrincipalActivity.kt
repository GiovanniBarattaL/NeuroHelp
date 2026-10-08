package com.example.neurohelp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.neurohelp.Home.FragmentHome
import com.example.neurohelp.Home.FragmentHomeProfissional
import com.example.neurohelp.auth.PapelUsuario
import com.example.neurohelp.Perfil.FragmentPerfil
import com.example.neurohelp.Perfil.FragmentPrivacidade
import com.example.neurohelp.Perfil.FragmentSobre
import com.example.neurohelp.Profissionais.FragmentPerfilProfissional
import com.example.neurohelp.Profissionais.FragmentProfissionais
import com.example.neurohelp.agenda.FragmentAgenda
import com.example.neurohelp.agenda.FragmentAgendarConsulta
import com.example.neurohelp.agenda.FragmentConsultaDetalhe
import com.example.neurohelp.agenda.FragmentSelecionarPaciente
import com.example.neurohelp.notificacoes.FragmentNotificacoes
import com.example.neurohelp.ajuda.FragmentAjuda
import com.example.neurohelp.auth.papelUsuario
import com.google.android.material.bottomnavigation.BottomNavigationView

class PrincipalActivity : AppCompatActivity() {

    private val expiryHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val expiryCheck = object : Runnable {
        override fun run() {
            if (com.example.neurohelp.auth.SessionStore(this@PrincipalActivity).token() == null) {
                com.example.neurohelp.auth.goLoginFromPrincipal(this@PrincipalActivity)
            } else expiryHandler.postDelayed(this, 1000)
        }
    }
    override fun onResume() {
        super.onResume()
        expiryHandler.post(expiryCheck)
    }
    override fun onPause() {
        expiryHandler.removeCallbacks(expiryCheck)
        super.onPause()
    }

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (com.example.neurohelp.auth.SessionStore(this).token() == null) {
            com.example.neurohelp.auth.goLoginFromPrincipal(this)
            return
        }
        setContentView(R.layout.activity_principal)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Sempre que uma tela fica visível (aba, botão da Home, seta de voltar...)
        // o ícone verde do menu de baixo acompanha a tela que está aberta.
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    abaDoFragment(f)?.let { marcarAba(it) }
                    f.view?.findViewById<android.widget.ImageView>(R.id.imgPerfil)?.let {
                        com.example.neurohelp.auth.ProfilePhoto.own(f, it)
                    }
                }
            },
            false
        )

        if (savedInstanceState == null) {
            abrirAba(R.id.nav_inicio)
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            abrirAba(item.itemId)
        }

        // Tocar na aba que já está marcada volta para a tela inicial dela
        // (ex.: estando em Perfil > Sobre, tocar em "Perfil" volta ao Perfil).
        bottomNavigation.setOnItemReselectedListener { item ->
            if (supportFragmentManager.backStackEntryCount > 0) {
                abrirAba(item.itemId)
            }
        }
    }

    /** Usado pelas outras telas (ex.: atalhos da Home) para trocar de aba. */
    fun irParaAba(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }

    /** Abre a tela raiz da aba, limpando as telas empilhadas da aba anterior. */
    private fun abrirAba(itemId: Int): Boolean {

        val fragment: Fragment = when (itemId) {
            // Cada perfil tem a sua Home
            R.id.nav_inicio ->
                if (papelUsuario() == PapelUsuario.PROFISSIONAL) FragmentHomeProfissional()
                else FragmentHome()
            R.id.nav_profissionais -> FragmentProfissionais()
            R.id.nav_agenda -> FragmentAgenda()
            R.id.nav_perfil -> FragmentPerfil()
            else -> return false
        }

        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        return true
    }

    /** Qual aba do menu de baixo "é dona" de cada tela. */
    private fun abaDoFragment(fragment: Fragment): Int? = when (fragment) {
        is FragmentHome,
        is FragmentHomeProfissional -> R.id.nav_inicio

        is FragmentProfissionais,
        is FragmentPerfilProfissional -> R.id.nav_profissionais

        is FragmentAgenda,
        is FragmentConsultaDetalhe,
        is FragmentSelecionarPaciente,
        is FragmentAgendarConsulta -> R.id.nav_agenda

        is FragmentPerfil,
        is FragmentNotificacoes,
        is FragmentSobre,
        is FragmentAjuda,
        is FragmentPrivacidade -> R.id.nav_perfil

        else -> null
    }

    /** Marca a aba sem disparar o listener (não recarrega a tela). */
    private fun marcarAba(itemId: Int) {
        bottomNavigation.menu.findItem(itemId)?.isChecked = true
    }
}
