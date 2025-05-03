package br.com.cardo.service;

import br.com.cardo.persistence.dao.BlockDAO;
import br.com.cardo.persistence.dao.BoardColumnDAO;
import br.com.cardo.persistence.dao.BoardDAO;
import br.com.cardo.persistence.dao.CardDAO;
import br.com.cardo.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findByName(final String name) throws SQLException {
        var daoBoard = new BoardDAO(connection);
        if(daoBoard.findByName(name).isEmpty())
            return Optional.empty();

        BoardEntity entity = daoBoard.findByName(name).get();
        var boardColumnDAO = new BoardColumnDAO(connection);
        var cardDAO = new CardDAO(connection);
        var blockDAO = new BlockDAO(connection);
        var columns = boardColumnDAO.findByBoard(name);

        for (var column : columns) {
            var cards = cardDAO.findCardsByColumnName(name, column.getName());
            for (var card : cards)
                if(blockDAO.findByCardName(card.getTitle()).isPresent())
                    card.getBlocks().add(blockDAO.findByCardName(card.getTitle()).get());

            column.setCards(cards);
        }

        entity.setBoardColumns(columns);

        return Optional.of(entity);
    }

    public Optional<List<BoardEntity>> findAll() throws SQLException {
        var dao = new BoardDAO(connection);
        return dao.findAll();
    }

    public void showCardDetails(final BoardEntity board, final String cardTitle) throws SQLException {
        boolean exists = false;
        for (var column : board.getBoardColumns()) {
            for (var card : column.getCards())
                if(card.getTitle().equals(cardTitle)) {
                    exists = true;
                    System.out.println("                Card " + cardTitle + "\n");
                    System.out.println("Descrição: " + card.getDescription() + "\n");
                    if(card.getBlocks() != null)
                        for(var block : card.getBlocks()) {
                            System.out.println("Bloqueado em: " + block.getBlockedAt());
                            System.out.println("Bloqueado por: " + block.getBlockReason());
                            if(block.getUnblockedAt() != null) {
                                System.out.println("Desbloqueado em: " + block.getUnblockedAt());
                                System.out.println("Desbloqueado por: " + block.getUnblockReason());
                            }
                        }
                }
        }

        if(!exists)
            System.out.println("Card " + cardTitle + " não encontrado no board " + board.getName() + "!\n");
    }
}
