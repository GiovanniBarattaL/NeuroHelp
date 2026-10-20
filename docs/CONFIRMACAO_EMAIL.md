# Confirmação de e-mail somente para novos cadastros

Depende do backend:
https://github.com/DiegodsGuinazu/BackEnd_TCC_3DS/pull/10

Os cadastros de responsável e profissional solicitam um código por
`POST /cadastro/email/solicitar`. Um painel abaixo do botão de cadastro permite
digitar oito números, confirmar e cadastrar, reenviar após 60 segundos ou voltar
para corrigir os dados. Ao confirmar, `codigoEmail` acompanha os campos anteriores
nas rotas de cadastro. A conta só é salva se o backend validar o código.

O ViewModel mantém a operação e os dados em memória durante rotação. Senha e
código não são colocados em Intent, Bundle, SavedStateHandle, arquivos ou logs.
Ao encerrar o processo, o usuário deverá preencher o cadastro novamente. Corrigir
os dados ou finalizar limpa a cópia mantida no ViewModel. Enquanto a confirmação
está aberta, os campos originais ficam bloqueados para não enviar dados diferentes
dos que foram apresentados.

Login, JWTs, perfil, recuperação e contas anteriores não mudam. Versões antigas
continuam entrando nas contas existentes, mas precisam da atualização para criar
novas contas depois do deploy do backend. Não publicar o backend isoladamente
antes de preparar os clientes.

## Validação no Android Studio

No terminal PowerShell da raiz do projeto:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug --no-daemon
```

Depois, conferir os dois perfis em dispositivo/emulador: e-mail real, código
incorreto e correto, reenvio, correção do endereço, voltar para login e rotação
durante envio e confirmação. Usar contas novas; contas anteriores fazem login
normalmente. O código não deve reaparecer após reiniciar a Activity/processo.

A build desta atualização não foi executada no ambiente de preparação: o download
do Gradle 9.1.0 estava bloqueado pela rede. Foram conferidos os 129 XMLs existentes,
integração dos dois formulários e ausência de armazenamento de dados sensíveis.
A build acima e a CI do PR do aplicativo devem passar antes de publicar.
