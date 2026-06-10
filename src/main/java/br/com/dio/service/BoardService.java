package br.com.dio.service;

import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.exception.InvalidBoardOperationException;
import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service para regras de negocio relacionadas a board.
 */
public class BoardService {
    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;
    private final CardDAO cardDAO;

    public BoardService(BoardDAO boardDAO, BoardColumnDAO boardColumnDAO, CardDAO cardDAO) {
        this.boardDAO = boardDAO;
        this.boardColumnDAO = boardColumnDAO;
        this.cardDAO = cardDAO;
    }

    public BoardEntity createBoard(String name) {
        BoardEntity board = new BoardEntity(null, name);
        board.addColumn(new BoardColumnEntity(null, null, "A Fazer", 0, BoardColumnKindEnum.INITIAL));
        board.addColumn(new BoardColumnEntity(null, null, "Em Andamento", 1, BoardColumnKindEnum.PENDING));
        board.addColumn(new BoardColumnEntity(null, null, "Concluido", 2, BoardColumnKindEnum.FINAL));
        board.addColumn(new BoardColumnEntity(null, null, "Cancelado", 3, BoardColumnKindEnum.CANCEL));
        return createBoard(board);
    }

    public BoardEntity createBoard(BoardEntity board) {
        validateBoard(board);

        Connection connection = null;
        try {
            connection = ConnectionConfig.getConnection();
            BoardEntity persistedBoard = boardDAO.insert(connection, board.getName().trim());
            List<BoardColumnEntity> persistedColumns = boardColumnDAO.insert(
                    connection,
                    persistedBoard.getId(),
                    board.getColumns()
            );
            connection.commit();
            persistedColumns.forEach(persistedBoard::addColumn);
            return persistedBoard;
        } catch (RuntimeException | SQLException e) {
            rollback(connection);
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new InvalidBoardOperationException("Erro ao criar board: " + e.getMessage(), e);
        } finally {
            ConnectionConfig.closeConnection(connection);
        }
    }

    public List<BoardEntity> listBoards() {
        return boardDAO.findAll();
    }

    public BoardEntity findBoard(Long boardId) {
        return boardDAO.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board nao encontrado"));
    }

    public void deleteBoard(Long boardId) {
        if (!boardDAO.delete(boardId)) {
            throw new EntityNotFoundException("Board nao encontrado");
        }
    }

    private void validateBoard(BoardEntity board) {
        if (board == null) {
            throw new InvalidBoardOperationException("Board invalido");
        }
        if (board.getName() == null || board.getName().isBlank()) {
            throw new InvalidBoardOperationException("O nome do board nao pode ser vazio");
        }

        List<BoardColumnEntity> columns = board.getColumns();
        if (columns.size() < 4) {
            throw new InvalidBoardOperationException("O board precisa ter ao menos quatro colunas");
        }

        long initialColumns = columns.stream().filter(column -> column.getKind() == BoardColumnKindEnum.INITIAL).count();
        long pendingColumns = columns.stream().filter(column -> column.getKind() == BoardColumnKindEnum.PENDING).count();
        long finalColumns = columns.stream().filter(column -> column.getKind() == BoardColumnKindEnum.FINAL).count();
        long cancelColumns = columns.stream().filter(column -> column.getKind() == BoardColumnKindEnum.CANCEL).count();

        if (initialColumns != 1) {
            throw new InvalidBoardOperationException("O board precisa ter exatamente uma coluna inicial");
        }
        if (pendingColumns < 1) {
            throw new InvalidBoardOperationException("O board precisa ter ao menos uma coluna pendente");
        }
        if (finalColumns != 1) {
            throw new InvalidBoardOperationException("O board precisa ter exatamente uma coluna final");
        }
        if (cancelColumns != 1) {
            throw new InvalidBoardOperationException("O board precisa ter exatamente uma coluna de cancelamento");
        }

        for (int index = 0; index < columns.size(); index++) {
            BoardColumnEntity column = columns.get(index);
            if (column.getName() == null || column.getName().isBlank()) {
                throw new InvalidBoardOperationException("Toda coluna precisa ter nome");
            }
            if (column.getOrder() != index) {
                throw new InvalidBoardOperationException("A ordem das colunas deve ser sequencial a partir de zero");
            }
        }

        if (columns.get(0).getKind() != BoardColumnKindEnum.INITIAL) {
            throw new InvalidBoardOperationException("A primeira coluna deve ser inicial");
        }
        if (columns.get(columns.size() - 2).getKind() != BoardColumnKindEnum.FINAL) {
            throw new InvalidBoardOperationException("A penultima coluna deve ser final");
        }
        if (columns.get(columns.size() - 1).getKind() != BoardColumnKindEnum.CANCEL) {
            throw new InvalidBoardOperationException("A ultima coluna deve ser de cancelamento");
        }
        for (int index = 1; index < columns.size() - 2; index++) {
            if (columns.get(index).getKind() != BoardColumnKindEnum.PENDING) {
                throw new InvalidBoardOperationException("As colunas intermediarias devem ser pendentes");
            }
        }
    }

    private void rollback(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }
}
