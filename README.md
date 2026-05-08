# Agendador de Tarefas

API REST backend em Java 17 com Spring Boot 4.0.4 para o trabalho final da disciplina de Programacao Web Java.

## Descricao

O sistema permite cadastrar tarefas e etiquetas, alem de associar varias etiquetas a uma tarefa. O projeto nao possui frontend, autenticacao, login, notificacoes, calendario avancado ou envio de e-mail.

## Tema escolhido

API Agendador de Tarefas.

## Tecnologias utilizadas

- Java 17
- Spring Boot 4.0.4
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Bean Validation com `jakarta.validation`
- Maven

## Requisitos para rodar

- JDK 17 instalado
- Maven instalado
- MySQL instalado e rodando

Comandos para verificar:

```bash
java -version
mvn -version
mysql --version
```

Se algum comando nao for reconhecido no terminal, no Windows uma forma objetiva de instalar e:

```bash
winget install Apache.Maven
winget install Oracle.MySQL
```

Depois feche e abra novamente o terminal e rode novamente `mvn -version` e `mysql --version`.

## Banco de dados

Crie o banco e o usuario da aplicacao no MySQL:

```sql
CREATE DATABASE agendador_tarefas_db;
CREATE USER IF NOT EXISTS 'agendador_user'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON agendador_tarefas_db.* TO 'agendador_user'@'localhost';
FLUSH PRIVILEGES;
```

## Configuracao

O projeto ja esta configurado para usar um usuario proprio da aplicacao:

```properties
spring.application.name=agendador-tarefas

spring.datasource.url=jdbc:mysql://localhost:3306/agendador_tarefas_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
spring.datasource.username=agendador_user
spring.datasource.password=123456

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

Se quiser trocar a senha, altere no MySQL e atualize `spring.datasource.password`.

## Como executar

Na pasta do projeto:

```bash
mvn clean install
mvn spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

## Entidades

### Tarefa

Campos principais:

- `id`
- `titulo`
- `descricao`
- `dataLimite`
- `status`
- `prioridade`
- `ativo`
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

### Etiqueta

Campos principais:

- `id`
- `nome`
- `descricao`
- `ativo`
- `tarefas`

## Relacionamento muitos-para-muitos

Uma tarefa pode ter varias etiquetas, e uma etiqueta pode estar em varias tarefas.

O relacionamento e implementado com `@ManyToMany` e a tabela intermediaria:

```text
tarefa_etiqueta
- tarefa_id
- etiqueta_id
```

Os controllers retornam DTOs, nao entidades JPA diretamente, para evitar loop infinito no JSON.

## Validacoes

### Tarefa

- `titulo` e obrigatorio
- `titulo` deve ter entre 3 e 120 caracteres
- `descricao` pode ter no maximo 500 caracteres
- `status` e obrigatorio
- `prioridade` e obrigatoria
- `dataLimite` e opcional

### Etiqueta

- `nome` e obrigatorio
- `nome` deve ter entre 3 e 80 caracteres
- `nome` nao pode ser duplicado
- `descricao` pode ter no maximo 300 caracteres

## Tratamento de excecoes

O projeto possui tratamento global em `GlobalExceptionHandler`.

Erros tratados:

- Recurso nao encontrado
- Erro de regra de negocio
- Erros de validacao do Bean Validation
- JSON invalido ou valor invalido em enum
- Erro inesperado

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

## Endpoints

### Tarefas

```text
GET    /api/tarefas
GET    /api/tarefas/{id}
POST   /api/tarefas
PUT    /api/tarefas/{id}
DELETE /api/tarefas/{id}
```

### Etiquetas

```text
GET    /api/etiquetas
GET    /api/etiquetas/{id}
POST   /api/etiquetas
PUT    /api/etiquetas/{id}
DELETE /api/etiquetas/{id}
```

### Relacionamento

```text
POST   /api/tarefas/{tarefaId}/etiquetas/{etiquetaId}
DELETE /api/tarefas/{tarefaId}/etiquetas/{etiquetaId}
GET    /api/tarefas/{tarefaId}/etiquetas
GET    /api/etiquetas/{etiquetaId}/tarefas
```

## Exemplos de requisicoes

### Criar tarefa

`POST /api/tarefas`

```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar JPA, DTOs e validacoes",
  "dataLimite": "2026-05-14",
  "status": "PENDENTE",
  "prioridade": "ALTA"
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

### Atualizar tarefa

`PUT /api/tarefas/1`

```json
{
  "titulo": "Estudar Spring Boot e Hibernate",
  "descricao": "Revisar JPA, DTOs, validacoes e relacionamento muitos-para-muitos",
  "dataLimite": "2026-05-14",
  "status": "EM_ANDAMENTO",
  "prioridade": "ALTA"
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

## Roteiro de testes manuais

1. Criar etiqueta
2. Criar tarefa
3. Listar tarefas
4. Buscar tarefa por id
5. Atualizar tarefa
6. Vincular etiqueta a tarefa
7. Listar etiquetas da tarefa
8. Listar tarefas da etiqueta
9. Remover vinculo
10. Remover tarefa
11. Remover etiqueta

Tambem teste erros:

- Buscar tarefa inexistente
- Criar tarefa sem titulo
- Criar etiqueta duplicada
- Vincular etiqueta inexistente
- Vincular a mesma etiqueta duas vezes

## Arquitetura

O projeto usa arquitetura em camadas:

```text
controller  -> recebe as requisicoes HTTP
service     -> contem regras de negocio
repository  -> acesso ao banco com Spring Data JPA
model       -> entidades JPA e enums
dto         -> objetos de entrada e saida da API
exception   -> excecoes e tratamento global
```
