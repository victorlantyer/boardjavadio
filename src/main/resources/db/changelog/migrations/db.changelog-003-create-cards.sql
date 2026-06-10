--liquibase formatted sql
--changeset dio:003 splitStatements:true endDelimiter:;

CREATE TABLE IF NOT EXISTS CARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT fk_cards_board_columns FOREIGN KEY (board_column_id) REFERENCES BOARD_COLUMNS(id) ON DELETE CASCADE
);

--rollback DROP TABLE CARDS;
