# OrgSync

OrgSync é uma aplicação de gerenciamento organizacional que permite sincronizar e gerenciar departamentos e usuários de forma eficiente.

Front-end: https://github.com/lithoykai/orgsync_frontend


## 🚀 Funcionalidades

- Autenticação e autorização com JWT
- Gerenciamento de usuários
- Gerenciamento de departamentos
- Sistema de roles (ADMIN e USER)
- API RESTful documentada com Swagger

## 🛠️ Tecnologias Utilizadas

- **Backend**:
  - Java 17
  - Spring Boot
  - Spring Security
  - JWT (JSON Web Tokens)
  - JPA/Hibernate
  - BCrypt para criptografia de senhas

- **Frontend**:
  - Flutter/Dart
  - Dio para requisições HTTP

## 📋 Pré-requisitos

- Java JDK 17 ou superior
- Maven
- Flutter SDK
- IDE de sua preferência (recomendado: IntelliJ IDEA, VS Code)

## 🔧 Configuração do Ambiente

### Backend

1. Clone o repositório:
```bash
git clone https://github.com/lithoykai/orgsync_backend.git
```

2. Gere seu par de chaves RSA com o OpenSSL.

```bash
openssl genpkey -algorithm RSA -out app.key -pkeyopt rsa_keygen_bits:2048

openssl rsa -pubout -in app.key -out app.pub

```

Após rodar, coloque os arquivos na pasta `src/main/resources/`

3. Configure as variáveis de ambiente no arquivo `application.properties` e caso for rodar no Docker, alterar as variveis lá:
```properties
jwt.public.key=classpath:certs/public.pem
jwt.private.key=classpath:certs/private.pem
orgsync.default.email=[email-admin]
orgsync.default.password=[senha-admin]
```

4. Execute o projeto:
```bash
mvn spring-boot:run
```

O servidor estará disponível em `http://localhost:8080`

### Frontend

1. Navegue até a pasta do frontend (caso tenha baixado):
```bash
cd frontend
```

2. Instale as dependências:
```bash
flutter pub get
```

3. Execute o projeto:
```bash
flutter run
```

## 🔒 Segurança

- Autenticação baseada em JWT
- Senhas criptografadas com BCrypt
- CORS configurado para segurança
- Endpoints protegidos por roles

## 📚 Documentação da API

A documentação completa da API está disponível através do Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

### Endpoints Principais

Verifique no Swagger os demais endpoints.

- **Autenticação**:
  - POST `/auth/login` - Login de usuário
  - POST `/auth/register` - Registro de novo usuário

- **Usuários**:
  - GET `/api/users/all` - Lista todos os usuários (requer ADMIN)
  - GET `/api/users/department/{id}` - Lista usuários por departamento

- **Departamentos**:
  - GET `/api/department/all` - Lista todos os departamentos
  - POST `/api/department/` - Cria novo departamento (requer ADMIN)
  - PUT `/api/department/{id}` - Atualiza departamento (requer ADMIN)
  - DELETE `/api/department/{id}` - Remove departamento (requer ADMIN)

## 👥 Roles do Sistema

- **ADMIN**: Acesso total ao sistema
- **USER**: Acesso limitado às funcionalidades básicas
