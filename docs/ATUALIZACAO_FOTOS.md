# Atualização de fotos e compatibilidade

O contrato de cadastro/login existente foi preservado. O backend acrescenta `tipoPerfil: ADMIN` na sessão para administradores e `fotoPerfilUrl` opcional nas respostas de profissionais. A foto própria continua em `GET/PUT /api/conta/foto` com `{fotoPerfil: dataURL}`; a URL pública do profissional usa `GET /api/profissionais/{id}/foto`, retornando JPEG ou 404.

O app ignora campos desconhecidos via leitura seletiva do JSONObject. Perfis sem `fotoPerfilUrl` recebem avatar padrão. Fotos são carregadas sem cache HTTP, limitadas no download, e o envio reduz a imagem para JPEG 256×256 antes da validação final no servidor. Nenhum token ou corpo de requisição/resposta é registrado em log.

Compile com `./gradlew assembleDebug testDebugUnitTest assembleDebugAndroidTest`, SDK Android instalado e JDK compatível. Configure a API HTTPS por `-PESPECTROCARE_API_URL=https://sua-api`. Testes em aparelho/emulador: `./gradlew connectedDebugAndroidTest`.

Homologue seleção de foto na aba Perfil para responsável/profissional, foto nos ícones das abas, recarga do perfil, logout/login, imagem adicionada pelo site e apresentação do profissional por ID real. A Aprendizagem continua abrindo o site no navegador; não envie JWT por URL e use o login do site para operações administrativas. Convites e gestão da Aprendizagem não receberam telas nativas adicionais.

Cards e perfis agora usam dados reais da API. A API atual não define avaliações/modalidades: exemplos dessas informações deixam de aparecer como se fossem dados reais. O favorito do perfil conserva o estado local preexistente; não há persistência de favoritos/avaliações nesta entrega. Filtros demonstrativos continuam sem contrato de backend. Consulte `backend/docs/ADMINISTRACAO_E_FOTOS.md` no ZIP para migração, configuração e limitações completas.
