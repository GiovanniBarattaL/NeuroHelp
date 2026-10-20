package com.example.neurohelp.auth

import org.junit.Assert.*
import org.junit.Test

class CpfTest {
    @Test fun aceitaComESemMascara() {
        listOf("52998224725", "529.982.247-25", "11144477735").forEach { assertTrue(Cpf.valido(it)) }
    }
    @Test fun rejeitaRepetidosDigitosErradosEFormatoInvalido() {
        listOf("", "11111111111", "00000000000", "52998224724", "12345678900", "5299822472", "529982247251", "x52998224725")
            .forEach { assertFalse(Cpf.valido(it)) }
    }
}
