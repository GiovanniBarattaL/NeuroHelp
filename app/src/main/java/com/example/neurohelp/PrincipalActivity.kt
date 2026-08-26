package com.example.neurohelp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class PrincipalActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_principal)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            abrirFragment(FragmentHome())
        }

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_inicio -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, FragmentHome())
                        .commit()
                    true
                }

                R.id.nav_profissionais -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, FragmentProfissionais())
                        .commit()
                    true
                }

                R.id.nav_agenda -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, FragmentAgenda())
                        .commit()
                    true
                }

                R.id.nav_comunidade -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, FragmentComunidade())
                        .commit()
                    true
                }

                R.id.nav_perfil -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, FragmentPerfil())
                        .commit()
                    true
                }

                else -> false
            }}}

    private fun abrirFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}