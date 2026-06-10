package br.com.dio.service;

import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.exception.InvalidOperationException;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.CardEntity;

/**
 * Service para regras de negocio relacionadas a cards.
 */
public class CardService {
    private final BoardColumnDAO boardColumnDAO;
    private final CardDAO cardDAO;
    private final BlockDAO blockDAO;

    public CardService(BoardColumnDAO boardColumnDAO, CardDAO cardDAO, BlockDAO blockDAO) {
        this.boardColumnDAO = boardColumnDAO;
        this.cardDAO = cardDAO;
        this.blockDAO = blockDAO;
    }

    public CardEntity createCard(Long boardId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O titulo do card nao pode ser vazio");
        }
        BoardColumnEntity initialColumn = boardColumnDAO.findByKind(boardId, BoardColumnKindEnum.INITIAL)
                .orElseThrow(() -> new EntityNotFoundException("Coluna inicial nao encontrada"));
        return cardDAO.insert(
                initialColumn.getId(),
                title.trim(),
                description == null ? "" : description.trim()
        );
    }

    public void moveToNextColumn(Long boardId, Long cardId) {
        CardEntity card = getCardInBoard(boardId, cardId);
        BoardColumnEntity currentColumn = getColumn(card.getBoardColumnId());

        if (blockDAO.findActiveBlock(cardId).isPresent()) {
            throw new CardBlockedException("O card esta bloqueado e nao pode ser movido");
        }
        if (currentColumn.getKind() == BoardColumnKindEnum.FINAL) {
            throw new CardFinishedException("O card ja esta finalizado");
        }
        if (currentColumn.getKind() == BoardColumnKindEnum.CANCEL) {
            throw new InvalidOperationException("O card esta cancelado");
        }

        BoardColumnEntity nextColumn = boardColumnDAO.nextColumn(currentColumn)
                .orElseThrow(() -> new InvalidOperationException("Nao existe proxima coluna"));
        if (nextColumn.getKind() == BoardColumnKindEnum.CANCEL) {
            throw new InvalidOperationException("Use a opcao de cancelar para enviar um card para cancelado");
        }
        cardDAO.move(cardId, nextColumn.getId());
    }

    public void block(Long boardId, Long cardId, String reason) {
        CardEntity card = getCardInBoard(boardId, cardId);
        BoardColumnEntity currentColumn = getColumn(card.getBoardColumnId());

        if (currentColumn.getKind() == BoardColumnKindEnum.FINAL || currentColumn.getKind() == BoardColumnKindEnum.CANCEL) {
            throw new InvalidOperationException("Nao e possivel bloquear card finalizado ou cancelado");
        }
        if (blockDAO.findActiveBlock(cardId).isPresent()) {
            throw new InvalidOperationException("O card ja esta bloqueado");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Informe o motivo do bloqueio");
        }
        blockDAO.block(cardId, reason.trim());
    }

    public void unblock(Long boardId, Long cardId, String reason) {
        getCardInBoard(boardId, cardId);
        var block = blockDAO.findActiveBlock(cardId)
                .orElseThrow(() -> new InvalidOperationException("O card nao esta bloqueado"));
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Informe o motivo do desbloqueio");
        }
        blockDAO.unblock(block.getId(), reason.trim());
    }

    public void cancel(Long boardId, Long cardId) {
        CardEntity card = getCardInBoard(boardId, cardId);
        BoardColumnEntity currentColumn = getColumn(card.getBoardColumnId());

        if (currentColumn.getKind() == BoardColumnKindEnum.FINAL) {
            throw new InvalidOperationException("Um card finalizado nao pode ser cancelado");
        }
        if (currentColumn.getKind() == BoardColumnKindEnum.CANCEL) {
            throw new InvalidOperationException("O card ja esta cancelado");
        }
        BoardColumnEntity cancelColumn = boardColumnDAO.findByKind(boardId, BoardColumnKindEnum.CANCEL)
                .orElseThrow(() -> new EntityNotFoundException("Coluna de cancelamento nao encontrada"));
        cardDAO.move(cardId, cancelColumn.getId());
    }

    private CardEntity getCardInBoard(Long boardId, Long cardId) {
        CardEntity card = cardDAO.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Nao existe um card com o id " + cardId));
        BoardColumnEntity column = getColumn(card.getBoardColumnId());
        if (!boardId.equals(column.getBoardId())) {
            throw new EntityNotFoundException("Nao existe um card com o id " + cardId);
        }
        return card;
    }

    private BoardColumnEntity getColumn(Long columnId) {
        return boardColumnDAO.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Coluna nao encontrada"));
    }
}
