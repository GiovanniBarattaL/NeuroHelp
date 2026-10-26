# Correção da falha ao abrir Profissionais

FragmentProfissionais carrega profissionais_fragment.xml. Esse layout ainda
continha txtEstrelas1 (TextView), enquanto a linha 69 procurava estrelasLista
(LinearLayout). A mudança anterior estava apenas em profissional_card_api.xml,
que não é o layout inflado por esse fragmento.

Substituído o campo no layout realmente carregado por LinearLayout com o ID
estrelasLista, compatível com EstrelasHelper. Mantidas as cinco estrelas com
ícone de meia estrela. Conferidos todos os IDs pesquisados por view.findViewById
nessa tela, incluindo os cabeçalhos incluídos.

Incluído teste instrumentado que infla o layout real e preenche as estrelas.
XMLs e referências estáticas conferidos. O teste Android e a compilação ainda
não foram executados, pois o ambiente não consegue baixar o Gradle.
