package br.com.cardo.service;

import br.com.cardo.persistence.dao.BoardColumnDAO;
import br.com.cardo.persistence.dao.BoardDAO;
import br.com.cardo.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findByName(final String name) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findByName(name);

        if(optional.isPresent()) {
            var entity = optional.get();
            entity.setBoardColumns(optional.get().getBoardColumns());
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public void showCardDetails(final BoardEntity board, final String columnName, final String cardTitle) throws SQLException {

        var column = board.getBoardColumns().stream().filter(c -> c.getName().equals(columnName)).findFirst();

        if(column.isEmpty()) {
            System.out.println("Coluna " + columnName + " não encontrada no board " + board.getName());
            return;
        }

        var card = column.get().getCards().stream().filter(c -> c.getTitle().equals(cardTitle)).findFirst();

        if(card.isEmpty()) {
            System.out.println("Card " + cardTitle + " não encontrado na coluna " + columnName);
            return;
        }

        System.out.println("                Card " + cardTitle + "\n");
        System.out.println("Descrição: " + card.get().getDescription() + "\n");

        for (var block : card.get().getBlocks() ) {
            System.out.println("Bloqueado em: " + block.getBlockedAt());
            System.out.println("Bloqueado por: " + block.getBlockReason());
            if(block.getUnblockedAt() != null) {
                System.out.println("Desbloqueado em: " + block.getUnblockedAt());
                System.out.println("Desbloqueado por: " + block.getUnblockReason());
            }
        }

    }
}
