package br.com.dio.persistence.dao;

import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.converter.BoardColumnKindConverter;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operacoes com as colunas do board.
 */
public class BoardColumnDAO {

    public void createDefaultColumns(Long boardId) {
        List<BoardColumnEntity> columns = List.of(
                new BoardColumnEntity(null, boardId, "A Fazer", 0, BoardColumnKindEnum.INITIAL),
                new BoardColumnEntity(null, boardId, "Em Andamento", 1, BoardColumnKindEnum.PENDING),
                new BoardColumnEntity(null, boardId, "Concluido", 2, BoardColumnKindEnum.FINAL),
                new BoardColumnEntity(null, boardId, "Cancelado", 3, BoardColumnKindEnum.CANCEL)
        );

        try (Connection connection = ConnectionConfig.getConnection()) {
            insert(connection, boardId, columns);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar colunas padrao do board: " + e.getMessage(), e);
        }
    }

    public List<BoardColumnEntity> insert(Connection connection, Long boardId, List<BoardColumnEntity> columns) {
        List<BoardColumnEntity> persistedColumns = new ArrayList<>();
        for (BoardColumnEntity column : columns) {
            persistedColumns.add(insert(connection, boardId, column));
        }
        return persistedColumns;
    }

    public BoardColumnEntity insert(Connection connection, Long boardId, BoardColumnEntity column) {
        String sql = "INSERT INTO BOARD_COLUMNS (name, position, kind, board_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, column.getName());
            statement.setInt(2, column.getOrder());
            statement.setString(3, BoardColumnKindConverter.convertToDatabaseColumn(column.getKind()));
            statement.setLong(4, boardId);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new BoardColumnEntity(
                            generatedKeys.getLong(1),
                            boardId,
                            column.getName(),
                            column.getOrder(),
                            column.getKind()
                    );
                }
            }
            throw new RuntimeException("Falha ao criar coluna: nenhum ID foi gerado");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir coluna: " + e.getMessage(), e);
        }
    }

    public List<BoardColumnEntity> findByBoardId(Long boardId) {
        String sql = "SELECT id, name, position, kind, board_id FROM BOARD_COLUMNS WHERE board_id = ? ORDER BY position";
        List<BoardColumnEntity> columns = new ArrayList<>();
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    columns.add(map(resultSet));
                }
            }
            return columns;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar colunas do board: " + e.getMessage(), e);
        }
    }

    public Optional<BoardColumnEntity> findById(Long columnId) {
        String sql = "SELECT id, name, position, kind, board_id FROM BOARD_COLUMNS WHERE id = ?";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, columnId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar coluna: " + e.getMessage(), e);
        }
    }

    public Optional<BoardColumnEntity> findByKind(Long boardId, BoardColumnKindEnum kind) {
        String sql = "SELECT id, name, position, kind, board_id FROM BOARD_COLUMNS WHERE board_id = ? AND kind = ? ORDER BY position";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.setString(2, kind.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar coluna por tipo: " + e.getMessage(), e);
        }
    }

    public Optional<BoardColumnEntity> nextColumn(BoardColumnEntity currentColumn) {
        String sql = "SELECT id, name, position, kind, board_id FROM BOARD_COLUMNS WHERE board_id = ? AND position = ? LIMIT 1";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, currentColumn.getBoardId());
            statement.setInt(2, currentColumn.getOrder() + 1);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar proxima coluna: " + e.getMessage(), e);
        }
    }

    private BoardColumnEntity map(ResultSet resultSet) throws SQLException {
        return new BoardColumnEntity(
                resultSet.getLong("id"),
                resultSet.getLong("board_id"),
                resultSet.getString("name"),
                resultSet.getInt("position"),
                BoardColumnKindConverter.toDatabaseColumn(resultSet.getString("kind"))
        );
    }
}
