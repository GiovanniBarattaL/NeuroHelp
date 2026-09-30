# Integração Android com EspectroCare

Implementada no aplicativo Kotlin/XML do repositório GiovanniBarattaL/NeuroHelp, a partir de master. Contrato inspecionado no backend DiegodsGuinazu/BackEnd_TCC_3DS, commit b28c48de863789f42b48c1b412b13507cf8d9bac. Nenhum arquivo do backend foi alterado.

## Configuração e execução

No Android Studio, usar JDK 17 ou superior compatível com AGP 9.0.1, instalar SDK Android 36 e sincronizar o Gradle 9.1.0 do wrapper. A linguagem/target do aplicativo permanece conforme o projeto existente; Java 21 é requisito do backend.

A URL padrão é https://espectrocare.onrender.com. Alterar com a propriedade ESPECTROCARE_API_URL no gradle.properties local ou com -PESPECTROCARE_API_URL=https://seu-servidor. A configuração gera BuildConfig.API_BASE_URL e exige HTTPS. Não incluir senhas nem JWT_SECRET.

```bash
./gradlew testDebugUnitTest assembleDebug
# Com emulador ou dispositivo Android conectado:
./gradlew connectedDebugAndroidTest
```

## Contratos confirmados no código

| Operação | Rota | Entrada enviada | Sucesso |
| --- | --- | --- | --- |
| Login | POST /auth/login | email, senha | 200 JSON com token e email |
| Responsável | POST /cadastro/responsavel | nome, email, senha, cpf, telefone, estado | 200 texto |
| Profissional | POST /cadastro/profissional | anteriores + numRegistro | 200 texto |

Os cadastros recebem UserResp/UserProf diretamente, não DTOs. email, senha e cpf têm restrições NOT NULL no banco; nome também é solicitado no formulário. Não foram inventadas exigências de tamanho de senha ou formatos de registro. CPF tem verificação de 11 dígitos, sem validação de dígitos verificadores. Não enviar id, authorities, confirmação de senha/e-mail ou campos adicionais.

No profissional, a primeira etapa coleta nome, CPF (campo adicionado), telefone e UF. E-mail e senha são coletados somente na segunda etapa. Apenas dados não secretos passam por Intent; senhas não são persistidas nem incluídas em extras. A morte do processo exige redigitar a senha. Campos sem suporte no contrato foram ocultados: endereço detalhado, nascimento, sexo, vínculo, profissão e especialidade. O estilo visual dos campos e das telas foi mantido. Uma nota informa o escopo dos dados salvos.

O seletor pai/mãe ou profissional do login mantém seu estilo, mas não determina autorização: a API não recebe perfil nem o retorna no login. Ambos usam a tela principal existente. Não foi criada uma área exclusiva de profissional ou uma regra de perfil.

## Sessão e rede

ApiService centraliza HTTPS, JSON de entrada, respostas de sucesso JSON/texto, timeouts e erros. Requisições protegidas futuras devem usar protectedRequest, que adiciona Authorization: Bearer e invalida a sessão em 401. Erros 403 mantêm a sessão e indicam acesso negado. Corpos de erro nunca são exibidos literalmente, evitando vazamento de detalhes internos e permitindo respostas texto/HTML/vazias. Não há repetição automática de POST: em timeout o servidor pode já ter concluído o cadastro.

SessionStore persiste somente JWT criptografado com AES-GCM e chave no Android Keystore. Senhas e tokens não são registrados em logs. Backup e transferência excluem a sessão; backup do aplicativo foi desativado. A expiração exp do JWT é verificada localmente; a verificação criptográfica e autorização continuam no servidor. Token inválido, armazenamento corrompido ou chave indisponível limpam a sessão. Não há refresh token ou rota de logout no contrato: logout local remove o token, limpa a navegação e exige nova autenticação; não revoga um token já emitido no servidor.

AuthSubmissionViewModel mantém a operação durante rotação de tela, bloqueando POST duplicado e restaurando o estado de carregamento. Sucesso/erro é consumido pela tela visível. Senhas em EditText não entram no estado salvo. Após cadastro, o usuário é enviado ao login, sem autenticação automática. PrincipalActivity verifica a sessão ao abrir, ao retornar e enquanto estiver visível, e redireciona ao login quando expira ou é invalidada.

## Validação

Realizados: inspeção dos controllers, entidades, DTO Login, filtro JWT, TokenService e SecurityConfig; análise de todos os XMLs; revisão dos IDs das telas e git diff --check.

Adicionados testes instrumentados com transporte simulado para login válido e inválido com resposta texto, envio de Bearer, cadastro dos dois perfis sem login automático, erro de rede, logout, token expirado, 401 versus 403, resposta inválida, erro 500 sem detalhes internos, criptografia e corrupção da sessão, bloqueio de envio duplicado e rejeição de campos fora do contrato. Os JWTs dos testes são sintéticos e servem exclusivamente ao teste da expiração local.

Pendente: executar compilação, testes JVM e instrumentados, verificar layouts em dispositivo e fazer testes ponta a ponta com contas de teste. Neste ambiente, o wrapper não conseguiu baixar Gradle (Network is unreachable), e não há SDK Android. Nenhum teste contra a produção ou criação de conta real foi executado. Os testes não devem ser descritos como aprovados antes de sua execução.

Roteiro manual: cadastrar cada perfil; verificar retorno ao login; testar senha incorreta e correta; tocar duas vezes em enviar; girar a tela durante a requisição; desligar a internet; reabrir o app com sessão válida; sair e pressionar voltar; verificar redirecionamento por expiração. Inspecionar o formulário profissional e responsável em telas pequenas.

## Backend e impacto no site

Não é necessário alterar CORS para este aplicativo nativo. Não foram alterados endpoints, respostas, permissões ou regras de negócio da API compartilhada com o site.

Limitações encontradas no backend atual:

- Cadastro não usa Bean Validation/DTOs e não trata duplicidade com 409: e-mail/CPF duplicados podem resultar em 500. O aplicativo mostra mensagem genérica nesse caso e não classifica todo 500 como duplicidade. Se a API passar a retornar 409, a mensagem específica já está prevista. Recomenda-se adicionar DTOs e tratamento consistente, com testes de compatibilidade do site.
- SecurityConfig não configura explicitamente AuthenticationEntryPoint 401 nem AccessDeniedHandler 403. Portanto, é necessário confirmar em execução o código de token inválido nas rotas protegidas; o aplicativo não transforma um 403 em sessão expirada. Ajustes no backend devem ser revisados com o site antes de aplicar.
- Login não retorna perfil; o seletor visual não pode validar o tipo de conta. Evoluir esse contrato exige coordenação com os clientes.
- CORS atual permite padrões '*', e controllers usam CrossOrigin '*'. Recomenda-se restringir às origens web necessárias em trabalho separado, após listar os domínios reais do site. Isso não foi modificado para evitar impacto sem confirmação.

Para desfazer, retirar a branch/PR antes do merge ou reverter o commit da integração após o merge. Não é necessária migração no banco.
