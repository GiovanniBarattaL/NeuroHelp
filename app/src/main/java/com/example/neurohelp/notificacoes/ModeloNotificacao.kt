package com.example.neurohelp.notificacoes

import com.example.neurohelp.agenda.Consulta
import com.example.neurohelp.agenda.Especialidade
import com.example.neurohelp.agenda.ModalidadeConsulta
import java.util.Calendar
import java.util.Date

data class Notificacao(
    val id: String,
    val consulta: Consulta,
    val mensagem: String,
    val criadaEm: Date,
    val visto: Boolean
)

enum class FiltroNotificacao { NAO_VISTO, ESSA_SEMANA, TODOS }

fun Date.estaNaSemanaAtual(): Boolean {
    val agora = Calendar.getInstance()
    val data = Calendar.getInstance().apply { time = this@estaNaSemanaAtual }
    return agora.get(Calendar.YEAR) == data.get(Calendar.YEAR) &&
        agora.get(Calendar.WEEK_OF_YEAR) == data.get(Calendar.WEEK_OF_YEAR)
}

/**
 * Fonte temporária (em memória) das notificações.
 * TODO: trocar por chamada à API quando o backend de notificações existir.
 */
object NotificacoesStore {

    private val itens: MutableList<Notificacao> = criarExemplos()

    fun todas(): List<Notificacao> = itens.sortedByDescending { it.criadaEm }

    fun marcarComoVista(id: String) {
        val i = itens.indexOfFirst { it.id == id }
        if (i >= 0) itens[i] = itens[i].copy(visto = true)
    }

    fun filtrar(filtro: FiltroNotificacao): List<Notificacao> = when (filtro) {
        FiltroNotificacao.NAO_VISTO -> todas().filter { !it.visto }
        FiltroNotificacao.ESSA_SEMANA -> todas().filter { it.criadaEm.estaNaSemanaAtual() }
        FiltroNotificacao.TODOS -> todas()
    }

    private fun criarExemplos(): MutableList<Notificacao> {
        fun diasAtras(dias: Int, hora: Int = 9) = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -dias)
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, 0)
        }.time

        fun hoje(hora: Int, minuto: Int) = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val psicologo = Consulta(
            id = "notif-consulta-1",
            especialidade = Especialidade.PSICOLOGO,
            profissional = "Dr. Marcelo Prado",
            inicio = hoje(14, 0),
            modalidade = ModalidadeConsulta.ONLINE,
            crp = "CRP 06/123456",
            observacoes = "Acompanhamento psicológico semanal",
            foco = "ansiedade e regulação emocional"
        )
        val fono = Consulta(
            id = "notif-consulta-2",
            especialidade = Especialidade.FONOAUDIOLOGO,
            profissional = "Dra. Helena Souza",
            inicio = diasAtras(-2, 16),
            modalidade = ModalidadeConsulta.PRESENCIAL,
            local = "Clínica Central"
        )
        val nutri = Consulta(
            id = "notif-consulta-3",
            especialidade = Especialidade.NUTRICIONISTA,
            profissional = "Dra. Paula Lima",
            inicio = diasAtras(14, 9),
            modalidade = ModalidadeConsulta.ONLINE
        )

        return mutableListOf(
            Notificacao("n1", psicologo, "Sua consulta é hoje! Confirme sua presença aqui…", hoje(8, 0), visto = false),
            Notificacao("n2", fono, "Sua consulta está chegando. Confirme sua presença aqui…", diasAtras(1), visto = true),
            Notificacao("n3", nutri, "Sua consulta foi realizada. Veja os detalhes aqui…", diasAtras(14), visto = true)
        )
    }
}
