package br.com.cardo.service;

import br.com.cardo.persistence.dao.BoardColumnDAO;
import br.com.cardo.persistence.dao.BoardDAO;
import br.com.cardo.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        try {
            dao.insert(entity);
            var columns = entity.getBoardColumns().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();

            for (var column : columns) {
                boardColumnDAO.insert(column);
            }

            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public boolean delete(final String name) throws SQLException {
        var dao = new BoardDAO(connection);
        try {
            if(!dao.exists(name))
                return false;

            dao.delete(name);
            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}
