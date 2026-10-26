# Saída, agenda e estrelas

- Na tela principal, Voltar fecha primeiro o teclado, se aberto, ou retorna pela
  pilha de telas. Quando não há outra tela para retornar, pergunta:
  "Quer mesmo sair do app?", com Sair e Cancelar. Fechar o app mantém a sessão.
- O aviso usa DialogFragment para sobreviver à rotação. Ir à tela inicial do
  Android ou remover o app dos recentes não pode ser interceptado pelo aviso.
- O destaque verde do calendário mede 36 x 36 dp, centralizado na célula.
  A célula continua com área de toque maior; só o destaque deixa de ser oval.
- O card da lista de profissionais usa EstrelasHelper com a nota do modelo:
  cinco ícones, incluindo estrela parcialmente preenchida para meia nota.
  O texto 1/2 foi removido; o ícone de meia estrela também é usado no perfil.

Validação: XMLs e referências conferidos. Testar no Android Studio/aparelho:
voltar com teclado, de tela interna e da raiz; confirmar/cancelar o aviso;
girar com o aviso aberto; conferir calendário e estrelas com fontes ampliadas.
Compilação e testes Android continuam pendentes por falta do Gradle local e
bloqueio do download neste ambiente.
