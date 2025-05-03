package br.com.cardo.service;

import br.com.cardo.persistence.dao.BoardColumnDAO;
import br.com.cardo.persistence.dao.BlockDAO;
import br.com.cardo.persistence.dao.CardDAO;
import br.com.cardo.persistence.entity.BlockEntity;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BlockService {

    private final Connection connection;

    public BlockEntity insert(final BlockEntity entity) throws SQLException {
        var dao = new BlockDAO(connection);

        try {
            dao.insert(entity);

            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void update(final BoardEntity board, final CardEntity card, final BlockEntity entity) throws SQLException {
        var dao = new BlockDAO(connection);

        try {
            dao.update(board, card, entity);
            connection.commit();

        } catch (Exception e) {
            connection.rollback();
            throw e;
        }

    }
}
