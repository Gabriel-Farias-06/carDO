package br.com.cardo.service;

import br.com.cardo.persistence.dao.BlockDAO;
import br.com.cardo.persistence.dao.CardDAO;
import br.com.cardo.persistence.entity.BlockEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BlockQueryService {

    private final Connection connection;

    public Optional<BlockEntity> findByCardName(final String boardName, final String name) throws SQLException {
        var daoCard = new CardDAO(connection);
        var daoBlock = new BlockDAO(connection);

        var optionalCard = daoCard.findByName(boardName, name);
        if (optionalCard.isEmpty())
            return Optional.empty();

        var card = optionalCard.get();
        return daoBlock.findByCardName(card.getTitle());
    }

}
