# Revisão do envio da foto

A imagem do erro mostra uma mensagem genérica, sem o código HTTP ou a etapa da
falha. Não é possível afirmar a causa específica com esse registro.

Corrigidos no app:
- O processamento usa os bytes reais da imagem, em vez de rejeitar apenas por
  MIME ausente/diferente informado pelo provedor de galeria.
- Fotos de até 15 MB e até 100 megapixels são lidas com amostragem para tamanho
  reduzido. O envio continua JPEG de 256 x 256, sem aumento de tamanho na API.
- PUT 2xx com resposta vazia ou texto não causa JSONException. O app faz GET
  para confirmar que a foto enviada foi persistida, sem repetir o PUT.
- Se o servidor não confirmar a imagem, não é exibido sucesso.
- Falhas de acesso, conexão, sessão e HTTP mostram motivos diferentes em diálogo
  completo. Não são expostos tokens, conteúdo da imagem ou resposta do servidor.

Revisão adicional: conferidos IDs pesquisados na raiz dos fragments com um
layout de criação identificado, incluindo cabeçalhos, sem referência ausente.
Isso não substitui compilação, teste de navegação e teste contra o backend.

O backend não foi enviado. Se aparecer 403, 404, 405 ou erro de formato no novo
aviso, é necessário corrigir autorização/rota/contrato no backend. O ZIP não
inclui esse serviço. A compilação segue bloqueada pelo download do Gradle.

Incluídos três testes instrumentados para confirmação de PUT sem corpo,
recusa de confirmação quando GET ainda devolve foto antiga e preservação do
código 404. Esses testes não foram executados neste ambiente.
