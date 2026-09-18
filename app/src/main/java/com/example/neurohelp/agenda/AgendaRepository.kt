package com.example.neurohelp.agenda

import java.util.Calendar
import java.util.Date

/**
 * Fonte de dados da agenda.
 *
 * Para ligar no backend basta criar uma implementação (ex.: `AgendaRepositoryApi`)
 * que faça a chamada de rede e devolver as mesmas entidades, e passá-la para o
 * [AgendaViewModel] através da [AgendaViewModelFactory].
 */
interface AgendaRepository {

    /** Consultas do mês. [mes] segue o padrão do [Calendar] (0 = janeiro). */
    suspend fun consultasDoMes(ano: Int, mes: Int): List<Consulta>
}

/**
 * Implementação temporária em memória, usada enquanto a API não está conectada.
 * Mantém a tela funcional com dados de exemplo do mês exibido.
 */
class AgendaRepositoryMemoria : AgendaRepository {

    override suspend fun consultasDoMes(ano: Int, mes: Int): List<Consulta> {
        val ultimoDia = Calendar.getInstance().apply {
            set(ano, mes, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)

        return listOf(
            criarConsulta(ano, mes, 1, 14, 0, Especialidade.PSICOLOGO, "Dr. Marcelo Prado", ModalidadeConsulta.ONLINE),
            criarConsulta(ano, mes, 1, 16, 30, Especialidade.FONOAUDIOLOGO, "Dra. Helena Souza", ModalidadeConsulta.PRESENCIAL, "Clínica Central"),
            criarConsulta(ano, mes, 4, 13, 30, Especialidade.TERAPEUTA_OCUPACIONAL, "Dra. Camila Reis", ModalidadeConsulta.PRESENCIAL, "Clínica Central"),
            criarConsulta(ano, mes, 4, 15, 30, Especialidade.NEUROPEDIATRA, "Dr. Rafael Antunes", ModalidadeConsulta.PRESENCIAL, "Hospital Vida"),
            criarConsulta(ano, mes, minOf(18, ultimoDia), 9, 0, Especialidade.NUTRICIONISTA, "Dra. Paula Lima", ModalidadeConsulta.ONLINE)
        ).sortedBy { it.inicio }
    }

    private fun criarConsulta(
        ano: Int,
        mes: Int,
        dia: Int,
        hora: Int,
        minuto: Int,
        especialidade: Especialidade,
        profissional: String,
        modalidade: ModalidadeConsulta,
        local: String? = null
    ): Consulta {
        val calendario = Calendar.getInstance().apply {
            set(ano, mes, dia, hora, minuto, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Consulta(
            id = "$ano-$mes-$dia-$hora$minuto-${especialidade.name}",
            especialidade = especialidade,
            profissional = profissional,
            inicio = calendario.time,
            modalidade = modalidade,
            local = local
        )
    }
}

fun Date.horaFormatada(): String {
    val calendario = Calendar.getInstance().apply { time = this@horaFormatada }
    return String.format(
        java.util.Locale("pt", "BR"),
        "%02dh%02d",
        calendario.get(Calendar.HOUR_OF_DAY),
        calendario.get(Calendar.MINUTE)
    )
}
