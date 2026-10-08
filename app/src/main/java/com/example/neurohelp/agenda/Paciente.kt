package com.example.neurohelp.agenda

/** Responsável pelo paciente (aparece em "Sobre o(a) Responsável"). */
data class ResponsavelPaciente(
    val nome: String,
    val cpf: String,
    val sexo: String,
    val nascimento: String
)

/** Paciente atendido pelo profissional. */
data class Paciente(
    val id: String,
    val nome: String,
    val cpf: String,
    val responsavel: ResponsavelPaciente? = null,
    val sobre: String? = null
)

/**
 * Pacientes de exemplo.
 * TODO: trocar por dados da API (lista dos pacientes do profissional + busca por CPF).
 */
object PacientesMock {

    private const val SOBRE = "Lorem Ipsum is simply dummy text of the printing and typesetting industry."

    val todos: List<Paciente> = listOf(
        Paciente("p1", "Lucas Almeida", "111.222.333-44",
            ResponsavelPaciente("Marina Almeida", "666.777.888-99", "Feminino", "14/03/1985"), SOBRE),
        Paciente("p2", "Sofia Martins", "222.333.444-55",
            ResponsavelPaciente("Carlos Martins", "777.888.999-00", "Masculino", "02/09/1983"), SOBRE),
        Paciente("p3", "Miguel Rocha", "333.444.555-66",
            ResponsavelPaciente("Fernanda Rocha", "888.999.000-11", "Feminino", "21/11/1988"), SOBRE),
        Paciente("p4", "Helena Costa", "444.555.666-77",
            ResponsavelPaciente("Ricardo Costa", "999.000.111-22", "Masculino", "30/06/1980"), SOBRE),
        Paciente("p5", "Davi Ferreira", "555.666.777-88",
            ResponsavelPaciente("Patrícia Ferreira", "000.111.222-33", "Feminino", "08/01/1990"), SOBRE)
    )

    fun porId(id: String?): Paciente? = todos.firstOrNull { it.id == id }

    fun porNome(nome: String?): Paciente? =
        if (nome.isNullOrBlank()) null else todos.firstOrNull { it.nome.equals(nome, ignoreCase = true) }

    /** Busca pelo CPF (com ou sem pontuação); se o texto tiver letras, busca pelo nome. */
    fun buscar(termo: String): List<Paciente> {
        val texto = termo.trim()
        if (texto.isEmpty()) return todos

        val digitos = texto.filter { it.isDigit() }
        return if (digitos.isNotEmpty() && texto.none { it.isLetter() }) {
            todos.filter { p -> p.cpf.filter { it.isDigit() }.contains(digitos) }
        } else {
            todos.filter { it.nome.contains(texto, ignoreCase = true) }
        }
    }
}

/** Formata até 11 dígitos como 000.000.000-00 enquanto o usuário digita. */
fun formatarCpf(digitos: String): String {
    val d = digitos.filter { it.isDigit() }.take(11)
    val sb = StringBuilder()
    d.forEachIndexed { i, c ->
        when (i) {
            3, 6 -> sb.append('.')
            9 -> sb.append('-')
        }
        sb.append(c)
    }
    return sb.toString()
}
