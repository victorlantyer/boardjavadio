package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;

import java.util.List;

/**
 * Service de consulta de colunas de board.
 */
public class BoardColumnQueryService {
    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;
    private final CardDAO cardDAO;

    public BoardColumnQueryService(BoardDAO boardDAO, BoardColumnDAO boardColumnDAO, CardDAO cardDAO) {
        this.boardDAO = boardDAO;
        this.boardColumnDAO = boardColumnDAO;
        this.cardDAO = cardDAO;
    }

    public List<BoardColumnInfoDTO> findByBoardId(Long boardId) {
        ensureBoardExists(boardId);
        return boardColumnDAO.findByBoardId(boardId).stream()
                .map(this::toInfo)
                .toList();
    }

    public BoardColumnInfoDTO findById(Long boardId, Long columnId) {
        ensureBoardExists(boardId);
        BoardColumnEntity column = boardColumnDAO.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Coluna nao encontrada"));

        if (!boardId.equals(column.getBoardId())) {
            throw new EntityNotFoundException("Coluna nao encontrada");
        }

        return toInfo(column);
    }

    private BoardColumnInfoDTO toInfo(BoardColumnEntity column) {
        return new BoardColumnInfoDTO(
                column.getId(),
                column.getName(),
                column.getOrder(),
                column.getKind(),
                cardDAO.findByColumn(column.getId()).size()
        );
    }

    private void ensureBoardExists(Long boardId) {
        boardDAO.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board nao encontrado"));
    }
}
