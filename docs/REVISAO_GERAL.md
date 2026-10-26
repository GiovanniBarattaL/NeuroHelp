# Revisão geral do EspectroCare

Base: arquivo `EspectroCare (2).zip` e imagem do botão de login enviados em 22/10/2026.

Esta entrega contém correções implementadas no código. A conferência de XML e referências foi feita, mas **a compilação, os testes Android e a inspeção em emulador ainda precisam ser executados no Android Studio**. Não há garantia de ausência de outros defeitos.

## Problema da imagem

O botão Entrar tinha `layout_weight="1"` numa coluna dentro de um NestedScrollView com `fillViewport="true"`. Ele recebia o espaço vertical restante e ficava muito alto. O peso foi removido; o botão usa altura pelo conteúdo e mínimo de 48dp, permitindo crescer quando o usuário aumenta a fonte. Os pesos que dividem a largura de linhas horizontais foram preservados.

O arquivo do login também estava sem `inputType` nos campos de e-mail e senha. Foram definidos teclado de e-mail, máscara de senha, botão de mostrar/ocultar, preenchimento automático e envio pelo botão Concluído do teclado. O seletor de perfil ganhou medidas coerentes. As rotas, o armazenamento de sessão e a requisição de login foram preservados. O texto isolado "ou continue com", sem opções de acesso associadas, foi retirado.

## Correções por área

| Área | Alteração implementada |
| --- | --- |
| Botões e formulários | Alturas fixas curtas substituídas por altura pelo conteúdo e mínimo de 48dp nos botões de ação. Rótulos podem quebrar linha. Campos de uma linha e suas caixas deixam de depender de alturas pequenas que cortam fontes maiores. |
| Perfil e cadastro | Campos podem crescer com a fonte; botões Editar também. Corrigida a dica de data de nascimento do dependente. |
| Telas estreitas | Ações lado a lado, resumo da consulta e topo do perfil profissional passam a coluna abaixo de 380dp ou com fonte acima de 115%. |
| Tablets e paisagem | Conteúdos de rolagem são centralizados com largura limitada; foram mantidos os ajustes específicos já existentes da Home e Privacidade do profissional. |
| Barras e teclado | Ajuste explícito do espaço do teclado na tela principal. Aplicação do inset superior e dos limites de largura torna-se idempotente. Removida a configuração duplicada do cabeçalho no Perfil. |
| Home do responsável | Removido o quarto card duplicado. Os três conteúdos dividem a linha ou são empilhados em telas estreitas; os três abrem a página de aprendizagem já usada pelo app. |
| Home dos dois perfis | Saudação usa o nome retornado pelo endpoint de conta já existente; sem resposta, exibe "Olá!", em vez de Ana ou XXX. |
| Lista de profissionais | Busca local por nome, profissão, registro e localização; quantidade acompanha o registro exibido; favoritos e filtros ligados à interface. A opção Favoritar não disputa espaço com o CRP. |
| Favoritos | A opção no Perfil abre a lista filtrada; coração da lista e do perfil usa o mesmo armazenamento local da sessão. Sobrevive à navegação e rotação; é apagado ao sair da conta e não é sincronizado com servidor. |
| Filtros | Painel de profissionais centralizado, com dimensões limitadas à janela visível, rolagem, opções legíveis e cidade preenchida. Aplicar e Limpar passam a agir sobre o registro disponível. Filtro das avaliações também ganha rolagem e limites de tamanho. |
| Agenda | Uma consulta passada não é mais mostrada como próxima quando não há consulta futura no mês carregado. Calendário é montado por dias de calendário, evitando depender de dias de exatamente 24 horas. Cancelamento de corrotina é propagado. |
| Notificações | Filtro da semana usa início/fim da semana, evitando erro na virada do ano. |
| Diagnóstico | Impedida a reentrada dos listeners ao escolher uma opção e desmarcar as demais. Mantém a seleção exclusiva sem a lógica reativar uma opção antiga durante a troca. |
| Retorno das ações | Ações sem integração deixam de exibir "consulta agendada", "cancelada", "confirmada" ou "solicitação enviada" sem salvar nada. Agora avisam que a função está indisponível. Formulários mantêm o que foi digitado. |
| Contato e links | Número fictício do WhatsApp do mock removido; contato ausente recebe mensagem apropriada. Agendamento também fica desabilitado no perfil do profissional quando o usuário entrou como profissional. Ajuda/Sobre tratam ausência de aplicativo para abrir links. |
| Ajuda | Respostas deixam de prometer videochamada dentro do app ou regra de cancelamento de 24 horas que não está implementada. |
| Indicador de envio | Indicador usa dp e fica centralizado, em vez de tamanho em pixels e alinhamento no canto. |
| Gradle wrapper | Script Unix convertido de CRLF para LF e marcado executável. O arquivo original falhava no shell antes mesmo de iniciar o Gradle. |

## Limitações funcionais ainda existentes

A revisão não criou contratos de backend nem substituiu os dados de exemplo por dados reais.

- **Profissionais:** existe apenas um registro demonstrativo, com texto e avaliações de exemplo. Os filtros operam sobre ele. A contagem anterior de 48 era fixa; não foram inventados outros 47 profissionais.
- **Agenda e pacientes:** continuam usando `AgendaRepositoryMemoria` e `PacientesMock`. Criar, editar, cancelar, confirmar e solicitar alteração dependem de integração real. A interface informa essa limitação em vez de confirmar uma gravação inexistente.
- **Próxima consulta:** a busca considera apenas o mês carregado. Não consulta automaticamente o próximo mês.
- **Privacidade:** salvar dados pessoais, endereço, formação, segurança e documentos ainda não chama um endpoint de atualização. A revisão mantém a edição aberta e avisa que não foi salva. Dados do responsável/dependente ainda precisam ser ligados à conta real.
- **Avaliações:** leitura, paginação e filtro continuam locais. O envio de novo comentário ainda não é implementado; a tecla Enviar agora apresenta uma informação em vez de não fazer nada.
- **Notificações:** são exemplos em memória. Marcar como vista não é sincronizado com servidor.
- **Perfil do usuário:** a separação responsável/profissional ainda usa o seletor local existente. A autorização efetiva dos recursos precisa continuar sendo feita pelo servidor.
- **Recuperação de senha:** a própria implementação original informa que a rota `/auth/esqueci-senha` precisa ser confirmada com o backend. Nenhuma chamada real foi feita nesta revisão.
- **Cadastro:** os campos aceitos e enviados à API continuam os do contrato documentado em `docs/INTEGRACAO_API.md`. Há campos de apresentação que não são enviados. A validação de CPF existente verifica quantidade de dígitos, não os dígitos verificadores.
- **Fontes e links:** as fontes continuam sendo as fontes baixáveis do projeto; os links externos e o serviço remoto não foram testados nesta execução.
- **Fidelidade visual:** esta revisão corrige problemas de implementação e adaptação. Não é uma certificação de equivalência pixel a pixel com o PDF anterior; cabeçalhos, paleta e espaçamentos ainda podem precisar de aprovação visual no aparelho.

## Conferência executada

- Leitura dos layouts e código de navegação, formulários, agenda, perfis, profissionais, notificações, autenticação e ajuda.
- XMLs de recursos analisados quanto à sintaxe; referências locais de recursos conferidas; IDs duplicados e cardinalidade de filhos de ScrollViews verificados.
- 127 casts explícitos de `findViewById` confrontados estaticamente com os tipos de view declarados nos layouts, sem incompatibilidade detectada.
- Conferidos botões em colunas para não consumirem altura com peso vertical.
- `bash -n gradlew` passou após corrigir as quebras de linha.
- Foi tentado `./gradlew testDebugUnitTest assembleDebug --offline`. O wrapper não estava instalado e tentou obter o Gradle 9.1.0; o ambiente retornou `Network is unreachable`. Portanto, não houve compilação nem execução dos testes.
- O ambiente não tem SDK/emulador Android disponível. Responsividade foi tratada por revisão estrutural do código; aparência e interação em execução permanecem pendentes.

## Como conferir no Android Studio

1. Extraia o ZIP em uma pasta separada e abra a pasta EspectroCare. Faça a sincronização com o SDK/JDK compatíveis com o projeto.
2. Execute `./gradlew testDebugUnitTest assembleDebug` (Windows: `gradlew.bat testDebugUnitTest assembleDebug`). Resolva qualquer falha real reportada pela sincronização antes de executar o app.
3. Confira em larguras de 320, 360, 411, 600 e 840dp; retrato e paisagem; fonte padrão, 130% e 200%.
4. No login, confira altura do Entrar com teclado fechado/aberto, troca dos dois perfis, senha mascarada, mostrar/ocultar e Concluído.
5. Navegue entre todas as abas e volte de uma tela interna; confira barras do sistema, teclado e rolagem até o último botão.
6. Busque um termo inexistente e depois limpe; aplique e limpe filtros; favorite o registro, abra Favoritos e remova o favorito; confira o estado vazio e a limpeza ao sair da conta.
7. Confira os dois perfis, os resumos da consulta e botões longos com fonte grande. No diagnóstico, alterne entre as três opções.
8. Confira mensagens das operações ainda indisponíveis; elas não devem anunciar sucesso nem descartar o formulário como se o servidor tivesse salvo.

O ZIP contém o código e recursos do aplicativo, wrapper e documentação. Não inclui histórico Git, configurações locais do Android Studio, caches, builds ou `local.properties`; o Android Studio gera a configuração local do SDK ao abrir o projeto.
