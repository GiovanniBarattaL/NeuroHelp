package com.example.neurohelp.auth

/** Validação matemática; não confirma existência na Receita nem titularidade. */
object Cpf {
    fun valido(valor: String): Boolean {
        if (!Regex("[0-9.\\-\\s]+").matches(valor)) return false
        val cpf = valor.replace(Regex("[^0-9]"), "")
        if (cpf.length != 11 || cpf.toSet().size == 1) return false
        for (tamanho in 9..10) {
            val soma = (0 until tamanho).sumOf { (cpf[it] - '0') * (tamanho + 1 - it) }
            val digito = (soma * 10) % 11 % 10
            if (digito != cpf[tamanho] - '0') return false
        }
        return true
    }
}
