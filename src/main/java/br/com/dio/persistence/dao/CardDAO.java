package br.com.dio.persistence.dao;

import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operacoes com a entidade Card usando JDBC puro.
 */
public class CardDAO {

    public CardEntity insert(Long boardColumnId, String title, String description) {
        String sql = "INSERT INTO CARDS (title, description, board_column_id) VALUES (?, ?, ?)";

        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, title);
            statement.setString(2, description);
            statement.setLong(3, boardColumnId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Falha ao criar card: nenhuma linha foi inserida");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    connection.commit();
                    CardEntity card = new CardEntity(id, boardColumnId, title, description);
                    card.setCreatedAt(LocalDateTime.now());
                    return card;
                }
            }

            throw new RuntimeException("Falha ao criar card: nenhum ID foi gerado");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir card: " + e.getMessage(), e);
        }
    }

    public Optional<CardEntity> findById(Long cardId) {
        String sql = "SELECT id, title, description, created_at, board_column_id FROM CARDS WHERE id = ?";

        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, cardId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    CardEntity card = new CardEntity(
                            resultSet.getLong("id"),
                            resultSet.getLong("board_column_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description")
                    );
                    card.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    return Optional.of(card);
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar card: " + e.getMessage(), e);
        }
    }

    public List<CardEntity> findByBoard(Long boardId) {
        String sql = "SELECT c.id, c.title, c.description, c.created_at, c.board_column_id "
                + "FROM CARDS c "
                + "JOIN BOARD_COLUMNS bc ON c.board_column_id = bc.id "
                + "WHERE bc.board_id = ? ORDER BY c.created_at";
        List<CardEntity> cards = new ArrayList<>();

        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CardEntity card = new CardEntity(
                            resultSet.getLong("id"),
                            resultSet.getLong("board_column_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description")
                    );
                    card.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    cards.add(card);
                }
            }

            return cards;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cards do board: " + e.getMessage(), e);
        }
    }

    public List<CardEntity> findByColumn(Long columnId) {
        String sql = "SELECT id, title, description, created_at, board_column_id "
                + "FROM CARDS WHERE board_column_id = ? ORDER BY created_at";
        List<CardEntity> cards = new ArrayList<>();

        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, columnId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CardEntity card = new CardEntity(
                            resultSet.getLong("id"),
                            resultSet.getLong("board_column_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description")
                    );
                    card.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    cards.add(card);
                }
            }

            return cards;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cards da coluna: " + e.getMessage(), e);
        }
    }

    public void move(Long cardId, Long newColumnId) {
        String sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?";

        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, newColumnId);
            statement.setLong(2, cardId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Card nao encontrado para atualizacao");
            }

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao mover card: " + e.getMessage(), e);
        }
    }
}
