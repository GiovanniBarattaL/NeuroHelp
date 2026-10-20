# CPF e recuperação de senha

Atualização compatível com o backend EspectroCare. Publicar o backend antes de distribuir este app. Login e cadastro mantêm as rotas/corpos anteriores.

## Alterações

- `auth/Cpf.kt`, `auth/AuthUi.kt`, `auth/ApiService.kt`: validação dos dígitos verificadores no cadastro dos dois perfis; também revalidada pelo backend. Mensagem “CPF inválido”; não comprova existência ou titularidade.
- `Login/RecuperarSenha.kt` e seu layout: substitui o antigo envio de link por envio real de código via `POST /auth/esqueci-senha`. “Já tenho um código” abre a confirmação sem exigir reenvio. O pedido usa ViewModel para sobreviver à rotação, mantendo apenas o e-mail no estado salvo.
- `Login/RedefinirSenha.kt`, `res/layout/redefinir_senha.xml`, Manifest: código de 8 dígitos, nova senha forte e confirmação via `POST /auth/redefinir-senha`. Código/senhas não entram em extras, preferências ou estado salvo. Após sucesso, apaga a sessão e volta ao login.
- `Perfil/FragmentPerfil.kt` e `res/layout/perfil_fragment.xml`: botão Alterar senha sob o e-mail. No fluxo do perfil, consulta `/api/auth/me` e envia sem corpo para `/api/conta/recuperacao-senha`, usando o destinatário do banco.
- `auth/AuthUi.kt`: callback opcional de estado para bloquear edição enquanto envia; chamadas existentes mantidas.
- `auth/CpfTest.kt`: casos válidos, inválidos, repetidos e com máscara.

A CI em `.github/workflows/validar-android.yml` executa os testes e compila o APK de depuração em PRs para `master`.

## Testar

No Android Studio, sincronizar Gradle e executar `./gradlew testDebugUnitTest assembleDebug` (Windows: `gradlew.bat testDebugUnitTest assembleDebug`). Requer SDK 36 e JDK compatível com o Android Gradle Plugin do projeto.

1. CPF incorreto deve bloquear os dois cadastros antes do envio. O backend também deve rejeitar chamadas manuais que contornem a interface.
2. Entrar como responsável/profissional, Perfil → Alterar senha. E-mail vem da conta e não pode ser editado nesse fluxo.
3. Recuperar pelo login, receber código por e-mail, usar Já tenho um código, testar senha fraca/confirmação diferente/código incorreto. Prazo: 15 minutos, cinco tentativas e uso único.
4. Rotacionar a tela durante o envio: não deve duplicar pedido nem trocar o destinatário. Voltar após falha para corrigir e-mail quando necessário.
5. Após redefinir, confirmar novo login no app e no site; senha antiga e JWT anterior deixam de funcionar. O site pode usar um código solicitado pelo app e vice-versa.
6. Testar com teclado aberto, fonte ampliada e tela estreita; a confirmação usa rolagem.

O envio usa Gmail no backend após o PR #8. Configurar `EMAIL_PROVIDER=gmail`, `GMAIL_CLIENT_ID`, `GMAIL_CLIENT_SECRET`, `GMAIL_REFRESH_TOKEN` e `GMAIL_SENDER=espectrocare@gmail.com` somente no Render/backend. Não inserir credenciais no app. A troca do provedor preserva as rotas do #7 e não exige outras alterações no cliente. Sem configuração, recuperação fica indisponível e os outros recursos continuam funcionando. Sem credenciais não é possível validar entrega real. Consulta oficial de CPF permanece pendente de contratação e credenciais; nenhuma integração cadastral foi simulada.

Para desfazer, reverter este commit e reinstalar a versão anterior; não há migração no armazenamento do app. Se o backend também for revertido, seguir o plano de revogação de JWT no documento dele.

## Alinhamento com o backend integrado (PR #7)

Compatível com `main` após https://github.com/DiegodsGuinazu/BackEnd_TCC_3DS/pull/7 . Não mesclar o PR #6 para aplicar estes clientes. O endpoint autenticado é `POST /api/conta/recuperacao-senha` sem corpo; solicitações públicas usam `/auth/esqueci-senha` e confirmações usam `/auth/redefinir-senha`. As três operações retornam HTTP 200 no sucesso, com `mensagem`. A rota pública de validação matemática é `POST /validacoes/cpf`; os cadastros continuam revalidando CPF diretamente.

Código vale por 15 minutos, tem cinco tentativas e uso único. Reenvio após 60 segundos substitui o código anterior; limites adicionais podem retornar 429. Use a migração `2026-10-19-cpf-recuperacao-senha.sql` da `main` e a documentação atual do backend.

## Envio por Gmail — PR #8

O backend na main inclui o PR #8, que preserva os contratos do #7 usados por este cliente. Gmail é configurado apenas no Render/backend. Nenhuma chave, JSON OAuth ou token deve ser copiado para o site/app. A migração PostgreSQL continua sendo a do #7. Confira o deploy, o banco e a entrega real antes de publicar.

## Compatibilidade com a master de 20/10/2026

Patch completo atualizado para a master `0292c997920e215f405cf586a25b40f7e6923663`. Preserva tela cheia, aplicação de insets e limite de largura da recuperação, os ajustes de janela das atividades e as opções específicas de profissional no perfil (notificações e privacidade). Adiciona a recuperação por código sobre essa versão. O contrato do backend e o SQL permanecem os dos PRs #7/#8.
