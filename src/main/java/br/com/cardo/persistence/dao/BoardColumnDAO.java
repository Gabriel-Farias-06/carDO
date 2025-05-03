package br.com.cardo.persistence.dao;

import br.com.cardo.persistence.entity.BoardColumnEntity;
import br.com.cardo.persistence.entity.BoardColumnKindEnum;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public List<BoardColumnEntity> findByBoard(final String name) throws SQLException {
        var sql = """
            SELECT
                bc.idboard_column,
                bc.name,
                bc.order,
                bc.kind
            FROM board_column bc join board b on idboard = fk_board where b.name
             = ?;
       """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            List<BoardColumnEntity> columns = new ArrayList<>();

            while (resultSet.next()) {
                var boardColumn = new BoardColumnEntity();
                boardColumn.setId(UUID.fromString(resultSet.getString("idboard_column")));
                boardColumn.setName(resultSet.getString("name"));
                boardColumn.setOrder(resultSet.getInt("bc.order"));
                boardColumn.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));

                columns.add(boardColumn);
            }

            return columns;
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
