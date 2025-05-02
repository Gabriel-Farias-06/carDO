package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.BoardColumnEntity;
import br.com.cardo.persistence.entity.BoardColumnKindEnum;
import br.com.cardo.persistence.entity.BoardEntity;
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
                            bc.kind
                        FROM board b
                        LEFT JOIN board_column bc ON fk_board = idboard
                        WHERE b.name = ?;
                """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            BoardEntity board = null;
            List<BoardColumnEntity> columns = new ArrayList<>();

            while (resultSet.next()) {
                if (board == null) {
                    board = new BoardEntity();
                    board.setId(UUID.fromString(resultSet.getString("idboard")));
                    board.setName(resultSet.getString("board_name"));
                }

                String id = resultSet.getString("idboard_column");
                if (id != null) {
                    var column = new BoardColumnEntity();
                    column.setId(UUID.fromString(id));
                    column.setName(resultSet.getString("board_column_name"));
                    column.setOrder(resultSet.getInt("order"));
                    column.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));
                    column.setBoard(board);
                    columns.add(column);
                }
            }

            if (board != null) {
                board.setBoardColumns(columns);
                return Optional.of(board);
            }

            return Optional.empty();
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
