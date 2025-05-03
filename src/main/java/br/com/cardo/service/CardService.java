package br.com.cardo.service;

import br.com.cardo.persistence.dao.CardDAO;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var dao = new CardDAO(connection);

        try {
            dao.insert(entity);

            connection.commit();
            return entity;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }

    public void update(final BoardEntity board, CardEntity card, final int order) throws SQLException {
        var dao = new CardDAO(connection);

        try {
            dao.update(board, card, order);

            connection.commit();

        } catch (Exception e) {
            connection.rollback();
            throw e;
        }

    }

    public boolean delete(final String title) throws SQLException {
        var dao = new CardDAO(connection);

        try {
            if(!dao.exists(title))
                return false;
            dao.delete(title);
            connection.commit();
            return true;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }
}
