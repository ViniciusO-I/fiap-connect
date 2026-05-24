# FIAP Connect

Plataforma de formação de grupos por habilidades para estudantes da FIAP.

## Sobre o projeto

O FIAP Connect permite que estudantes se conectem e formem grupos de projeto com base em suas habilidades técnicas. O sistema utiliza autenticação via GitHub OAuth2 e gerencia grupos, skills e membros com regras de negócio bem definidas.

## Tecnologias

- Java 21
- Spring Boot 3.4.4
- Spring Security + OAuth2 (GitHub)
- Spring Data JPA + Hibernate
- Flyway (migrações de banco)
- Thymeleaf + Bootstrap 5
- H2 (desenvolvimento) / MySQL (produção)
- Railway (deploy)

## Arquitetura

```
controller/   → recebe requisições HTTP, delega para services
service/      → regras de negócio
repository/   → acesso ao banco via Spring Data JPA
dto/          → objetos de transferência (Java Records)
exception/    → hierarquia de exceções de domínio
security/     → integração OAuth2 GitHub
```

## Como rodar localmente

### Pré-requisitos

- Java 21
- Maven 3.9+
- Conta no GitHub com um OAuth App configurado

### Configurar o GitHub OAuth App

1. Acesse `https://github.com/settings/developers`
2. Clique em **New OAuth App**
3. Preencha:
   - **Homepage URL:** `http://localhost:8080`
   - **Callback URL:** `http://localhost:8080/login/oauth2/code/github`
4. Gere um **Client Secret** e guarde

### Configurar variáveis de ambiente

Defina as variáveis no seu sistema operacional ou IDE antes de rodar:

```bash
export GITHUB_CLIENT_ID=seu_client_id_aqui
export GITHUB_CLIENT_SECRET=seu_client_secret_aqui
```

No IntelliJ, vá em **Run > Edit Configurations > Environment Variables** e adicione as duas variáveis.

### Rodar a aplicação

```bash
mvn spring-boot:run
```

Acesse em: `http://localhost:8080`

O banco H2 é criado em memória automaticamente via Flyway. O console H2 está disponível em `http://localhost:8080/h2-console`.

### Usuário administrador padrão

O seed de dados cria automaticamente um usuário administrador associado ao login do GitHub `ViniciusO-I`. Para usar outro login, altere o email em `V2__seed_data.sql`:

```sql
INSERT INTO user_entity (..., email, ...) VALUES (..., 'seu-login@github', ...);
```

## Deploy (Railway)

### Variáveis de ambiente necessárias no Railway

| Variável | Descrição |
|---|---|
| `DATABASE_URL` | URL JDBC do MySQL fornecida pelo Railway |
| `DATABASE_USERNAME` | Usuário do banco MySQL |
| `DATABASE_PASSWORD` | Senha do banco MySQL |
| `GITHUB_CLIENT_ID` | Client ID do GitHub OAuth App |
| `GITHUB_CLIENT_SECRET` | Client Secret do GitHub OAuth App |
| `SPRING_PROFILES_ACTIVE` | Deve ser `prod` |

### Atualizar o GitHub OAuth App para produção

Após obter a URL do Railway, atualize o OAuth App em `https://github.com/settings/developers`:

- **Homepage URL:** `https://sua-url.railway.app`
- **Callback URL:** `https://sua-url.railway.app/login/oauth2/code/github`

## Funcionalidades

- Login e logout via GitHub OAuth2
- Cadastro automático de novos usuários ao fazer login
- Perfil do usuário com gerenciamento de skills
- Listagem de grupos de projeto
- Criação e edição de grupos com skills obrigatórias
- Entrada em grupos com validação de vagas e skills
- Gerenciamento de skills (somente administrador)
- Controle de acesso por perfil (ADMINISTRATOR / STUDENT)

## Regras de negócio — entrar em um grupo

1. O usuário não pode já ser membro do grupo
2. O grupo deve ter vagas disponíveis
3. O usuário deve possuir todas as skills obrigatórias do grupo

## Estrutura de banco

- `user_entity` — usuários
- `skill_entity` — skills cadastradas
- `group_entity` — grupos de projeto
- `user_skill` — skills de cada usuário
- `group_member` — membros de cada grupo
- `group_skill_requirement` — skills obrigatórias de cada grupo
# CI/CD test Sun May 24 13:17:04 -03 2026
