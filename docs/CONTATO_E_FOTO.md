# Contato e foto da conta

## Alterações

Ajuda e Suporte e Sobre usam espectrocare@gmail.com, inclusive no destinatário
aberto pelo toque. Instagram removido das duas telas.

Na aba Perfil, tanto a foto grande quanto a pequena do cabeçalho permitem
selecionar uma imagem. A falha em GET /api/auth/me não desativa a seleção.
O envio usa a sessão autenticada e o contrato já documentado no projeto:

- GET /api/conta/foto: JSON com fotoPerfil como data:image/jpeg;base64,...
- PUT /api/conta/foto: mesmo campo; resposta deve confirmar uma foto válida.

O app reduz para JPEG quadrado de 256 pixels e corrige orientação EXIF.
Uma confirmação válida da API atualiza todos os avatares próprios já ligados à
tela. Leituras iniciadas antes do envio não restauram a imagem anterior.
Ao navegar ou retornar ao app, a foto é lida novamente da API. Uma falha de
rede mantém a foto já carregada na sessão. Sair da conta limpa a imagem em
memória; o armazenamento durável permanece no servidor.

## Site

Este ZIP contém somente o aplicativo Android. O envio continua usando a rota
compartilhada existente, sem endpoint novo ou armazenamento local como fonte
principal. O site precisa ler fotoPerfil da mesma conta/API e atualizar sua
imagem ao recarregar. O código do site e o backend não foram fornecidos;
não foi possível conferir ou alterar a exibição no site nem comprovar a
persistência em produção. Nenhuma foto real foi enviada durante os testes.

## Validação

Verificados XMLs, IDs e referências de recursos; e-mail antigo e Instagram
removidos das telas pedidas. A tentativa de assembleDebug e testDebugUnitTest
parou antes da compilação: o wrapper Gradle não está disponível localmente e a
rede bloqueou o download. Testes Android/servidor não foram executados.

## Conferência no aparelho e site

1. Nos dois perfis, abrir Ajuda e Sobre e tocar no e-mail.
2. Na aba Perfil, tocar na foto grande e depois testar também a pequena.
3. Selecionar JPG/PNG/WebP; conferir foto no perfil e no cabeçalho.
4. Trocar de aba, fechar/abrir o app e entrar novamente com a mesma conta.
5. Abrir o site com essa conta, recarregar e conferir a foto do servidor.
6. Alterar a foto pelo site e retornar ao app; conferir a nova leitura.
7. Testar seleção cancelada, imagem inválida, rede indisponível e logout.
