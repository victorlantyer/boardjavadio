package br.com.dio.service;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.dto.CardSummaryDTO;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;

import java.util.List;

/**
 * Service de consulta de cards.
 */
public class CardQueryService {
    private final CardDAO cardDAO;
    private final BoardColumnDAO boardColumnDAO;
    private final BlockDAO blockDAO;

    public CardQueryService(CardDAO cardDAO, BoardColumnDAO boardColumnDAO, BlockDAO blockDAO) {
        this.cardDAO = cardDAO;
        this.boardColumnDAO = boardColumnDAO;
        this.blockDAO = blockDAO;
    }

    public CardDetailsDTO findById(Long cardId) {
        CardEntity card = loadCard(cardId);
        BoardColumnEntity column = loadColumn(card.getBoardColumnId());
        var activeBlock = blockDAO.findActiveBlock(cardId);
        int totalBlocks = blockDAO.findByCard(cardId).size();

        return new CardDetailsDTO(
                card.getId(),
                card.getTitle(),
                card.getDescription(),
                column.getId(),
                column.getName(),
                activeBlock.isPresent(),
                activeBlock.map(block -> block.getBlockReason()).orElse(null),
                totalBlocks,
                card.getCreatedAt()
        );
    }

    public CardDetailsDTO findById(Long boardId, Long cardId) {
        CardEntity card = loadCard(cardId);
        BoardColumnEntity column = loadColumn(card.getBoardColumnId());
        if (!boardId.equals(column.getBoardId())) {
            throw new EntityNotFoundException("Nao existe um card com o id " + cardId);
        }
        return findById(cardId);
    }

    public List<CardSummaryDTO> findSummariesByColumn(Long boardId, Long columnId) {
        loadColumnInBoard(boardId, columnId);
        return cardDAO.findByColumn(columnId).stream()
                .map(card -> new CardSummaryDTO(
                        card.getId(),
                        card.getTitle(),
                        card.getDescription(),
                        blockDAO.findActiveBlock(card.getId()).isPresent()
                ))
                .toList();
    }

    private CardEntity loadCard(Long cardId) {
        return cardDAO.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Nao existe um card com o id " + cardId));
    }

    private BoardColumnEntity loadColumn(Long columnId) {
        return boardColumnDAO.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Coluna nao encontrada"));
    }

    private BoardColumnEntity loadColumnInBoard(Long boardId, Long columnId) {
        BoardColumnEntity column = loadColumn(columnId);
        if (!boardId.equals(column.getBoardId())) {
            throw new EntityNotFoundException("Coluna nao encontrada");
        }
        return column;
    }
}
