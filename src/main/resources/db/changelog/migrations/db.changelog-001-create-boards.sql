--liquibase formatted sql
--changeset dio:001 splitStatements:true endDelimiter:;

CREATE TABLE IF NOT EXISTS BOARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE BOARDS;
