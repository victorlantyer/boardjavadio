-- Script de referência para a estrutura do board.
-- Nesta implementação didática, a aplicação roda com persistência em arquivo.
-- O arquivo fica aqui para manter a ideia de migrations vista na aula.

CREATE TABLE BOARDS (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE BOARD_COLUMNS (
    id BIGINT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    column_order INT NOT NULL,
    kind VARCHAR(50) NOT NULL
);

CREATE TABLE CARDS (
    id BIGINT PRIMARY KEY,
    board_column_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE BLOCKS (
    id BIGINT PRIMARY KEY,
    card_id BIGINT NOT NULL,
    block_reason TEXT NOT NULL,
    blocked_at TIMESTAMP NOT NULL,
    unblock_reason TEXT,
    unblocked_at TIMESTAMP,
    active BOOLEAN NOT NULL
);
