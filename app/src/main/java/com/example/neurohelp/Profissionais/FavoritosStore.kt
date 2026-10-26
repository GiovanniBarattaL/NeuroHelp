package com.example.neurohelp.Profissionais

import android.content.Context

/** Favoritos locais da sessão. SessionStore.clear() também os remove ao sair. */
class FavoritosStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
    fun contem(id: Int): Boolean = prefs.getStringSet("favoritos_locais", emptySet())
        .orEmpty().contains(id.toString())
    fun definir(id: Int, favorito: Boolean) {
        val ids = prefs.getStringSet("favoritos_locais", emptySet()).orEmpty().toMutableSet()
        if (favorito) ids.add(id.toString()) else ids.remove(id.toString())
        prefs.edit().putStringSet("favoritos_locais", ids).apply()
    }
}
