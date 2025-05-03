package br.com.cardo.service;

import br.com.cardo.persistence.dao.BoardDAO;
import br.com.cardo.persistence.dao.CardDAO;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {

    private Connection connection;

    public Optional<CardEntity> findByName(final BoardEntity board, final String cardTitle) throws SQLException {
        var daoCard = new CardDAO(connection);

        if(daoCard.findByName(board.getName(), cardTitle).isEmpty())
            return Optional.empty();

        return Optional.of(daoCard.findByName(board.getName(), cardTitle).get());
    }
}
