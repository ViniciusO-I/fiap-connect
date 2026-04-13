# FIAP Connect

Plataforma web de formação de grupos por habilidades, desenvolvida com Spring Boot 3, autenticação OAuth2 via GitHub e banco de dados H2 em memória.

---

## Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

| Ferramenta | Versão mínima |
|------------|--------------|
| Java (JDK) | 21           |
| Maven      | 3.9+         |
| Conta no GitHub | — (para criar o OAuth App) |

Para verificar suas versões:
```bash
java -version
mvn -version
```

---

## 1. Configurar o OAuth App no GitHub

O projeto usa login via GitHub. Você precisa criar um OAuth App para obter as credenciais.

1. Acesse [github.com/settings/developers](https://github.com/settings/developers)
2. Clique em **"New OAuth App"**
3. Preencha os campos:
   - **Application name:** `fiap-connect` (ou qualquer nome)
   - **Homepage URL:** `http://localhost:8080`
   - **Authorization callback URL:** `http://localhost:8080/login/oauth2/code/github`
4. Clique em **"Register application"**
5. Copie o **Client ID**
6. Clique em **"Generate a new client secret"** e copie o **Client Secret**

---

## 2. Configurar as credenciais no projeto

Abra o arquivo `src/main/resources/application.properties` e substitua os valores de placeholder pelas credenciais geradas no passo anterior:

```properties
spring.security.oauth2.client.registration.github.client-id=SEU_CLIENT_ID_AQUI
spring.security.oauth2.client.registration.github.client-secret=SEU_CLIENT_SECRET_AQUI
```

---

## 3. Rodar o projeto

Na raiz do projeto (onde está o `pom.xml`), execute:

```bash
mvn spring-boot:run
```

Aguarde a mensagem de inicialização:
```
Started FiapConnectApplication in X seconds
```

Acesse no navegador: [http://localhost:8080](http://localhost:8080)

---

## 4. Primeiro acesso e configuração do perfil de Administrador

O banco de dados é criado em memória a cada execução e populado automaticamente com dados de seed (Flyway). Para ter acesso como **ADMINISTRATOR** (que permite criar/editar/deletar skills), siga os passos:

1. Faça login com sua conta GitHub em [http://localhost:8080](http://localhost:8080)
2. Acesse o console H2 em [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
   - **JDBC URL:** `jdbc:h2:mem:fiapconnectdb`
   - **User Name:** `sa`
   - **Password:** *(deixe em branco)*
3. Execute a query abaixo substituindo pelo seu e-mail do GitHub:

```sql
-- Descubra seu e-mail cadastrado automaticamente pelo login:
SELECT * FROM USER_ENTITY;

-- Depois atualize o profile para ADMINISTRATOR:
UPDATE USER_ENTITY SET PROFILE = 'ADMINISTRATOR' WHERE EMAIL = 'seu-login-github@github';
```

> **Como funciona o e-mail:** o sistema grava o usuário com e-mail no formato `{login-do-github}@github`. Por exemplo, se seu login no GitHub é `joaosilva`, seu e-mail no sistema será `joaosilva@github`.

---

## Funcionalidades

| Perfil        | Permissões                                                                 |
|---------------|---------------------------------------------------------------------------|
| `STUDENT`     | Ver skills, ver grupos, entrar em grupos, gerenciar próprio perfil        |
| `ADMINISTRATOR` | Tudo do STUDENT + criar, editar e deletar skills                       |

### Rotas disponíveis

| Método | Rota                      | Descrição                            |
|--------|---------------------------|--------------------------------------|
| GET    | `/`                       | Página inicial                        |
| GET    | `/users`                  | Lista todos os usuários              |
| GET    | `/users/profile`          | Perfil do usuário logado             |
| POST   | `/users/profile/skills`   | Adicionar skills ao próprio perfil   |
| GET    | `/groups`                 | Lista todos os grupos                |
| GET    | `/groups/{id}`            | Detalhe de um grupo                  |
| GET    | `/groups/new`             | Formulário para criar grupo          |
| POST   | `/groups/new`             | Salvar novo grupo                    |
| GET    | `/groups/edit/{id}`       | Formulário para editar grupo         |
| POST   | `/groups/edit/{id}`       | Salvar edição de grupo               |
| POST   | `/groups/delete/{id}`     | Deletar grupo                        |
| POST   | `/groups/{id}/join`       | Entrar em um grupo                   |
| GET    | `/skills`                 | Lista todas as skills                |
| GET    | `/skills/new`             | Formulário para criar skill *(ADMIN)*|
| POST   | `/skills/new`             | Salvar nova skill *(ADMIN)*          |
| GET    | `/skills/edit/{id}`       | Formulário para editar skill *(ADMIN)*|
| POST   | `/skills/edit/{id}`       | Salvar edição de skill *(ADMIN)*     |
| POST   | `/skills/delete/{id}`     | Deletar skill *(ADMIN)*              |
| GET    | `/h2-console`             | Console do banco H2 (dev)            |

---

## Dados de seed (populados automaticamente)

O Flyway executa automaticamente as migrations ao iniciar. Os dados iniciais incluem:

**Usuários:**
- `Admin FIAP` — perfil `ADMINISTRATOR` — email `admin@github`
- `Estudante Demo` — perfil `STUDENT` — email `estudante@github`

**Skills pré-cadastradas:** Java, Spring Boot, React, SQL, DevOps, UX/UI Design, Python, Docker

**Grupo de exemplo:** "Projeto Full Stack - Turma A" (máx. 4 membros, requer Java + SQL)

---

## Stack tecnológica

- **Java 21** + **Spring Boot 3.4.4**
- **Spring Security** + **OAuth2** (GitHub)
- **Spring Data JPA** + **Hibernate**
- **Flyway** (migrations)
- **H2** (banco em memória)
- **Thymeleaf** (templates)
- **Lombok** + **MapStruct**
- **Maven**

---

## Observações importantes

- O banco H2 é **em memória**: todos os dados são perdidos ao reiniciar a aplicação. Isso é esperado para ambiente de desenvolvimento.
- O login é **exclusivamente via GitHub**. Não há formulário de usuário/senha.
- Ao fazer login pela primeira vez com uma conta GitHub nova, o sistema cria automaticamente um usuário com perfil `STUDENT`.
