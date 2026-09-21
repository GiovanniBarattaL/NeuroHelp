package com.example.neurohelp.ajuda

/**
 * Modelo de uma pergunta frequente.
 * "expandida" controla se a resposta está visível na lista.
 * Depois você pode trocar a lista fixa em FragmentAjuda por dados vindos
 * de uma API/Firestore/Room, sem precisar mexer no adapter.
 */
data class FaqItem(
    val pergunta: String,
    val resposta: String,
    var expandida: Boolean = false
)