package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.BoardColumnEntity;
import br.com.cardo.persistence.entity.BoardColumnKindEnum;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class BoardDAO {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var sql = "insert into board (idboard, name) values (?, ?);";
        try (var statement = connection.prepareStatement(sql)) {
            entity.setId(UUID.randomUUID());
            statement.setString(1, entity.getId().toString());
            statement.setString(2, entity.getName());
            statement.executeUpdate();

            return entity;
        }
    }

    public void delete(final String name) throws SQLException {
        var sql = "DELETE FROM board WHERE name = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        }
    }

    public Optional<BoardEntity> findByName(final String name) throws SQLException {
        var sql = """
        SELECT
            b.idboard,
            b.name AS board_name,
            bc.idboard_column,
            bc.name AS board_column_name,
            bc.order,
            bc.kind,
            c.idcard,
            c.title,
            c.description
        FROM board b
        LEFT JOIN board_column bc ON fk_board = idboard
        LEFT JOIN card c ON fk_board_column = idboard_column
        WHERE b.name = ?
        ORDER BY bc.order;
    """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            BoardEntity board = null;
            var columnMap = new java.util.LinkedHashMap<UUID, BoardColumnEntity>();

            while (resultSet.next()) {
                if (board == null) {
                    board = new BoardEntity();
                    board.setId(UUID.fromString(resultSet.getString("idboard")));
                    board.setName(resultSet.getString("board_name"));
                }

                String columnIdStr = resultSet.getString("idboard_column");
                if (columnIdStr != null) {
                    UUID columnId = UUID.fromString(columnIdStr);

                    var column = columnMap.get(columnId);
                    if (column == null) {
                        column = new BoardColumnEntity();
                        column.setId(columnId);
                        column.setName(resultSet.getString("board_column_name"));
                        column.setOrder(resultSet.getInt("order"));
                        column.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));
                        column.setBoard(board);
                        column.setCards(new ArrayList<>()); // Inicializa a lista de cards
                        columnMap.put(columnId, column);
                    }

                    String cardIdStr = resultSet.getString("idcard");
                    if (cardIdStr != null) {
                        var card = new CardEntity();
                        card.setId(UUID.fromString(cardIdStr));
                        card.setTitle(resultSet.getString("title"));
                        card.setDescription(resultSet.getString("description"));
                        card.setBoardColumn(column);
                        column.getCards().add(card);
                    }
                }
            }

            if (board != null) {
                board.setBoardColumns(new ArrayList<>(columnMap.values()));
                return Optional.of(board);
            }

            return Optional.empty();
        }
    }

    public Optional<List<BoardEntity>> findAll() throws SQLException {
        var sql = """
        SELECT
            b.idboard,
            b.name
            FROM board b;
        """;
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            List<BoardEntity> boards = new ArrayList<>();
            while (resultSet.next()) {
                var board = new BoardEntity();
                board.setId(UUID.fromString(resultSet.getString("idboard")));
                board.setName(resultSet.getString("name"));
                boards.add(board);
            }

            if (boards.isEmpty())
                return Optional.empty();

            return Optional.of(boards);
        }
    }


    public boolean exists(final String name) throws SQLException {
        var sql = "SELECT 1 FROM board WHERE name = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }
}
