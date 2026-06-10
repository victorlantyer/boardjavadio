--liquibase formatted sql
--changeset dio:005 splitStatements:true endDelimiter:;

CREATE INDEX idx_board_columns_board_id ON BOARD_COLUMNS(board_id);
CREATE INDEX idx_cards_board_column_id ON CARDS(board_column_id);
CREATE INDEX idx_blocks_card_id ON BLOCKS(card_id);

--rollback DROP INDEX idx_board_columns_board_id ON BOARD_COLUMNS;
--rollback DROP INDEX idx_cards_board_column_id ON CARDS;
--rollback DROP INDEX idx_blocks_card_id ON BLOCKS;
