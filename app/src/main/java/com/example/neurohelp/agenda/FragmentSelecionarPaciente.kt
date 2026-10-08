package com.example.neurohelp.agenda

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao

/**
 * Primeira etapa do agendamento do profissional: escolher o paciente (busca por CPF).
 * Aberta pelo botão "+" da Agenda; "Confirmar e próxima etapa" leva para [FragmentAgendarConsulta].
 */
class FragmentSelecionarPaciente : Fragment() {

    private var selecionadoId: String? = null

    private lateinit var adapter: PacientesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.agenda_selecionar_paciente, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarCabecalhoPadrao(view)

        selecionadoId = savedInstanceState?.getString(ESTADO_SELECIONADO)

        val edtBusca = view.findViewById<EditText>(R.id.edtBuscaPaciente)
        val card = view.findViewById<View>(R.id.cardPacientes)
        val txtSem = view.findViewById<TextView>(R.id.txtSemPacientes)

        adapter = PacientesAdapter(selecionadoId) { paciente -> selecionadoId = paciente.id }
        view.findViewById<RecyclerView>(R.id.rvPacientes).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragmentSelecionarPaciente.adapter
        }

        fun atualizarLista() {
            // TODO: buscar na API (pacientes do profissional filtrados pelo CPF)
            val lista = PacientesMock.buscar(edtBusca.text.toString())
            adapter.submit(lista)
            card.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
            txtSem.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }

        edtBusca.addTextChangedListener(object : TextWatcher {
            private var formatando = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (formatando || s == null) return
                // Só números viram CPF com pontos e traço; texto com letras fica como está
                if (s.isNotEmpty() && s.none { it.isLetter() }) {
                    val formatado = formatarCpf(s.toString())
                    if (formatado != s.toString()) {
                        formatando = true
                        s.replace(0, s.length, formatado)
                        formatando = false
                    }
                }
                atualizarLista()
            }
        })

        atualizarLista()

        view.findViewById<View>(R.id.btnConfirmarPaciente).setOnClickListener {
            val paciente = PacientesMock.porId(selecionadoId)
            if (paciente == null) {
                Toast.makeText(requireContext(), "Selecione um paciente para continuar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentAgendarConsulta.novaConsulta(paciente.id))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ESTADO_SELECIONADO, selecionadoId)
    }

    private companion object {
        const val ESTADO_SELECIONADO = "paciente_selecionado"
    }
}

/** Botão "+" da Agenda do profissional: abre a seleção de paciente. */
fun Fragment.abrirSelecionarPaciente() {
    parentFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, FragmentSelecionarPaciente())
        .addToBackStack(null)
        .commit()
}

/** "Editar Consulta" do bottom sheet do profissional: abre a tela de agendamento já preenchida. */
fun Fragment.abrirEdicaoConsulta(consulta: Consulta) {
    parentFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, FragmentAgendarConsulta.editarConsulta(consulta))
        .addToBackStack(null)
        .commit()
}

private class PacientesAdapter(
    private var selecionadoId: String?,
    private val aoSelecionar: (Paciente) -> Unit
) : RecyclerView.Adapter<PacientesAdapter.PacienteViewHolder>() {

    private var itens: List<Paciente> = emptyList()

    fun submit(lista: List<Paciente>) {
        itens = lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PacienteViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.agenda_item_paciente, parent, false)
    )

    override fun getItemCount() = itens.size

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = itens[position]
        val selecionado = paciente.id == selecionadoId

        holder.nome.text = "Nome: ${paciente.nome}"
        holder.cpf.text = "CPF: ${paciente.cpf}"
        holder.marca.visibility = if (selecionado) View.VISIBLE else View.GONE
        holder.itemView.setBackgroundColor(if (selecionado) 0xFFE8FFF2.toInt() else 0x00000000)
        holder.divisor.visibility = if (position == itens.lastIndex) View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener {
            selecionadoId = paciente.id
            aoSelecionar(paciente)
            notifyDataSetChanged()
        }
    }

    class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.txtNomePacienteItem)
        val cpf: TextView = itemView.findViewById(R.id.txtCpfPacienteItem)
        val marca: View = itemView.findViewById(R.id.imgSelecionado)
        val divisor: View = itemView.findViewById(R.id.divisorPaciente)
    }
}
