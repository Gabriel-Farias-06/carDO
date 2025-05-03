package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.*;
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

    public CardEntity update(final BoardEntity board, final CardEntity entity, int order) throws SQLException {
        var sql = "UPDATE card SET fk_board_column = ? WHERE idcard = ?;";
        try(var statement = connection.prepareStatement(sql)) {
            var sql2 = "SELECT idboard_column from board_column bc join board b on fk_board = idboard where b.name = ? and bc.order = ?;";
            try(var statement2 = connection.prepareStatement(sql2)) {
                statement2.setString(1, board.getName());
                statement2.setInt(2, order);
                var resultSet = statement2.executeQuery();

                resultSet.next();

                String id = resultSet.getString("idboard_column");
                statement.setString(1, id);
                statement.setString(2, entity.getId().toString());
                statement.executeUpdate();
                return entity;
            }
        }
    }

    public void delete(final String title) throws SQLException {
        var sql = "DELETE FROM card WHERE title = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.executeUpdate();
        }
    }

    public Optional<CardEntity> findByName(final String boardName, final String cardName) throws SQLException {
        var sql = "SELECT idcard, c.title as card_title, description, idboard_column, bc.name as board_name, bc.order, bc.kind FROM card c join board_column bc on fk_board_column = idboard_column join board b on fk_board = idboard WHERE c.title = ? and b.name = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, cardName);
            statement.setString(2, boardName);
            var resultSet = statement.executeQuery();
            if(!resultSet.next())
                return Optional.empty();

            CardEntity card = new CardEntity();
            card.setId(UUID.fromString(resultSet.getString("idcard")));
            card.setTitle(resultSet.getString("card_title"));
            card.setDescription(resultSet.getString("description"));

            var column = new BoardColumnEntity();
            column.setId(UUID.fromString(resultSet.getString("idboard_column")));
            column.setName(resultSet.getString("board_name"));
            column.setOrder(resultSet.getInt("order"));
            column.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));

            card.setBoardColumn(column);

            return Optional.of(card);

        }
    }


    public List<CardEntity> findCardsByColumnName(final String boardName, final String columnName) throws SQLException {
        var sql = "SELECT idcard, c.title as card_title, description FROM card c join board_column bc on fk_board_column = idboard_column join board b on fk_board = idboard WHERE bc.name = ? and b.name = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, columnName);
            statement.setString(2, boardName);
            var resultSet = statement.executeQuery();

            List<CardEntity> cards = new ArrayList<>();

            while (resultSet.next()) {
                var card = new CardEntity();
                card.setId(UUID.fromString(resultSet.getString("idcard")));
                card.setTitle(resultSet.getString("card_title"));
                card.setDescription(resultSet.getString("description"));
                cards.add(card);
            }

            return cards;

        }
    }

    public boolean exists(final String title) throws SQLException {
        var sql = "SELECT 1 FROM card WHERE title = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setString(i++, title);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }

    public boolean existsInTheBoard(final String title, final String name) throws SQLException {
        var sql = "SELECT 1 FROM card c join board_column on fk_board_column = idboard_column join board b on fk_board = idboard join block bl on idcard = fk_card WHERE c.title = ? and b.name = ? and unblocked_at is null;";
        try (var statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setString(i++, title);
            statement.setString(i, name);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }
}
