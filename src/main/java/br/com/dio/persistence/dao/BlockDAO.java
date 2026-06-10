package br.com.dio.persistence.dao;

import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.entity.BlockEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operacoes com bloqueios.
 */
public class BlockDAO {

    public BlockEntity block(Long cardId, String reason) {
        String sql = "INSERT INTO BLOCKS (block_reason, card_id) VALUES (?, ?)";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, reason);
            statement.setLong(2, cardId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Falha ao criar bloqueio: nenhuma linha foi inserida");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    connection.commit();
                    return new BlockEntity(generatedKeys.getLong(1), cardId, reason);
                }
            }
            throw new RuntimeException("Falha ao criar bloqueio: nenhum ID foi gerado");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao bloquear card: " + e.getMessage(), e);
        }
    }

    public Optional<BlockEntity> findActiveBlock(Long cardId) {
        String sql = "SELECT id, card_id, blocked_at, block_reason, unblocked_at, unblock_reason "
                + "FROM BLOCKS WHERE card_id = ? AND unblocked_at IS NULL ORDER BY blocked_at DESC LIMIT 1";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar bloqueio ativo: " + e.getMessage(), e);
        }
    }

    public void unblock(Long blockId, String reason) {
        String sql = "UPDATE BLOCKS SET unblocked_at = CURRENT_TIMESTAMP, unblock_reason = ? WHERE id = ?";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reason);
            statement.setLong(2, blockId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Bloqueio nao encontrado para atualizacao");
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desbloquear card: " + e.getMessage(), e);
        }
    }

    public List<BlockEntity> findByCard(Long cardId) {
        String sql = "SELECT id, card_id, blocked_at, block_reason, unblocked_at, unblock_reason "
                + "FROM BLOCKS WHERE card_id = ? ORDER BY blocked_at DESC";
        List<BlockEntity> blocks = new ArrayList<>();
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    blocks.add(map(resultSet));
                }
            }
            return blocks;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar bloqueios do card: " + e.getMessage(), e);
        }
    }

    public int countByCard(Long cardId) {
        String sql = "SELECT COUNT(*) FROM BLOCKS WHERE card_id = ?";
        try (Connection connection = ConnectionConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar bloqueios do card: " + e.getMessage(), e);
        }
    }

    private BlockEntity map(ResultSet resultSet) throws SQLException {
        Timestamp blockedAt = resultSet.getTimestamp("blocked_at");
        Timestamp unblockedAt = resultSet.getTimestamp("unblocked_at");
        return new BlockEntity(
                resultSet.getLong("id"),
                resultSet.getLong("card_id"),
                resultSet.getString("block_reason"),
                blockedAt == null ? null : blockedAt.toLocalDateTime(),
                resultSet.getString("unblock_reason"),
                unblockedAt == null ? null : unblockedAt.toLocalDateTime()
        );
    }
}
