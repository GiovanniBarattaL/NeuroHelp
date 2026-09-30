package com.example.neurohelp.Profissionais

/** Uma avaliação (comentário) deixada para um profissional. */
data class Avaliacao(
    val nome: String,
    val nota: Double,
    val data: String,
    val texto: String
)

/** Dados exibidos na tela de perfil do profissional. */
data class ProfissionalPerfil(
    val id: Int,
    val nome: String,
    val profissao: String,
    val registro: String,
    val atendimento: String,
    val local: String,
    val notaMedia: Double,
    val totalAvaliacoes: Int,
    val abordagem: String,
    val publicoAlvo: String,
    val sobreMim: String,
    val whatsapp: String,
    val avaliacoes: List<Avaliacao>
)

/**
 * Dados de exemplo (iguais ao Figma).
 * TODO: trocar por dados vindos da sua API/Firestore/Room usando o id do profissional.
 */
object ProfissionalMock {

    private const val LOREM =
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen book."

    fun exemplo(id: Int) = ProfissionalPerfil(
        id = id,
        nome = "Dra. XXXXXXXXXXXX",
        profissao = "Psicóloga",
        registro = "CRP 06/123456",
        atendimento = "Atende online e presencial",
        local = "São Paulo - SP",
        notaMedia = 3.5,
        totalAvaliacoes = 97,
        abordagem = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        publicoAlvo = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        sobreMim = LOREM,
        whatsapp = "5511999999999", // TODO: número real do profissional (DDI + DDD + número)
        avaliacoes = listOf(
            Avaliacao("XXXXXXX", 3.5, "2026-04-06", LOREM),
            Avaliacao("XXXXXXX", 3.5, "2026-04-06", LOREM),
            Avaliacao("XXXXXXX", 4.0, "2026-04-02", LOREM),
            Avaliacao("XXXXXXX", 5.0, "2026-03-28", LOREM),
            Avaliacao("XXXXXXX", 3.0, "2026-03-15", LOREM),
            Avaliacao("XXXXXXX", 4.5, "2026-03-09", LOREM)
        )
    )
}
