# Board de Tarefas em Java

Projeto criado para o desafio da DIO **Projeto - Board de tarefas**. Uma aplicação funcional de board de tarefas (Kanban) desenvolvida em Java puro com JDBC, Liquibase, MySQL e Gradle.

## Sobre o Projeto

Este é um **board de tarefas** simplificado, similar a um Kanban, onde usuários podem:

- Criar boards (tabuleiros)
- Gerenciar cards (cartões de tarefas)
- Mover cards entre colunas
- Cancelar cards
- Bloquear/desbloquear cards com motivos
- Visualizar detalhes do board e dos cards

A aplicação é executada via **terminal** e utiliza:

- **Java 17** como linguagem
- **MySQL** como banco de dados
- **Liquibase** para versionamento e migração do banco
- **JDBC puro** para persistência
- **Gradle com Kotlin DSL** como build tool
- **Lombok** para reduzir boilerplate (opcional)

## Arquitetura e Camadas

```
src/main/java/br/com/dio
├── Main.java                        # Ponto de entrada
├── dto/                             # Objetos de transferência de dados
│   ├── BoardDetailsDTO.java
│   ├── BoardColumnInfoDTO.java
│   ├── CardDetailsDTO.java
│   └── CardSummaryDTO.java
├── exception/                       # Exceções customizadas
│   ├── EntityNotFoundException.java
│   ├── CardBlockedException.java
│   ├── CardFinishedException.java
│   ├── InvalidOperationException.java
│   └── InvalidBoardOperationException.java
├── persistence/
│   ├── config/
│   │   └── ConnectionConfig.java        # Configuração JDBC
│   ├── converter/
│   │   └── BoardColumnKindConverter.java # Conversor de tipos
│   ├── dao/                         # Data Access Object (JDBC)
│   │   ├── BoardDAO.java
│   │   ├── BoardColumnDAO.java
│   │   ├── CardDAO.java
│   │   └── BlockDAO.java
│   ├── entity/                      # Entidades do domínio
│   │   ├── BoardEntity.java
│   │   ├── BoardColumnEntity.java
│   │   ├── CardEntity.java
│   │   ├── BlockEntity.java
│   │   └── BoardColumnKindEnum.java
│   └── migration/
│       └── MigrationStrategy.java       # Liquibase integration
├── service/                         # Lógica de negócio
│   ├── BoardService.java
│   ├── CardService.java
│   └── BoardQueryService.java
└── ui/                              # Interface de usuário
    ├── MainMenu.java
    ├── BoardMenu.java
    └── InputReader.java
```

### Camadas Explicadas

- **UI**: Interface de terminal com menus interativos
- **Service**: Lógica de negócio (validações, regras)
- **DAO**: Acesso ao banco de dados via JDBC
- **Entity**: Representação das tabelas
- **DTO**: Objetos para transferência de dados sem expor entidades
- **Config**: Configuração de conexões e ambiente
- **Migration**: Versionamento de banco com Liquibase

## Funcionalidades

### Board

- ✅ Criar novo board com colunas padrão (A Fazer, Em Andamento, Concluído, Cancelado)
- ✅ Listar todos os boards
- ✅ Ver detalhes do board
- ✅ Excluir board
- ✅ Selecionar board para gerenciar

### Cards

- ✅ Criar cards na coluna inicial
- ✅ Mover cards para próxima coluna
- ✅ Cancelar cards
- ✅ Ver cards por coluna
- ✅ Ver detalhes do card

### Bloqueios

- ✅ Bloquear cards com motivo
- ✅ Desbloquear cards com motivo
- ✅ Histórico de bloqueios
- ✅ Validação: cards bloqueados não podem ser movidos

### Regras de Negócio

- Cards começam na coluna **A Fazer**
- Cards podem avançar coluna por coluna
- Cards podem ser cancelados (coluna **Cancelado**)
- Cards na coluna **Concluído** ou **Cancelado** não podem ser movidos
- Cards bloqueados precisam ser desbloqueados antes de mover
- Bloqueio e desbloqueio requerem motivo

## Dependências

```gradle
dependencies {
    implementation("org.liquibase:liquibase-core:4.29.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}
```

## Configuração do Banco de Dados

### Pré-requisitos

- MySQL instalado e rodando
- Usuário e senha do MySQL configurados

### Criar o Banco

```sql
CREATE DATABASE board;
```

### Configurar Variáveis de Ambiente

#### No Linux/Mac:

```bash
export DB_URL="jdbc:mysql://localhost:3306/board"
export DB_USER="root"
export DB_PASSWORD="sua_senha"
```

#### No Windows PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/board"
$env:DB_USER="root"
$env:DB_PASSWORD="sua_senha"
```

#### No Windows CMD:

```cmd
set DB_URL=jdbc:mysql://localhost:3306/board
set DB_USER=root
set DB_PASSWORD=sua_senha
```

### Fallback

Se as variáveis de ambiente não estiverem definidas, a aplicação usará valores padrão:

- `DB_URL`: `jdbc:mysql://localhost:3306/board`
- `DB_USER`: `root`
- `DB_PASSWORD`: `root`

## Como Executar

### Build do Projeto

```bash
# Linux/Mac
./gradlew build

# Windows
.\gradlew.bat build
```

### Executar a Aplicação

#### Linux/Mac:

```bash
export DB_URL="jdbc:mysql://localhost:3306/board"
export DB_USER="root"
export DB_PASSWORD="root"
./gradlew run
```

#### Windows PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/board"
$env:DB_USER="root"
$env:DB_PASSWORD="root"
.\gradlew.bat run
```

#### Windows CMD:

```cmd
set DB_URL=jdbc:mysql://localhost:3306/board
set DB_USER=root
set DB_PASSWORD=root
.\gradlew.bat run
```

## Estrutura do Banco de Dados

### Tabela: BOARDS

```sql
CREATE TABLE BOARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tabela: BOARD_COLUMNS

```sql
CREATE TABLE BOARD_COLUMNS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position INT NOT NULL,
    kind VARCHAR(50) NOT NULL,
    board_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE
);
```

### Tabela: CARDS

```sql
CREATE TABLE CARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    board_column_id BIGINT NOT NULL,
    FOREIGN KEY (board_column_id) REFERENCES BOARD_COLUMNS(id) ON DELETE CASCADE
);
```

### Tabela: BLOCKS

```sql
CREATE TABLE BLOCKS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason TEXT NOT NULL,
    unblocked_at TIMESTAMP NULL,
    unblock_reason TEXT NULL,
    card_id BIGINT NOT NULL,
    FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
);
```

## Migrações

As migrações são executadas automaticamente ao iniciar a aplicação via **Liquibase**.

Arquivos de migração localizados em:

```
src/main/resources/db/changelog/migrations/
├── db.changelog-001-create-boards.sql
├── db.changelog-002-create-board-columns.sql
├── db.changelog-003-create-cards.sql
├── db.changelog-004-create-blocks.sql
└── db.changelog-005-add-indexes.sql
```

## Tratamento de Erros

A aplicação exibe mensagens simples e compreensíveis para o usuário:

- ❌ "Board não encontrado"
- ❌ "Card não encontrado"
- ❌ "Card já está bloqueado"
- ❌ "Card já está finalizado"
- ❌ "O card está cancelado"
- ❌ "Erro ao conectar ao banco"
- ❌ "Erro ao executar migrations"

**Stack traces** são evitados na interface de usuário. Erros graves são registrados em logs.

## Boas Práticas Implementadas

✅ Try-with-resources para gerenciar recursos  
✅ Prepared Statements para evitar SQL injection  
✅ Transações com commit/rollback  
✅ Separação de responsabilidades (UI, Service, DAO, Entity, DTO)  
✅ Exceções customizadas  
✅ Variáveis de ambiente para configuração  
✅ DTOs para não expor entidades  
✅ Validação de entrada  
✅ Índices no banco para performance  

## Como Expandir

### Adicionar Nova Funcionalidade

1. Criar DAO se for persistência
2. Criar Service com lógica
3. Criar DTO para UI
4. Adicionar Exception customizada se necessário
5. Atualizar UI (MainMenu ou BoardMenu)
6. Testar com `./gradlew run`

### Modificar Banco de Dados

1. Criar novo arquivo SQL em `src/main/resources/db/changelog/migrations/`
2. Adicionar include no `db.changelog-master.yml`
3. A migração rodará automaticamente na próxima execução

## Troubleshooting

### Erro: "Não foi possível conectar ao banco de dados"

- Verifique se MySQL está rodando
- Verifique variáveis de ambiente: `DB_URL`, `DB_USER`, `DB_PASSWORD`
- Verifique se o banco `board` foi criado
- Verifique credenciais do usuário MySQL

### Erro: "Erro ao executar migrations"

- Verifique se a pasta `src/main/resources/db/changelog/migrations/` existe
- Verifique se os arquivos SQL estão bem formados
- Verifique permissões do usuário MySQL para criar tabelas

### Build falha com erro de dependências

- Limpe cache: `./gradlew clean`
- Force refresh: `./gradlew build --refresh-dependencies`

## Contribuindo

Este é um projeto educacional da DIO. Contribuições são bem-vindas para:

- Melhorias de performance
- Novos testes
- Documentação
- Correção de bugs

## Licença

Este projeto é de código aberto e está disponível para fins educacionais.

---

**Desenvolvido como desafio da Digital Innovation One (DIO)**

Na raiz do projeto, rode:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
java -cp out br.com.dio.Main
```

No Windows PowerShell, pode usar:

```powershell
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
java -cp out br.com.dio.Main
```

## Como executar com Gradle

Se você tiver Gradle instalado:

```bash
gradle run
```

## Observação

Esta versão foi feita para ser simples de executar, sem depender de banco de dados externo. A camada de persistência foi organizada em pacotes de `dao`, `entity` e `repository`, e os dados ficam salvos em um arquivo local dentro da pasta `.board-data`.
