package com.example.neurohelp.auth

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * Quem está usando o app. Define qual Home/Agenda aparece.
 *
 * ATENÇÃO: a API não devolve o perfil no login (ver docs/INTEGRACAO_API.md), então hoje o papel
 * vem do seletor "Sou pai/mãe" / "Sou profissional" da tela de Login. Quando o back-end passar a
 * informar o perfil, basta chamar [SessionStore.salvarPapel] com o valor da API (em Login.kt);
 * o restante do app já lê o papel só por [SessionStore.papel].
 */
enum class PapelUsuario { RESPONSAVEL, PROFISSIONAL }

/** Papel do usuário logado (responsável por padrão). */
fun Context.papelUsuario(): PapelUsuario = SessionStore(this).papel()

/** Atalho para os fragments: true quando quem entrou foi um profissional. */
fun Fragment.ehProfissional(): Boolean = requireContext().papelUsuario() == PapelUsuario.PROFISSIONAL
