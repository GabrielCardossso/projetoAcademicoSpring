# Frontend React

Interface simples para executar os endpoints REST do backend Spring Boot sem depender do Swagger.
Feita com React, TypeScript, Vite, React Router e Axios, com CSS simples.

## Resumo rápido

- **Só o backend (Swagger):** rode o `SoftwareApplication.java` pelo VS Code e abra
  `http://localhost:8080/swagger-ui.html`. Os endpoints exigem login. Faça o login pelo próprio Swagger: em
  **auth-controller**, abra `POST /auth/login`, clique em *Try it out*, preencha `email` e `senha` e clique em
  *Execute*. A partir daí as chamadas usam a sessão do usuário e respeitam o perfil dele (um desenvolvedor recebe `403`
  ao tentar um cadastro). `POST /auth/logout` encerra a sessão.
- **Se o VS Code der `UserDetailsService.class ... does not exist` ao rodar:** o VS Code não recarregou o `pom.xml`
  depois que o Spring Security foi adicionado. Abra a paleta de comandos (`Ctrl+Shift+P`) e rode
  **Java: Clean Java Language Server Workspace** (confirme com *Reload and delete*). Ou rode pelo terminal com
  `.\mvnw.cmd spring-boot:run`, que sempre usa o `pom.xml` atualizado.
- **Senha dos usuários:** na primeira execução, os usuários sem senha recebem uma senha aleatória, mostrada **uma única
  vez** no console nesta linha: `Senha inicial gerada (anote, ela não será exibida de novo): ...`. Anote-a. Para
  escolher a senha, rode pelo terminal com `SENHA_INICIAL` definida antes da primeira execução (passo 1 abaixo).
- **Com o frontend:** com o backend rodando na porta 8080, instale o Node.js e, na pasta `frontend`, rode
  `npm install` (só na primeira vez) e depois `npm run dev`. Abra `http://localhost:5173` e entre com o **e-mail** de
  um gestor ou desenvolvedor e a senha. O `.env` é opcional: sem ele, o front usa `http://localhost:8080`.

## Como rodar

Pré-requisitos: Java 17+, PostgreSQL com o banco `trabalhoAlisson` e Node.js 18 ou superior.

1. **Iniciar o backend** (na pasta raiz do projeto, a que tem o `pom.xml`):

   ```powershell
   # Opcional: senha dada aos usuários que ainda não têm senha (veja "Login")
   $env:SENHA_INICIAL = "12345678"
   .\mvnw.cmd spring-boot:run
   ```

   O backend sobe em `http://localhost:8080`.

2. **Configurar a URL do backend** (na pasta `frontend`):

   ```powershell
   copy .env.example .env
   ```

   O `.env` tem `VITE_API_URL=http://localhost:8080`. Use `localhost`, e não `127.0.0.1`, para os cookies funcionarem.

3. **Instalar as dependências e iniciar o frontend** (na pasta `frontend`):

   ```powershell
   npm install
   npm run dev
   ```

   Abra `http://localhost:5173`.

Outros scripts: `npm run typecheck` (verificação de tipos) e `npm run build` (tipos + build de produção em `dist/`).

## Login

- O login é o **e-mail** de um Gestor de Equipe ou de um Desenvolvedor, com a **senha** dele.
- O frontend envia `POST /auth/login` (form-urlencoded, campos `email` e `senha`). Quem valida é o Spring Security,
  que cria uma sessão HTTP (cookie `JSESSIONID`). Em seguida o frontend chama `GET /auth/me` para saber quem entrou.
- A senha fica no banco criptografada com BCrypt e nunca aparece no JSON. O navegador não guarda a senha.
- **Usuários que já existiam antes do login** (e os dados de exemplo) não tinham senha. Na inicialização, o backend
  dá a eles a senha da variável `SENHA_INICIAL`. Se a variável não existir, ele gera uma senha aleatória e mostra
  no console uma única vez (`Senha inicial gerada ...`). Isso só acontece para quem está sem senha.
- Novos gestores e desenvolvedores recebem a senha no próprio cadastro (`POST /gestor/salvar` e `POST /dev/salvar`).
- O botão **Sair** chama `POST /auth/logout`, que invalida a sessão. Sem sessão, qualquer página interna volta para o login.

## Perfis e permissões

O perfil vem do tipo do usuário: `GestorEquipe` = **GESTOR**, `Desenvolvedor` = **DESENVOLVEDOR**.

| Perfil        | Pode fazer                                                                                  |
|---------------|---------------------------------------------------------------------------------------------|
| GESTOR        | Tudo: consultas, cadastros, atualizações, atribuições e ativar/inativar.                    |
| DESENVOLVEDOR | Todas as consultas (GET), `PUT /atividade/atualizarStatus/{id}` e `PUT /atividade/atribuirDesenvolvedor`. |

As regras estão no `SecurityConfig` do backend. O frontend apenas esconde o que o perfil não pode usar. Para mostrar
na apresentação que o backend bloqueia de verdade, marque **Mostrar operações sem permissão** na barra lateral, entre
como desenvolvedor e execute um cadastro: a resposta será `403 Forbidden`.

## Endpoints integrados

Todos os endpoints dos Controllers estão na barra lateral. O mapa fica em `src/services/endpoints.ts`.

| Entidade         | GET | POST | PUT |
|------------------|-----|------|-----|
| Projeto          | `/projeto/listar`, `/projeto/mostrar/{id}`, `/projeto/listarPorStatus/{status}`, `/projeto/buscarPorNome`, `/projeto/listarPorEquipe/{equipeId}`, `/projeto/comPendencias`, `/projeto/progresso/{id}` | `/projeto/salvar` | `/projeto/atualizar/{id}`, `/projeto/atualizarStatus/{id}`, `/projeto/atribuirEquipe`, `/projeto/setStatus/{id}` |
| Atividade        | `/atividade/listar`, `/atividade/mostrar/{id}`, `/atividade/atrasadas`, `/atividade/listarPorDesenvolvedor/{devId}`, `/atividade/listarPorProjeto/{projetoId}`, `.../situacao/{situacao}`, `.../prioridade/{prioridade}` | `/atividade/salvar` | `/atividade/atualizar/{id}`, `/atividade/atualizarStatus/{id}`, `/atividade/atribuirDesenvolvedor`, `/atividade/setStatus/{id}` |
| Equipe           | `/equipe/listar`, `/equipe/mostrar/{id}` | `/equipe/salvar` | `/equipe/atribuirGestor` |
| Gestor de Equipe | `/gestor/listar`, `/gestor/mostrar/{id}` | `/gestor/salvar` | — |
| Desenvolvedor    | `/dev/listar`, `/dev/mostrar/{id}` | `/dev/salvar` | `/dev/atribuirEquipe`, `/dev/atribuirProjeto` |
| Autenticação     | `/auth/me` | `/auth/login`, `/auth/logout` | — |

Observações sobre o contrato do backend:

- **Não existe DELETE.** A exclusão é lógica: `PUT .../setStatus/{id}` alterna entre ativo e inativo (o frontend pede
  confirmação). As listagens só mostram os ativos; para reativar, digite o ID.
- Os parâmetros são `@RequestParam`: vão na query string no GET e no corpo `application/x-www-form-urlencoded` no POST/PUT.
- POST/PUT e as consultas de detalhe (`mostrar`, `progresso`) devolvem **texto com status 200**, inclusive quando
  uma regra de negócio recusa a operação. O frontend mostra a mensagem do backend como ela veio.
- Parâmetro ausente ou em formato errado gera `400`; sem login, `401`; sem permissão, `403`.

## Como a tela funciona

- **Barra lateral:** usuário logado, opção de mostrar operações sem permissão, botão Sair e a lista de entidades com
  suas operações.
- **Funcionalidades disponíveis:** tabela com método, endpoint, descrição e perfis de cada operação.
- **Tela de operação:** mostra o endpoint, o formulário com o nome real de cada parâmetro e onde ele vai (caminho,
  query ou corpo), e o resultado: método, caminho, corpo enviado (a senha aparece mascarada), status HTTP, tempo,
  mensagem e o JSON da resposta. Listagens aparecem em tabela.
- Nos campos de ID, o navegador sugere os registros existentes. Nas telas de atualização, escolher o ID preenche o
  formulário com os dados atuais. Depois de um POST/PUT, a tabela "Registros atuais" é recarregada.

## Ajustes feitos no backend

Mudanças mínimas para o login real e para o frontend conseguir chamar a API:

- `pom.xml`: dependência `spring-boot-starter-security`.
- `Usuario`: campo `senha` (BCrypt, com `@JsonIgnore`).
- `UsuarioRepository` (novo): busca usuário por e-mail.
- `UsuarioService` (novo): implementa `UserDetailsService` (login por e-mail, perfil pelo tipo do usuário).
- `SecurityConfig` (novo): login/logout por sessão, regras por perfil, respostas 401/403 em vez de redirecionamento,
  CSRF e CORS.
- `AuthController` e `UsuarioLogadoDTO` (novos): `GET /auth/me`.
- `GestorEquipeService` / `DesenvolvedorService` e seus Controllers: o cadastro recebe `senha` (obrigatória) e não
  aceita e-mail repetido, porque o e-mail é o login.
- `DadosIniciais`: define a senha inicial dos usuários sem senha.
- `SwaggerConfig` (novo): mostra o login e o logout no Swagger.
- `application.properties`: `app.cors.origens`, `app.senha-inicial` e CSRF no Swagger.

**CSRF:** continua ativo. O Spring Security envia o token no cookie `XSRF-TOKEN` e o Axios devolve no cabeçalho
`X-XSRF-TOKEN` em POST/PUT.

**CORS:** liberado só para `http://localhost:5173`, com credenciais (cookies). Para outra origem, defina a variável
`CORS_ORIGENS` no backend (separe vários endereços por vírgula).

**Swagger:** continua em `http://localhost:8080/swagger-ui.html`. O `SwaggerConfig` (novo) mostra o `POST /auth/login`
(formulário com `email` e `senha`) e o `POST /auth/logout` no grupo **auth-controller**, porque eles são filtros do
Spring Security e não aparecem sozinhos. Isso só muda a documentação. Depois do login pelo Swagger, o navegador guarda
o cookie da sessão, e o Swagger envia o token CSRF nos POST/PUT (`springdoc.swagger-ui.csrf.enabled=true`).

## Estrutura

```
src/
  auth/         AuthContext (sessão) e RotaProtegida
  components/   Layout, Sidebar, FormularioOperacao, TabelaRegistros, PainelResultado, MetodoTag
  hooks/        useListagens (carrega as listagens usadas nas tabelas e sugestões de ID)
  pages/        LoginPage, InicioPage, OperacaoPage
  services/     http (Axios), authService, apiService, endpoints (mapa dos endpoints)
  styles/       global.css
  types/        contratos da API e tipos do mapa de endpoints
```
