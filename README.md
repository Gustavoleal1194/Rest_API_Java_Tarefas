# Agendador de Tarefas

API REST desenvolvida em Java 17 com Spring Boot para gerenciamento de usuarios, tarefas e etiquetas.

O projeto foi criado para a disciplina de Programacao Web Java e implementa uma arquitetura em camadas com controllers, services, repositories, DTOs, validacoes e tratamento global de excecoes.

## Sobre o projeto

O Agendador de Tarefas permite:

- cadastrar usuarios
- cadastrar tarefas para usuarios
- cadastrar etiquetas
- vincular varias etiquetas a uma tarefa
- listar tarefas por usuario
- listar etiquetas de uma tarefa
- listar tarefas vinculadas a uma etiqueta
- realizar remocao logica de usuarios, tarefas e etiquetas

O projeto e somente backend. Nao possui frontend, login, autenticacao, notificacoes, calendario avancado ou envio de e-mail.

## Tecnologias

- Java 17
- Spring Boot 4.0.4
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation com `jakarta.validation`
- MySQL
- Maven

## Requisitos

Antes de executar, tenha instalado:

- JDK 17
- Maven
- MySQL

Para verificar:

```bash
java -version
mvn -version
mysql --version
```

No Windows, caso Maven ou MySQL nao estejam instalados, uma opcao e usar:

```bash
winget install Apache.Maven
winget install Oracle.MySQL
```

Depois da instalacao, feche e abra novamente o terminal.

## Configuracao do banco

As configuracoes principais ficam em `src/main/resources/application.properties`:

```properties
spring.application.name=agendador-tarefas

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/agendador_tarefas_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASS:}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=${SERVER_PORT:8080}
```

Por padrao, a aplicacao tenta acessar o MySQL local com:

- banco: `agendador_tarefas_db`
- usuario: `root`
- senha vazia
- porta da API: `8080`

Se o MySQL tiver senha, configure as variaveis de ambiente antes de rodar.

PowerShell:

```powershell
$env:DB_USER="root"
$env:DB_PASS="SUA_SENHA"
```

Bash:

```bash
export DB_USER=root
export DB_PASS=SUA_SENHA
```

Tambem e possivel alterar a porta:

```powershell
$env:SERVER_PORT="8081"
```

## Como executar

Na pasta raiz do projeto:

```bash
mvn clean install
mvn spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

## Estrutura do projeto

```text
src/main/java/com/gustavo/agendadortarefas
├── controller   # endpoints REST
├── dto          # objetos de entrada e saida da API
├── exception    # excecoes e tratamento global de erros
├── model        # entidades JPA e enums
├── repository   # acesso ao banco com Spring Data JPA
└── service      # regras de negocio
```

## Entidades principais

### Usuario

Campos:

- `id`
- `nome`
- `email`
- `ativo`
- `tarefas`

Regras:

- `nome` e obrigatorio
- `nome` deve ter entre 3 e 120 caracteres
- `email` e obrigatorio
- `email` deve ter formato valido
- `email` deve ter no maximo 120 caracteres
- nao permite email duplicado
- usuario com tarefas ativas nao pode ser removido

### Tarefa

Campos:

- `id`
- `titulo`
- `descricao`
- `dataLimite`
- `status`
- `prioridade`
- `ativo`
- `usuario`
- `etiquetas`

Valores aceitos para `status`:

- `PENDENTE`
- `EM_ANDAMENTO`
- `CONCLUIDA`
- `CANCELADA`

Valores aceitos para `prioridade`:

- `BAIXA`
- `MEDIA`
- `ALTA`

Regras:

- `titulo` e obrigatorio
- `titulo` deve ter entre 3 e 120 caracteres
- `descricao` pode ter no maximo 500 caracteres
- `status` e obrigatorio
- `prioridade` e obrigatoria
- `usuarioId` e obrigatorio
- `dataLimite` e opcional

### Etiqueta

Campos:

- `id`
- `nome`
- `descricao`
- `ativo`
- `tarefas`

Regras:

- `nome` e obrigatorio
- `nome` deve ter entre 3 e 80 caracteres
- `nome` nao pode ser duplicado
- `descricao` pode ter no maximo 300 caracteres

## Relacionamentos

### Usuario e tarefa

Um usuario pode ter varias tarefas, e cada tarefa pertence a um usuario.

### Tarefa e etiqueta

Uma tarefa pode ter varias etiquetas, e uma etiqueta pode estar vinculada a varias tarefas.

Esse relacionamento muitos-para-muitos usa a tabela intermediaria:

```text
tarefa_etiqueta
- tarefa_id
- etiqueta_id
```

Os controllers retornam DTOs em vez de entidades JPA diretamente, evitando loops no JSON.

## Endpoints

### Usuarios

```text
GET    /api/usuarios
GET    /api/usuarios/{id}
POST   /api/usuarios
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}
GET    /api/usuarios/{id}/tarefas
```

### Tarefas

```text
GET    /api/tarefas
GET    /api/tarefas/{id}
POST   /api/tarefas
PUT    /api/tarefas/{id}
DELETE /api/tarefas/{id}
GET    /api/tarefas/{tarefaId}/etiquetas
POST   /api/tarefas/{tarefaId}/etiquetas/{etiquetaId}
DELETE /api/tarefas/{tarefaId}/etiquetas/{etiquetaId}
```

### Etiquetas

```text
GET    /api/etiquetas
GET    /api/etiquetas/{id}
POST   /api/etiquetas
PUT    /api/etiquetas/{id}
DELETE /api/etiquetas/{id}
GET    /api/etiquetas/{etiquetaId}/tarefas
```

## Exemplos de requisicoes

### Criar usuario

`POST /api/usuarios`

```json
{
  "nome": "Gustavo Leal",
  "email": "gustavo@email.com"
}
```

### Criar tarefa

`POST /api/tarefas`

```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar JPA, DTOs e validacoes",
  "dataLimite": "2026-05-14",
  "status": "PENDENTE",
  "prioridade": "ALTA",
  "usuarioId": 1
}
```

### Criar etiqueta

`POST /api/etiquetas`

```json
{
  "nome": "Faculdade",
  "descricao": "Atividades relacionadas a faculdade"
}
```

### Vincular etiqueta a tarefa

`POST /api/tarefas/1/etiquetas/1`

### Atualizar usuario

`PUT /api/usuarios/1`

```json
{
  "nome": "Gustavo Leal",
  "email": "gustavo.leal@email.com"
}
```

### Atualizar tarefa

`PUT /api/tarefas/1`

```json
{
  "titulo": "Estudar Spring Boot e Hibernate",
  "descricao": "Revisar JPA, DTOs, validacoes e relacionamento muitos-para-muitos",
  "dataLimite": "2026-05-14",
  "status": "EM_ANDAMENTO",
  "prioridade": "ALTA",
  "usuarioId": 1
}
```

### Atualizar etiqueta

`PUT /api/etiquetas/1`

```json
{
  "nome": "Faculdade",
  "descricao": "Trabalhos e estudos da faculdade"
}
```

## Tratamento de erros

O projeto possui tratamento global em `GlobalExceptionHandler`.

Erros tratados:

- recurso nao encontrado
- regra de negocio invalida
- erro de validacao
- JSON invalido
- valor invalido para enum
- erro inesperado

Exemplo de resposta:

```json
{
  "dataHora": "2026-05-07T23:59:00",
  "status": 400,
  "erro": "Erro de validacao",
  "mensagens": [
    "O titulo da tarefa e obrigatorio"
  ]
}
```

## Roteiro de testes manuais

Uma sequencia recomendada para testar a API:

1. Criar usuario
2. Criar etiqueta
3. Criar tarefa usando o `usuarioId`
4. Listar tarefas
5. Buscar tarefa por id
6. Atualizar tarefa
7. Vincular etiqueta a tarefa
8. Listar etiquetas da tarefa
9. Listar tarefas da etiqueta
10. Listar tarefas do usuario
11. Remover vinculo entre tarefa e etiqueta
12. Remover tarefa
13. Remover etiqueta
14. Remover usuario

Tambem teste cenarios de erro:

- buscar tarefa inexistente
- criar usuario com email invalido
- criar usuario com email duplicado
- criar tarefa sem titulo
- criar tarefa sem usuario
- criar etiqueta duplicada
- vincular etiqueta inexistente
- vincular a mesma etiqueta duas vezes
- remover usuario com tarefas ativas

## Observacoes

- As remocoes sao logicas: os registros recebem `ativo = false`.
- As listagens principais retornam apenas registros ativos.
- Ao remover uma tarefa, os vinculos com etiquetas sao removidos.
- Ao remover uma etiqueta, ela tambem e desvinculada das tarefas.
