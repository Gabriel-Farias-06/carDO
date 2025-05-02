package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.BlockEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public BlockEntity insert(final BlockEntity entity) throws SQLException {
        var sql = "INSERT INTO block (idblock, title, blocked_at, block_reason, unblocked_at, unblock_reason, fk_card) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            int i = 1;
            entity.setId(UUID.randomUUID());

            statement.setString(i++, entity.getId().toString());
            statement.setString(i++, entity.getTitle());
            statement.setObject(i++, entity.getBlockedAt());
            statement.setString(i++, entity.getBlockReason());
            statement.setObject(i++, entity.getUnblockedAt());
            statement.setString(i++, entity.getUnblockReason());
            statement.setString(i, entity.getCard().getId().toString());
            statement.executeUpdate();

            return entity;
        }
    }

    public void delete(final String title) throws SQLException {
        var sql = "DELETE FROM block WHERE title = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.executeUpdate();
        }
    }

    public Optional<BlockEntity> findByName(final String name) throws SQLException {
        var sql = "SELECT idblock, title, blocked_at, block_reason, unblocked_at, unblock_reason FROM block WHERE title = ?;";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            if(resultSet.next()) {
                BlockEntity block = new BlockEntity();
                block.setId(UUID.fromString(resultSet.getString("idblock")));
                block.setTitle(resultSet.getString("title"));
                block.setBlockedAt(resultSet.getObject("blocked_at", OffsetDateTime.class));
                block.setBlockReason(resultSet.getString("block_reason"));
                block.setUnblockedAt(resultSet.getObject("unblocked_at", OffsetDateTime.class));
                block.setUnblockReason(resultSet.getString("unblock_reason"));

                return Optional.of(block);
            }

            return Optional.empty();
        }
    }

    public boolean exists(final String title) throws SQLException {
        var sql = "SELECT 1 FROM block WHERE title = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }
}
