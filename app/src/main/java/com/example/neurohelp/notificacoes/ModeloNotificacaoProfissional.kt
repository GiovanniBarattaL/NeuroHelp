package com.example.neurohelp.notificacoes

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** O que o paciente fez; define a mensagem do card e a cor do avatar (Figma). */
enum class TipoNotificacaoProfissional(
    val mensagem: String,
    val mensagemDetalhe: String,
    val corRes: Int
) {
    SOLICITOU_ALTERACAO(
        "O(a) paciente solicitou alterações na consulta.",
        "O(a) paciente encontrou inconformidades na consulta e solicitou alterações.",
        com.example.neurohelp.R.color.notificacao_solicitou
    ),
    PRECISA_REMARCAR(
        "O(a) paciente está precisando remarcar a consulta.",
        "O(a) paciente está precisando remarcar a consulta.",
        com.example.neurohelp.R.color.notificacao_solicitou
    ),
    DESMARCOU(
        "O(a) paciente decidiu desmarcar a consulta.",
        "O(a) paciente decidiu desmarcar a consulta.",
        com.example.neurohelp.R.color.notificacao_desmarcou
    ),
    CONFIRMOU(
        "O(a) paciente confirmou a consulta.",
        "O(a) paciente confirmou a consulta.",
        com.example.neurohelp.R.color.notificacao_confirmou
    )
}

/** Aviso que chega para o profissional (ex.: paciente confirmou, desmarcou ou pediu mudança). */
data class NotificacaoProfissional(
    val id: String,
    val paciente: String,
    val tipo: TipoNotificacaoProfissional,
    /** Texto livre escrito pelo paciente (aparece no pop-up). Pode não existir. */
    val observacao: String?,
    val criadaEm: Date,
    val visto: Boolean
)

/** "Hoje", "Ontem" ou dd/MM. */
fun Date.rotuloDia(): String {
    val agora = Calendar.getInstance()
    val data = Calendar.getInstance().apply { time = this@rotuloDia }
    fun mesmoDia(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    if (mesmoDia(agora, data)) return "Hoje"
    agora.add(Calendar.DAY_OF_YEAR, -1)
    if (mesmoDia(agora, data)) return "Ontem"
    return SimpleDateFormat("dd/MM", Locale("pt", "BR")).format(this)
}

/** HH:mm */
fun Date.rotuloHora(): String = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(this)

/**
 * Fonte temporária (em memória) das notificações do profissional.
 * TODO: trocar por chamada à API quando o backend de notificações existir
 * (mesmo pendente da NotificacoesStore do responsável).
 */
object NotificacoesProfissionalStore {

    private val itens: MutableList<NotificacaoProfissional> = criarExemplos()

    fun todas(): List<NotificacaoProfissional> = itens.sortedByDescending { it.criadaEm }

    fun buscar(id: String): NotificacaoProfissional? = itens.firstOrNull { it.id == id }

    fun marcarComoVista(id: String) {
        val i = itens.indexOfFirst { it.id == id }
        if (i >= 0) itens[i] = itens[i].copy(visto = true)
    }

    fun filtrar(filtro: FiltroNotificacao): List<NotificacaoProfissional> = when (filtro) {
        FiltroNotificacao.NAO_VISTO -> todas().filter { !it.visto }
        FiltroNotificacao.ESSA_SEMANA -> todas().filter { it.criadaEm.estaNaSemanaAtual() }
        FiltroNotificacao.TODOS -> todas()
    }

    private fun criarExemplos(): MutableList<NotificacaoProfissional> {
        fun diasAtras(dias: Int, hora: Int, minuto: Int = 0) = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -dias)
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return mutableListOf(
            NotificacaoProfissional(
                "p1", "Lucas Almeida", TipoNotificacaoProfissional.SOLICITOU_ALTERACAO,
                "Bom dia! O horário da próxima sessão coincide com uma atividade da escola do Lucas. " +
                    "Seria possível mudar para o período da tarde? Ele costuma ficar mais calmo depois do " +
                    "almoço, então acredito que renderia melhor. Obrigado(a) pela compreensão!",
                diasAtras(0, 8, 15), visto = false
            ),
            NotificacaoProfissional(
                "p2", "Marina Costa", TipoNotificacaoProfissional.DESMARCOU,
                "Precisei desmarcar a consulta desta semana por motivo de saúde na família. " +
                    "Assim que possível entro em contato para combinarmos uma nova data.",
                diasAtras(1, 18, 40), visto = false
            ),
            NotificacaoProfissional(
                "p3", "Pedro Henrique Souza", TipoNotificacaoProfissional.CONFIRMOU,
                null, diasAtras(1, 9, 5), visto = false
            ),
            NotificacaoProfissional(
                "p4", "Ana Beatriz Lima", TipoNotificacaoProfissional.PRECISA_REMARCAR,
                "Tenho um compromisso inesperado na data marcada e gostaria de remarcar para a semana seguinte.",
                diasAtras(10, 15, 20), visto = true
            )
        )
    }
}
