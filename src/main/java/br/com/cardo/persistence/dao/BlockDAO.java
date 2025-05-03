package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.BlockEntity;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.persistence.entity.CardEntity;
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
        var sql = "INSERT INTO block (idblock, title, blocked_at, block_reason, fk_card) VALUES (?, ?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            int i = 1;
            entity.setId(UUID.randomUUID());

            statement.setString(i++, entity.getId().toString());
            statement.setString(i++, entity.getTitle());
            statement.setObject(i++, entity.getBlockedAt());
            statement.setString(i++, entity.getBlockReason());
            statement.setString(i, entity.getCard().getId().toString());
            statement.executeUpdate();

            return entity;
        }
    }

    public void update(final BoardEntity board, final CardEntity card, final BlockEntity entity) throws SQLException {
        var sql = "UPDATE block bl JOIN card c ON bl.fk_card = c.idcard join board_column bc on c.fk_board_column = bc.idboard_column join board b on bc.fk_board = b.idboard SET bl.unblocked_at = ?, bl.blocked_at = ? WHERE bl.title = ? AND c.title = ? AND b.name = ? AND bl.unblocked_at IS NULL; ;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setObject(1, entity.getUnblockedAt());
            statement.setObject(2, entity.getBlockedAt());
            statement.setString(3, entity.getTitle());
            statement.setString(4, card.getTitle());
            statement.setString(5, board.getName());
            statement.executeUpdate();

        }
    }

    public void delete(final String title) throws SQLException {
        var sql = "DELETE FROM block WHERE title = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.executeUpdate();
        }
    }

    public Optional<BlockEntity> findByCardName(final String cardName) throws SQLException {
        var sql = "SELECT idblock, bl.title as block_title, blocked_at, block_reason, unblocked_at, unblock_reason FROM block bl join card c on fk_card = idcard WHERE c.title = ? and unblocked_at is null;";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, cardName);
            var resultSet = statement.executeQuery();

            if(resultSet.next()) {
                BlockEntity block = new BlockEntity();
                block.setId(UUID.fromString(resultSet.getString("idblock")));
                block.setTitle(resultSet.getString("block_title"));
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
