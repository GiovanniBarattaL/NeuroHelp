package com.example.neurohelp.agenda

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.neurohelp.R
import java.util.Calendar
import java.util.Date

enum class Especialidade(
    @StringRes val nomeRes: Int,
    @ColorRes val corRes: Int
) {
    FONOAUDIOLOGO(R.string.agenda_fonoaudiologo, R.color.prof_fonoaudiologo),
    TERAPEUTA_OCUPACIONAL(R.string.agenda_terapeuta_ocupacional, R.color.prof_terapeuta_ocupacional),
    PSIQUIATRA(R.string.agenda_psiquiatra, R.color.prof_psiquiatra),
    PSICOLOGO(R.string.agenda_psicologo, R.color.prof_psicologo),
    NEUROPEDIATRA(R.string.agenda_neuropediatra, R.color.prof_neuropediatra),
    NUTRICIONISTA(R.string.agenda_nutricionista, R.color.prof_nutricionista)
}

enum class ModalidadeConsulta(@StringRes val nomeRes: Int) {
    ONLINE(R.string.agenda_online),
    PRESENCIAL(R.string.agenda_presencial)
}


data class Consulta(
    val id: String,
    val especialidade: Especialidade,
    val profissional: String,
    val inicio: Date,
    val modalidade: ModalidadeConsulta,
    val local: String? = null
)


data class DiaCalendario(
    val data: Date,
    val dia: Int,
    val doMesExibido: Boolean,
    val temConsulta: Boolean,
    val selecionado: Boolean
)

/** Itens da lista agrupada: cabeçalho de dia + consultas daquele dia. */
sealed class ItemAgenda {
    data class Secao(val titulo: String) : ItemAgenda()
    data class Item(val consulta: Consulta) : ItemAgenda()
}

fun Date.mesmoDiaQue(outra: Date): Boolean {
    val a = Calendar.getInstance().apply { time = this@mesmoDiaQue }
    val b = Calendar.getInstance().apply { time = outra }
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

fun Date.inicioDoDia(): Date {
    val calendario = Calendar.getInstance().apply {
        time = this@inicioDoDia
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendario.time
}
