--liquibase formatted sql
--changeset dio:002 splitStatements:true endDelimiter:;

CREATE TABLE IF NOT EXISTS BOARD_COLUMNS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position INT NOT NULL,
    kind VARCHAR(50) NOT NULL,
    board_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_columns_boards FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE
);

--rollback DROP TABLE BOARD_COLUMNS;
