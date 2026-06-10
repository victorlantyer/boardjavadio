--liquibase formatted sql
--changeset dio:004 splitStatements:true endDelimiter:;

CREATE TABLE IF NOT EXISTS BLOCKS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason TEXT NOT NULL,
    unblocked_at TIMESTAMP NULL,
    unblock_reason TEXT NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT fk_blocks_cards FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
);

--rollback DROP TABLE BLOCKS;
