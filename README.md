# FIAP Connect

Plataforma de formação de grupos por habilidades para projetos acadêmicos da FIAP.

## Integrantes

| Nome | RM |
|------|-----|
| Vinicius Oliveira | RM559611 |
| Alexis Ronaldo Quirijota Rondo | RM560384 |

## Sobre o Projeto

O FIAP Connect conecta estudantes com habilidades complementares para formação de grupos de alto desempenho. A plataforma permite login via GitHub OAuth2, gerenciamento de skills e criação de grupos com validação de habilidades obrigatórias.

## Tecnologias

- Java 21 + Spring Boot 3.4
- Spring Security + OAuth2 GitHub
- Thymeleaf + Bootstrap 5
- H2 Database + Flyway
- Azure App Service
- Azure DevOps CI/CD

## Links

- GitHub: https://github.com/ViniciusO-I/fiap-connect
- Deploy Azure: https://app-ppt-fiap-connect-sprint4.azurewebsites.net
- Deploy Render: https://fiap-connect.onrender.com

## Pipeline CI/CD

- CI: Azure DevOps - Sprint-4-Maven-CI
- CD: Azure DevOps - New release pipeline
- Trigger: automático a cada push na branch main

## Como executar localmente

1. Clone o repositório
2. Configure as variáveis de ambiente no IntelliJ:
   - GITHUB_CLIENT_ID=seu_client_id
   - GITHUB_CLIENT_SECRET=seu_client_secret
3. Execute a aplicação
4. Acesse http://localhost:8080

## Banco de Dados

H2 in-memory populado automaticamente via Flyway com dados de demonstração.
Console H2: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:fiapconnectdb
- User: sa
- Password: (em branco)
