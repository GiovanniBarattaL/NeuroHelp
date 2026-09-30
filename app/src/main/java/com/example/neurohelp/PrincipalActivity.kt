package com.example.neurohelp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.neurohelp.Home.FragmentHome
import com.example.neurohelp.Perfil.FragmentPerfil
import com.example.neurohelp.Perfil.FragmentPrivacidade
import com.example.neurohelp.Perfil.FragmentSobre
import com.example.neurohelp.Profissionais.FragmentPerfilProfissional
import com.example.neurohelp.Profissionais.FragmentProfissionais
import com.example.neurohelp.agenda.FragmentAgenda
import com.example.neurohelp.ajuda.FragmentAjuda
import com.google.android.material.bottomnavigation.BottomNavigationView

class PrincipalActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_principal)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Sempre que uma tela fica visível (aba, botão da Home, seta de voltar...)
        // o ícone verde do menu de baixo acompanha a tela que está aberta.
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    abaDoFragment(f)?.let { marcarAba(it) }
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
            R.id.nav_inicio -> FragmentHome()
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
        is FragmentHome -> R.id.nav_inicio

        is FragmentProfissionais,
        is FragmentPerfilProfissional -> R.id.nav_profissionais

        is FragmentAgenda -> R.id.nav_agenda

        is FragmentPerfil,
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
