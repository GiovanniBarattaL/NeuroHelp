# Revisão de edição e teclado

- A raiz da tela principal aplica o maior espaço entre teclado e barra inferior,
  uma única vez. O menu inferior não reaplica esses espaços e fica oculto durante
  a digitação. Ao fechar o teclado, o menu volta a aparecer.
- Ao trocar de aba ou navegar pela pilha de telas, o foco é limpo e o teclado fecha.
- O retorno de /api/auth/me não sobrescreve campos do profissional já em edição.
- Senhas dos dois perfis não são salvas no estado da interface durante rotação.
- O seletor de documentos trata perda de permissão/URI inválida sem derrubar a tela.
- Mantidas as alterações anteriores: endereço nos dois cadastros, profissão e
  registro obrigatórios, sem tipo de vínculo.

## Limites encontrados na revisão

Os botões Salvar de Privacidade e Segurança ainda exibem recurso indisponível:
o projeto não implementa a atualização do perfil na API. O responsável também
não tem carregamento de dados da conta nessa tela; mostra dicas de exemplo.
Não foi inventado um endpoint para gravar dados nem exibido sucesso sem gravação.
O envio dos novos campos dos cadastros precisa ser aceito pelo backend.

## Verificação

Conferidos XMLs e referências de recursos. Incluído teste instrumentado do
tratamento de insets para teclado aberto/fechado e navegação por gestos/botões.
A compilação e os testes Android precisam rodar no Android Studio: este ambiente
não tem distribuição Gradle/SDK disponíveis e o download do Gradle está bloqueado.

No aparelho, conferir os perfis profissional e responsável: abrir Privacidade,
editar campos no início e fim do formulário, rolar com teclado aberto, fechar o
teclado e navegar de volta; repetir em paisagem e nos cadastros.

## Limite dos campos de CPF

Cadastro do profissional, cadastro do responsável e edição dos CPFs do
profissional, responsável e dependente: máximo de 11 caracteres, somente 0 a 9.
A validação do cadastro continua exigindo exatamente 11 dígitos antes de enviar.
Não foi adicionada validação matemática dos dígitos verificadores nesta alteração.
A busca de paciente usa máscara e mantém até 14 caracteres (11 dígitos e pontuação).

## Outros limites revisados

Telefone nacional com DDD: somente números e até 11 dígitos; no cadastro,
se preenchido, deve ter 10 ou 11 dígitos. Telefone continua opcional.
CEP: somente números e até 8 dígitos, com 8 exigidos no cadastro.
UF: somente letras e até 2 caracteres; o cadastro confere a sigla do estado.
E-mail e confirmação: até 254 caracteres, mantendo a validação do formato
nos fluxos de autenticação. Senhas não receberam corte de tamanho.
Nascimento nas telas de perfil: seleção por calendário, sem datas futuras.

Não foi imposto um limite arbitrário ao número residencial ou ao registro
profissional, que pode conter letras, barras e hífens conforme o conselho.

Ao colar CPF, telefone ou CEP com pontuação, a pontuação é removida antes do
limite de comprimento. Telefones brasileiros colados com +55 têm o código do
país removido antes de aplicar o limite nacional com DDD.
