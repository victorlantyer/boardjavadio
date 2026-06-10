package br.com.dio.persistence.dao;

import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operacoes com board usando JDBC puro.
 */
public class BoardDAO {

    public BoardEntity insert(String name) {
        try (Connection connection = ConnectionConfig.getConnection()) {
            BoardEntity board = insert(connection, name);
            connection.commit();
            return board;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir board: " + e.getMessage(), e);
        }
    }

    public BoardEntity insert(Connection connection, String name) {
        String sql = "INSERT INTO BOARDS (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new BoardEntity(generatedKeys.getLong(1), name);
                }
            }
            throw new RuntimeException("Falha ao criar board: nenhum ID foi gerado");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir board: " + e.getMessage(), e);
        }
    }

    public Optional<BoardEntity> findById(Long id) {
        String sql = "SELECT id, name FROM BOARDS WHERE id = ?";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new BoardEntity(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
                    ));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar board: " + e.getMessage(), e);
        }
    }

    public List<BoardEntity> findAll() {
        String sql = "SELECT id, name FROM BOARDS ORDER BY id";
        List<BoardEntity> boards = new ArrayList<>();
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                boards.add(new BoardEntity(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
            }
            return boards;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar boards: " + e.getMessage(), e);
        }
    }

    public boolean exists(Long id) {
        String sql = "SELECT COUNT(*) FROM BOARDS WHERE id = ?";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existencia do board: " + e.getMessage(), e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM BOARDS WHERE id = ?";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            connection.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar board: " + e.getMessage(), e);
        }
    }
}
