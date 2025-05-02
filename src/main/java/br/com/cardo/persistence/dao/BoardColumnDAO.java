package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.BoardColumnEntity;
import br.com.cardo.persistence.entity.BoardColumnKindEnum;
import br.com.cardo.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private  final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        String sql;
        sql = "INSERT INTO board_column (idboard_column, name, `order`, kind, fk_board) VALUES (?, ?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            int i = 1;
            entity.setId(UUID.randomUUID());

            statement.setString(i++, entity.getId().toString());
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setString(i, entity.getBoard().getId().toString());
            statement.executeUpdate();

            return entity;
        }
    }

    public void delete(final String name) throws SQLException {
        var sql = "DELETE FROM board_column WHERE name = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        }
    }

    public Optional<BoardColumnEntity> findByName(final String name) throws SQLException {
        var sql = """
            SELECT
                bc.idboard_column,
                bc.name,
                bc.order,
                bc.kind,
                c.idcard,
                c.title,
                c.description
       
            FROM board_column bc
            LEFT JOIN card c ON fk_board_column = idboard_column\s
            WHERE bc.name = ?;
       """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            BoardColumnEntity boardColumn = null;
            List<CardEntity> cards = new ArrayList<>();

            while (resultSet.next()) {
                if (boardColumn == null) {
                    boardColumn = new BoardColumnEntity();
                    boardColumn.setId(UUID.fromString(resultSet.getString("idboard_column")));
                    boardColumn.setName(resultSet.getString("name"));
                    boardColumn.setOrder(resultSet.getInt("`order`"));
                    boardColumn.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));
                }

                String id = resultSet.getString("idcard");
                if (id != null) {
                    var card = new CardEntity();
                    card.setId(UUID.fromString(id));
                    card.setTitle(resultSet.getString("title"));
                    card.setDescription(resultSet.getString("description"));
                    cards.add(card);
                }
            }

            if (boardColumn != null) {
                boardColumn.setCards(cards);
                return Optional.of(boardColumn);
            }

            return Optional.empty();
        }
    }

    public boolean exists(final String name) throws SQLException {
        var sql = "SELECT 1 FROM board_column WHERE name = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }
}
