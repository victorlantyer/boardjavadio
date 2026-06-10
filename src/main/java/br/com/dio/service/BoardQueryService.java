package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.dto.BoardDetailsDTO;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.dao.CardDAO;

import java.util.List;

/**
 * Service de consulta de boards, sem alteracao de dados.
 */
public class BoardQueryService {
    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;
    private final CardDAO cardDAO;

    public BoardQueryService(BoardDAO boardDAO, BoardColumnDAO boardColumnDAO, CardDAO cardDAO) {
        this.boardDAO = boardDAO;
        this.boardColumnDAO = boardColumnDAO;
        this.cardDAO = cardDAO;
    }

    public BoardDetailsDTO findById(Long boardId) {
        var board = boardDAO.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board nao encontrado"));

        List<BoardColumnInfoDTO> columns = boardColumnDAO.findByBoardId(boardId).stream()
                .map(column -> new BoardColumnInfoDTO(
                        column.getId(),
                        column.getName(),
                        column.getOrder(),
                        column.getKind(),
                        cardDAO.findByColumn(column.getId()).size()
                ))
                .toList();

        return new BoardDetailsDTO(board.getId(), board.getName(), columns);
    }
}
