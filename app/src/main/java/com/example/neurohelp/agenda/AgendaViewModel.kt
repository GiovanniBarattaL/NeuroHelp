package com.example.neurohelp.agenda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AgendaViewModel(
    private val repository: AgendaRepository = AgendaRepositoryMemoria()
) : ViewModel() {

    private val localePtBr = Locale("pt", "BR")

    private val mesExibido: Calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }

    private val _tituloMes = MutableLiveData<String>()
    val tituloMes: LiveData<String> = _tituloMes

    private val _ano = MutableLiveData<String>()
    val ano: LiveData<String> = _ano

    private val _dias = MutableLiveData<List<DiaCalendario>>()
    val dias: LiveData<List<DiaCalendario>> = _dias

    private val _itens = MutableLiveData<List<ItemAgenda>>()
    val itens: LiveData<List<ItemAgenda>> = _itens

    private val _proximaConsulta = MutableLiveData<Consulta?>()
    val proximaConsulta: LiveData<Consulta?> = _proximaConsulta

    private val _carregando = MutableLiveData(false)
    val carregando: LiveData<Boolean> = _carregando

    private val _erro = MutableLiveData<Boolean>()
    val erro: LiveData<Boolean> = _erro

    private var consultasDoMes: List<Consulta> = emptyList()
    private var diaSelecionado: Date = Date().inicioDoDia()

    init {
        carregarMes()
    }

    fun mesAnterior() {
        mesExibido.add(Calendar.MONTH, -1)
        diaSelecionado = primeiroDiaDoMesExibido()
        carregarMes()
    }

    fun proximoMes() {
        mesExibido.add(Calendar.MONTH, 1)
        diaSelecionado = primeiroDiaDoMesExibido()
        carregarMes()
    }

    fun selecionarDia(data: Date) {
        diaSelecionado = data.inicioDoDia()
        val calendarioDia = Calendar.getInstance().apply { time = diaSelecionado }
        val mudouDeMes = calendarioDia.get(Calendar.MONTH) != mesExibido.get(Calendar.MONTH) ||
            calendarioDia.get(Calendar.YEAR) != mesExibido.get(Calendar.YEAR)

        if (mudouDeMes) {
            mesExibido.time = diaSelecionado
            mesExibido.set(Calendar.DAY_OF_MONTH, 1)
            carregarMes()
        } else {
            publicar()
        }
    }

    fun recarregar() = carregarMes()

    private fun carregarMes() {
        _carregando.value = true
        _erro.value = false
        val ano = mesExibido.get(Calendar.YEAR)
        val mes = mesExibido.get(Calendar.MONTH)

        viewModelScope.launch {
            try {
                consultasDoMes = repository.consultasDoMes(ano, mes).sortedBy { it.inicio }
                _erro.value = false
            } catch (e: Exception) {
                consultasDoMes = emptyList()
                _erro.value = true
            }
            _carregando.value = false
            publicar()
        }
    }

    private fun publicar() {
        _tituloMes.value = formatar("MMMM yyyy", mesExibido.time)
        _ano.value = mesExibido.get(Calendar.YEAR).toString()
        _dias.value = montarDias()
        _itens.value = montarItens()
        _proximaConsulta.value = consultasDoMes.firstOrNull { !it.inicio.before(Date()) }
            ?: consultasDoMes.firstOrNull()
    }

    /** Semana (domingo a sábado) que contém o dia selecionado. */
    private fun montarDias(): List<DiaCalendario> {
        val cursor = Calendar.getInstance().apply {
            time = diaSelecionado
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }

        return (0 until 7).map {
            val data = cursor.time
            val dia = DiaCalendario(
                data = data,
                dia = cursor.get(Calendar.DAY_OF_MONTH),
                doMesExibido = cursor.get(Calendar.MONTH) == mesExibido.get(Calendar.MONTH),
                temConsulta = consultasDoMes.any { consulta -> consulta.inicio.mesmoDiaQue(data) },
                selecionado = data.mesmoDiaQue(diaSelecionado)
            )
            cursor.add(Calendar.DAY_OF_MONTH, 1)
            dia
        }
    }

    /** Consultas a partir do dia selecionado, agrupadas por dia. */
    private fun montarItens(): List<ItemAgenda> {
        val itens = mutableListOf<ItemAgenda>()
        consultasDoMes
            .filter { !it.inicio.inicioDoDia().before(diaSelecionado) }
            .groupBy { it.inicio.inicioDoDia() }
            .toSortedMap()
            .forEach { (dia, consultas) ->
                itens += ItemAgenda.Secao(
                    "Consultas do dia ${formatar("dd", dia)} de ${formatar("MMMM", dia)}"
                )
                consultas.sortedBy { it.inicio }.forEach { itens += ItemAgenda.Item(it) }
            }
        return itens
    }

    private fun primeiroDiaDoMesExibido(): Date =
        Calendar.getInstance().apply {
            time = mesExibido.time
            set(Calendar.DAY_OF_MONTH, 1)
        }.time.inicioDoDia()

    private fun formatar(padrao: String, data: Date): String =
        SimpleDateFormat(padrao, localePtBr).format(data)
            .replaceFirstChar { it.uppercase(localePtBr) }
}

/** Permite injetar a implementação real de [AgendaRepository] quando a API existir. */
class AgendaViewModelFactory(
    private val repository: AgendaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AgendaViewModel(repository) as T
}
