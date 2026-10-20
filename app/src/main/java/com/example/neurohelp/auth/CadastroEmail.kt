package com.example.neurohelp.auth

import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class EtapaCadastro { DADOS, CODIGO, CONCLUIDO }
data class CadastroEmailState(
    val etapa: EtapaCadastro = EtapaCadastro.DADOS,
    val email: String = "", val ocupado: Boolean = false, val solicitado: Boolean = false,
    val proximoEnvio: Long = 0, val mensagem: String = "", val erro: Boolean = false
)

/** Mantém a operação durante rotação, sem Activity, SavedStateHandle, Intent ou persistência de senhas. */
class CadastroEmailViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(CadastroEmailState())
    val state = mutableState.asStateFlow()
    private var campos: Map<String, String> = emptyMap()
    private var profissional = false
    fun iniciar(api: ApiService, profissional: Boolean, dados: Map<String, String>) {
        if (state.value.ocupado || state.value.etapa != EtapaCadastro.DADOS) return
        this.profissional = profissional; campos = dados.toMap()
        mutableState.value = CadastroEmailState(etapa = EtapaCadastro.CODIGO, email = dados["email"].orEmpty())
        reenviar(api)
    }
    fun reenviar(api: ApiService) {
        val atual = state.value
        if (atual.ocupado || atual.etapa != EtapaCadastro.CODIGO || System.currentTimeMillis() < atual.proximoEnvio) return
        executar {
            api.requestRegistrationEmail(atual.email)
            mutableState.value = state.value.copy(solicitado = true, proximoEnvio = System.currentTimeMillis() + 60000,
                mensagem = "Se o e-mail puder ser usado em um novo cadastro, você receberá um código. Confira também o spam. Se já possui uma conta, faça login.")
        }
    }
    fun confirmar(api: ApiService, codigo: String) {
        if (state.value.ocupado || !state.value.solicitado || state.value.etapa != EtapaCadastro.CODIGO) return
        if (!Regex("[0-9]{8}").matches(codigo)) {
            mutableState.value = state.value.copy(mensagem = "Informe os 8 dígitos do código.", erro = true); return
        }
        executar {
            api.register(profissional, campos + ("codigoEmail" to codigo))
            campos = emptyMap()
            mutableState.value = state.value.copy(etapa = EtapaCadastro.CONCLUIDO)
        }
    }
    fun corrigir() {
        if (state.value.ocupado) return
        campos = emptyMap(); mutableState.value = CadastroEmailState()
    }
    fun consumirSucesso() {
        if (state.value.etapa == EtapaCadastro.CONCLUIDO) {
            campos = emptyMap(); mutableState.value = CadastroEmailState()
        }
    }
    private fun executar(acao: suspend () -> Unit) {
        mutableState.value = state.value.copy(ocupado = true, mensagem = "Aguarde…", erro = false)
        viewModelScope.launch {
            try { acao() }
            catch (e: CancellationException) { throw e }
            catch (e: ApiException) {
                mutableState.value = state.value.copy(mensagem = when (e.status) {
                    429 -> "Muitas tentativas. Aguarde 15 minutos antes de tentar novamente."
                    409 -> "Não foi possível cadastrar com esses dados. Se já possui uma conta, faça login; caso contrário, corrija os dados."
                    else -> e.message.orEmpty()
                }, erro = true, proximoEnvio = if (e.status == 429) System.currentTimeMillis() + 900000 else state.value.proximoEnvio)
            } catch (_: java.io.IOException) {
                mutableState.value = state.value.copy(mensagem = "Falha de conexão. Tente novamente; se a conta já tiver sido criada, faça login.", erro = true)
            } catch (_: Exception) {
                mutableState.value = state.value.copy(mensagem = "Não foi possível concluir a operação. Tente novamente.", erro = true)
            } finally { mutableState.value = state.value.copy(ocupado = false) }
        }
    }
    override fun onCleared() { campos = emptyMap() }
}

/** Painel compartilhado pelos dois formulários; o código nunca é salvo no estado das views. */
fun AppCompatActivity.bindCadastroEmail(button: Button) {
    val model = ViewModelProvider(this)[CadastroEmailViewModel::class.java]
    val label = button.text
    val parent = button.parent as ViewGroup
    val panel = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        val margem = (24 * resources.displayMetrics.density).toInt()
        setPadding(margem, margem / 2, margem, margem)
        visibility = View.GONE
    }
    val titulo = TextView(this).apply { text = "Confirme seu e-mail"; textSize = 20f }
    val descricao = TextView(this)
    val codigo = EditText(this).apply {
        hint = "Código de confirmação (8 dígitos)"
        contentDescription = "Código de confirmação de e-mail, 8 dígitos"
        inputType = InputType.TYPE_CLASS_NUMBER
        filters = arrayOf(InputFilter.LengthFilter(8), InputFilter { source, start, end, _, _, _ ->
            val trecho = source.subSequence(start, end).toString()
            if (trecho.all { it in '0'..'9' }) null else trecho.filter { it in '0'..'9' }
        })
        isSaveEnabled = false
    }
    val feedback = TextView(this).apply { accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE }
    val confirmar = Button(this).apply { text = "Confirmar e cadastrar" }
    val reenviar = Button(this)
    val corrigir = Button(this).apply { text = "Corrigir e-mail ou dados" }
    for (view in listOf(titulo, descricao, codigo, feedback, confirmar, reenviar, corrigir))
        panel.addView(view, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    parent.addView(panel, parent.indexOfChild(button) + 1)
    fun api() = ApiService(SessionStore(applicationContext))
    confirmar.setOnClickListener { model.confirmar(api(), codigo.text.toString()) }
    reenviar.setOnClickListener { model.reenviar(api()) }
    corrigir.setOnClickListener { codigo.text.clear(); model.corrigir(); findViewById<EditText>(com.example.neurohelp.R.id.etEmail).requestFocus() }
    fun editaveis(view: View): List<View> = when {
        view === panel -> emptyList()
        view is EditText || view is CheckBox -> listOf(view)
        view is ViewGroup -> view.children.flatMap { editaveis(it) }.toList()
        else -> emptyList()
    }
    val dados = editaveis(button.rootView)
    var etapaAnterior = EtapaCadastro.DADOS
    fun render(state: CadastroEmailState) {
        val pendente = state.etapa == EtapaCadastro.CODIGO
        panel.visibility = if (pendente) View.VISIBLE else View.GONE
        button.isEnabled = state.etapa == EtapaCadastro.DADOS && !state.ocupado
        button.text = if (pendente) "Confirme seu e-mail abaixo" else label
        dados.forEach { it.isEnabled = !pendente && !state.ocupado }
        descricao.text = "Informe o código recebido em ${state.email}. Ele vale por 15 minutos."
        feedback.text = state.mensagem
        feedback.setTextColor(if (state.erro) android.graphics.Color.rgb(160, 25, 35) else android.graphics.Color.DKGRAY)
        confirmar.isEnabled = !state.ocupado && state.solicitado
        codigo.isEnabled = !state.ocupado && state.solicitado
        corrigir.isEnabled = !state.ocupado
        val segundos = ((state.proximoEnvio - System.currentTimeMillis() + 999) / 1000).coerceAtLeast(0)
        reenviar.isEnabled = !state.ocupado && segundos == 0L
        reenviar.text = if (segundos > 0) "Reenviar em ${segundos}s" else "Reenviar código"
        if (pendente && etapaAnterior != EtapaCadastro.CODIGO) panel.post { panel.requestRectangleOnScreen(android.graphics.Rect(0, 0, panel.width, panel.height), false) }
        etapaAnterior = state.etapa
    }
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                model.state.collect { state ->
                    render(state)
                    if (state.etapa == EtapaCadastro.CONCLUIDO) {
                        codigo.text.clear(); model.consumirSucesso()
                        Toast.makeText(this@bindCadastroEmail, "E-mail confirmado e cadastro realizado. Entre com seu e-mail e senha.", Toast.LENGTH_LONG).show()
                        goLogin()
                    }
                }
            }
            launch { while (isActive) { delay(1000); render(model.state.value) } }
        }
    }
}

fun AppCompatActivity.iniciarConfirmacaoCadastro(profissional: Boolean, fields: Map<String, String>) {
    ViewModelProvider(this)[CadastroEmailViewModel::class.java].iniciar(ApiService(SessionStore(applicationContext)), profissional, fields)
}
