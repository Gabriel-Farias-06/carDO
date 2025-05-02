package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.BlockEntity;
import br.com.cardo.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CardDAO {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO card (idcard, title, description, fk_board_column) VALUES (?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            int i = 1;
            entity.setId(UUID.randomUUID());

            statement.setString(i++, entity.getId().toString());
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setString(i, entity.getBoardColumn().getId().toString());
            statement.executeUpdate();

            return entity;
        }
    }

    public void delete(final String title) throws SQLException {
        var sql = "DELETE FROM card WHERE title = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.executeUpdate();
        }
    }



    public Optional<CardEntity> findByName(final String name) throws SQLException {
        var sql = "SELECT idcard, c.title as card_title, description, idblock, bl.title as block_title, blocked_at, block_reason, unblocked_at, unblock_reason FROM card c left join block bl on fk_card = idcard  WHERE c.title = ?;";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            CardEntity card = null;
            List<BlockEntity> blocks = new ArrayList<>();

            while (resultSet.next()) {
                if (card == null) {
                    card = new CardEntity();
                    card.setId(UUID.fromString(resultSet.getString("idcard")));
                    card.setTitle(resultSet.getString("card_title"));
                    card.setDescription(resultSet.getString("description"));
                }

                String id = resultSet.getString("idblock");
                if (id != null) {
                    var block = new BlockEntity();
                    block.setId(UUID.fromString(id));
                    block.setTitle(resultSet.getString("block_title"));
                    block.setBlockedAt(resultSet.getObject("blocked_at", OffsetDateTime.class));
                    block.setBlockReason(resultSet.getString("block_reason"));
                    block.setUnblockedAt(resultSet.getObject("unblocked_at", OffsetDateTime.class));
                    block.setUnblockReason(resultSet.getString("unblock_reason"));
                    blocks.add(block);
                }
            }

            if (card != null) {
                card.setBlocks(blocks);
                return Optional.of(card);
            }

            return Optional.empty();
        }
    }

    public boolean exists(final String title) throws SQLException {
        var sql = "SELECT 1 FROM card WHERE title = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }
}
