# EspectroCare

Aplicativo Android que aproxima **famílias de pessoas no espectro autista (TEA)** e **profissionais de saúde**.
O responsável encontra profissionais, acompanha a agenda de consultas e acessa conteúdos de aprendizagem.
O profissional acompanha a própria agenda, vê os dados dos pacientes e agenda ou edita consultas.

> Nome do app: **EspectroCare** · Pacote: `com.example.neurohelp` · Projeto Gradle: `NeuroHelp`

---

## Sumário

- [Funcionalidades](#funcionalidades)
- [Estado da integração com a API](#estado-da-integração-com-a-api)
- [Tecnologias](#tecnologias)
- [Como rodar](#como-rodar)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Perfis e navegação](#perfis-e-navegação)
- [Autenticação e segurança](#autenticação-e-segurança)
- [Testes](#testes)
- [Pendências](#pendências)
- [Documentação adicional](#documentação-adicional)

---

## Funcionalidades

### Comum aos dois perfis
- **Login** com e-mail e senha, recuperação de senha e **cadastro** (responsável ou profissional).
- **Cabeçalho padrão** (voltar, logo, notificações e perfil) em todas as telas internas.
- **Menu inferior** com quatro abas: Início, Profissionais, Agenda e Perfil.
- **Profissionais**: lista, busca, filtros (especialidade, tipo de atendimento e avaliação) e perfil completo com avaliações, favoritar e filtro das avaliações por nota.
- **Notificações** (abas Não visto, Essa semana e Todos), que abrem os detalhes da consulta.
- **Perfil**: dados da conta, Privacidade e Segurança, Ajuda e Suporte (FAQ), Sobre e Sair.
- **Conteúdos de aprendizagem**: atalhos que abrem a página de aprendizagem do site.

### Responsável
- **Início**: boas-vindas, atalhos (Aprendizagem, Profissionais etc.), conteúdos e próxima consulta, com acesso ao perfil do profissional.
- **Profissionais**: o botão *Agendar Consulta* abre o WhatsApp do profissional com a mensagem já escrita.
- **Agenda**: calendário mensal com os dias que têm consulta, lembrete da próxima consulta, legenda de cores por especialidade e lista agrupada por dia.
- **Detalhes da consulta** (bottom sheet): confirmar a consulta ou pedir alteração/cancelamento.

### Profissional
- **Início**: banner próprio, atalho de aprendizagem, conteúdos e a próxima consulta com os dados do paciente.
- **Profissionais**: a lista aparece, mas o botão *Agendar Consulta* fica desativado.
- **Agenda**: mesma base da agenda do responsável, mostrando o **paciente** em cada consulta, e botão **"+"** para novo agendamento.
- **Detalhes da consulta** (bottom sheet): dados do paciente, observações, **Editar Consulta** e **Cancelar consulta**.
- **Novo agendamento**: *Selecione o paciente* (busca por CPF, com máscara) → *Agendar consulta* (tipo, formato, data, horário e observações).
- **Editar consulta**: a mesma tela do agendamento, já preenchida, com o botão *Confirmar e alterar*.

---

## Estado da integração com a API

Hoje **somente a autenticação está integrada** ao back-end. O restante do app usa dados de exemplo (mock), marcados com `TODO` no código.

| Área | Situação | Onde está |
| --- | --- | --- |
| Login | Integrado (`POST /auth/login`) | `auth/ApiService.kt` |
| Cadastro de responsável e de profissional | Integrado (`POST /cadastro/...`) | `auth/ApiService.kt`, `Cadastro/` |
| Sessão (JWT) | Implementada e criptografada | `auth/SessionStore.kt` |
| Profissionais, perfil e avaliações | Dados de exemplo | `Profissionais/ModeloProfissional.kt` |
| Agenda e consultas | Dados de exemplo (em memória) | `agenda/AgendaRepository.kt` |
| Pacientes do profissional | Dados de exemplo | `agenda/Paciente.kt` (`PacientesMock`) |
| Notificações | Dados de exemplo | `notificacoes/ModeloNotificacao.kt` |
| FAQ da Ajuda | Lista fixa | `ajuda/FaqAdapter.kt`, `ajuda/FragmentAjuda.kt` |

Ações como confirmar, cancelar, agendar e editar consulta, favoritar e enviar comentário mostram só a resposta visual por enquanto.

---

## Tecnologias

- **Kotlin** com **Views em XML** (sem Compose) e **Fragments** dentro da `PrincipalActivity`.
- **Material Components 1.14** (Material 3), ConstraintLayout, RecyclerView, CardView.
- **ViewModel + LiveData** (`lifecycle` 2.9.4) e Fragment KTX.
- Rede com `HttpURLConnection`, JSON nativo e coroutines, sem bibliotecas externas de rede.
- **Android Keystore** (AES-GCM) para guardar o token.
- Fontes: Nunito e Inter.

| Item | Versão |
| --- | --- |
| Android Gradle Plugin | 9.0.1 |
| Gradle (wrapper) | 9.1.0 |
| `compileSdk` / `targetSdk` | 36 |
| `minSdk` | 24 (Android 7.0) |
| Java (compatibilidade) | 11 |

---

## Como rodar

### Requisitos
- Android Studio recente, com **JDK 17 ou superior** (compatível com o AGP 9.0.1).
- **SDK Android 36** instalado.
- Emulador ou dispositivo com Android 7.0 (API 24) ou superior.

### Passos
1. Abra a pasta do projeto no Android Studio e aguarde a sincronização do Gradle.
2. Rode o app (`app`) em um emulador ou dispositivo.

Pela linha de comando:

```bash
./gradlew assembleDebug            # gera o APK de debug
./gradlew testDebugUnitTest        # testes locais (JVM)
./gradlew connectedDebugAndroidTest  # testes instrumentados (exigem emulador/dispositivo)
```

### URL da API
Por padrão o app usa `https://espectrocare.onrender.com`. Para apontar para outro servidor, defina `ESPECTROCARE_API_URL` no `gradle.properties` local ou na linha de comando:

```bash
./gradlew assembleDebug -PESPECTROCARE_API_URL=https://seu-servidor
```

A URL **precisa ser HTTPS**. O valor vira `BuildConfig.API_BASE_URL`. Não coloque senhas nem segredos nesse arquivo.

> O servidor padrão está hospedado no Render. Em planos gratuitos a primeira requisição pode demorar enquanto ele "acorda".

---

## Estrutura do projeto

```
app/src/main/java/com/example/neurohelp/
├── Login/            Login e recuperação de senha
├── Cadastro/         Escolha do tipo de conta e cadastros (responsável e profissional, em 2 etapas)
├── auth/             ApiService, SessionStore (JWT criptografado), PapelUsuario, helpers de UI
├── PrincipalActivity.kt   Host dos fragments + menu inferior
├── CabecalhoPadrao.kt     Liga os botões do cabeçalho padrão (voltar, sino, perfil)
├── Home/             Home do responsável, Home do profissional e partes comuns (HomeComum.kt)
├── Profissionais/    Lista, filtros, perfil do profissional, avaliações, WhatsApp
├── agenda/           Agenda, calendário, detalhes da consulta (2 dialogs), reagendar,
│                     seleção de paciente, agendar/editar consulta, modelos e repositório de exemplo
├── notificacoes/     Lista de notificações com filtros
├── Perfil/           Perfil, Sobre e Privacidade e Segurança
└── ajuda/            Ajuda e Suporte (FAQ)

app/src/main/res/
├── layout/           Telas e itens (header_padrao.xml é o cabeçalho reutilizado via <include>)
├── drawable/         Ícones, fundos e imagens
├── font/             Nunito e Inter
└── values/           strings, colors, themes

docs/INTEGRACAO_API.md    Detalhes do contrato com o back-end, sessão e testes
```

---

## Perfis e navegação

O app tem **duas áreas**, uma para cada perfil, e as telas são trocadas conforme quem entrou:

- **Início** abre `FragmentHome` (responsável) ou `FragmentHomeProfissional` (profissional).
- **Agenda** é a mesma `FragmentAgenda`, que se adapta ao perfil: pacientes e botão "+" para o profissional.
- **Detalhes da consulta** abre um bottom sheet diferente para cada perfil.

**Como o perfil é definido:** a API não informa o tipo de conta no login. Por isso o app usa o seletor **"Sou pai/mãe" / "Sou profissional"** da tela de Login. A escolha é salva na sessão (`SessionStore.papel()`) e apagada junto com ela no logout.

> O seletor só decide **qual interface aparece**. Ele não concede nem valida permissões, que continuam sendo responsabilidade do servidor.
> Quando o back-end passar a devolver o perfil, basta gravá-lo com `SessionStore.salvarPapel()` em `Login/Login.kt`; o resto do app lê o papel apenas por `SessionStore.papel()`.

### Fluxo do profissional na agenda

```
Agenda ──"Detalhes"──▶ Bottom sheet do paciente ──"Editar Consulta"──▶ Agendar consulta (preenchida)
   │                                              └─"Cancelar consulta"──▶ confirmação
   └──"+"──▶ Selecione o paciente ──▶ Agendar consulta ──▶ volta para a Agenda
```

---

## Autenticação e segurança

- Comunicação **somente por HTTPS**; `usesCleartextTraffic` está desativado.
- O **JWT** é guardado criptografado (AES-GCM, chave no Android Keystore). Senhas não são salvas nem registradas em log.
- A expiração do token é verificada localmente. A `PrincipalActivity` confere a sessão ao abrir, ao voltar e enquanto está visível, e redireciona ao Login quando ela expira.
- Requisições protegidas devem usar `ApiService.protectedRequest`, que envia `Authorization: Bearer` e invalida a sessão em respostas 401.
- Backup do app desativado e sessão excluída das regras de backup e transferência.
- O envio de login e cadastro sobrevive à rotação de tela e bloqueia envios duplicados.
- Não há *logout* no servidor: sair apaga o token local e exige novo login, sem revogar o token já emitido.

Mais detalhes em [`docs/INTEGRACAO_API.md`](docs/INTEGRACAO_API.md).

---

## Testes

| Tipo | Local | O que cobre |
| --- | --- | --- |
| Instrumentados | `app/src/androidTest/.../auth/AuthIntegrationTest.kt` | Login, cadastro, sessão e Bearer, 401 e 403, token expirado, criptografia, envio duplicado, usando transporte simulado |
| Local (JVM) | `app/src/test/.../ExampleUnitTest.kt` | Apenas o teste de exemplo do template |

As telas e os fluxos de agenda não têm testes automatizados; os testes são manuais. Os testes de autenticação precisam de emulador ou dispositivo.

---

## Pendências

Principais pontos para a próxima etapa (todos marcados com `TODO` no código):

- Trocar os dados de exemplo (profissionais, agenda, pacientes, notificações e FAQ) por chamadas à API.
- Persistir as ações da agenda: confirmar, cancelar, pedir alteração, agendar e editar consulta.
- Favoritar profissional e enviar comentário de avaliação.
- Número de WhatsApp real de cada profissional.
- Nome do usuário na saudação da Home do profissional (hoje aparece "Olá, Dr.(a) XXX").
- Receber o **perfil da conta** do back-end, em vez de depender do seletor do Login.
- Executar a suíte de testes e fazer testes ponta a ponta com contas de teste.

---

## Documentação adicional

- [`docs/INTEGRACAO_API.md`](docs/INTEGRACAO_API.md): contrato da API, sessão, rede, roteiro de testes e limitações do back-end.
- Back-end: repositório `DiegodsGuinazu/BackEnd_TCC_3DS` (Java 21).
