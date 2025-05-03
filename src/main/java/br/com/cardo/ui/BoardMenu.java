package br.com.cardo.ui;

import br.com.cardo.persistence.config.ConnectionConfig;
import br.com.cardo.persistence.converter.OffSetDateTimeConverter;
import br.com.cardo.persistence.dao.CardDAO;
import br.com.cardo.persistence.entity.BlockEntity;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.persistence.entity.CardEntity;
import br.com.cardo.service.*;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);
    private BoardEntity entity;

    public void showBoard() {
        System.out.println("                      Board " + entity.getName() + "\n");
        System.out.println("                              Colunas\n");

        var columns = entity.getBoardColumns();

        for (var column : columns)
            System.out.printf("%-15s", column.getName());
        System.out.println();

        for (int i = 0; i < columns.size(); i++)
            System.out.print("---------------");

        System.out.println();

        int maxCards = columns.stream()
                .mapToInt(c -> c.getCards().size())
                .max()
                .orElse(0);

        for (int i = 0; i < maxCards; i++) {
            for (var column : columns) {
                var cards = column.getCards();
                if (i < cards.size())
                    System.out.printf("%-15s", cards.get(i).getTitle());
                else
                    System.out.printf("%-15s", "-");
            }
            System.out.println();

        }

        System.out.println("\n");
    }


    public void execute() throws SQLException {
        int option = 0;
        while (option != 7) {
            showBoard();

            System.out.print("\n1 - Criar card\n2 - Mover card\n3 - Remover card\n4 - Bloquear um card\n5 - Desbloquear um card\n6 - Visualizar detalhes do card\n7 - Voltar\n8 - Sair\nDigite a operação desejada: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> createCard();
                case 2 -> moveCard();
                case 3 -> removeCard();
                case 4 -> blockCard();
                case 5 -> unblockCard();
                case 6 -> checkCardInfo();
                case 7 -> System.out.println("Voltando ao menu principal...");
                case 8 -> System.exit(0);
                default -> System.out.println("Digite uma opção válida");
            }

            try (var connection = ConnectionConfig.getConnection()) {
                this.entity = new BoardQueryService(connection).findByName(entity.getName()).orElseThrow();
            }

            System.out.print("Digite ENTER para continuar");
            scanner.nextLine();

            MainMenu.clearConsole();
        }
    }

    private void createCard() throws SQLException {
        var card = new CardEntity();

        try(var connection = ConnectionConfig.getConnection()) {
            var dao = new CardDAO(connection);
            var service = new CardService(connection);

            System.out.print("Informe o nome do novo card: ");
            String name = scanner.nextLine();

            if(dao.existsInTheBoard(name, entity.getName())) {
                System.out.println("Os nomes de um card de um mesmo board precisam ser diferentes!");
                return;
            }

            card.setTitle(name);

            System.out.print("Informe a descrição do novo card: ");
            card.setDescription(scanner.nextLine());

            card.setBoardColumn(entity.getBoardColumns().get(0));

            service.insert(card);

            System.out.println("Card " + card.getTitle() + " criado com sucesso!");
        }
    }

    private void moveCard() {
        showBoard();

        try (var connection = ConnectionConfig.getConnection()) {
            System.out.print("\n\nDigite o nome do card que deseja mover: ");
            String cardName = scanner.nextLine();

            var optional = new CardQueryService(connection).findByName(entity, cardName);

            if (optional.isEmpty()) {
                System.out.println("Card " + cardName + " não foi encontrado no board " + entity.getName() + "!");
                return;
            }

            var optionalBlock = new BlockQueryService(connection).findByCardName(entity.getName(), cardName);

            if(optionalBlock.isPresent() && optionalBlock.get().getBlockedAt() != null && optionalBlock.get().getUnblockedAt() == null) {
                System.out.println("Card " + cardName + " está bloqueado!");
                return;
            }

            var card = optional.get();
            var currentOrder = card.getBoardColumn().getOrder();
            int opcao = 0;

            switch (currentOrder) {
                case 0 -> {
                    System.out.print("1 - Mudar para a coluna Fazendo\n2 - Mudar para a coluna Cancelada\n3 - Voltar para o menu anterior\n\nDigite a sua opção: ");
                    opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1 -> moveCardToColumn(connection, card, currentOrder, 1);
                        case 2 -> moveCardToColumn(connection, card, currentOrder, 3);
                        case 3 -> System.out.println("Voltando para a tela anterior...");
                        default -> System.out.println("Opção inválida.");
                    }
                }
                case 1 -> {
                    System.out.print("1 - Mudar para a coluna Finalizada\n2 - Mudar para a coluna Cancelada\n3 - Voltar para o menu anterior\n\nDigite a sua opção: ");
                    opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1 -> moveCardToColumn(connection, card, currentOrder, 2);
                        case 2 -> moveCardToColumn(connection, card, currentOrder, 3);
                        case 3 -> System.out.println("Voltando para a tela anterior...");
                        default -> System.out.println("Opção inválida.");
                    }
                }

                case 2 -> System.out.println("Esse card foi concluído, não há necessidade de mover para outra coluna.");
                case 3 -> System.out.println("Esse card foi cancelado, não há necessidade de mover para outra coluna.");
                default -> System.out.println("Coluna atual desconhecida.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeCard() {
        showBoard();

        System.out.print("Digite o nome do card que deseja remover: ");
        String cardName = scanner.nextLine();
        try (var connection = ConnectionConfig.getConnection()) {

            var optionalBlock = new BlockQueryService(connection).findByCardName(entity.getName(), cardName);

            if(optionalBlock.isPresent() && optionalBlock.get().getBlockedAt() != null && optionalBlock.get().getUnblockedAt() == null) {
                System.out.println("Card " + cardName + " está bloqueado!");
                return;
            }

            if(new CardService(connection).delete(cardName))
                System.out.println("Card " + cardName + " deletado com sucesso!");
            else
                System.out.println("Card " + cardName + " não foi encontrado no board " + entity.getName() + "!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void blockCard() {
        showBoard();

        System.out.print("Digite o nome do card que deseja bloquear: ");
        String cardName = scanner.nextLine();

        try (var connection = ConnectionConfig.getConnection()) {
            var optional = new CardQueryService(connection).findByName(entity, cardName);

            if(optional.isEmpty()) {
                System.out.println("Card" + cardName + " não foi encontrado no board " + entity.getName() + "!");
                return;
            }

            var block = new BlockEntity();

            System.out.print("Digite o título do bloqueio: ");
            String title = scanner.nextLine();


            System.out.print("Digite o motivo do bloqueio: ");
            String reason = scanner.nextLine();

            block.setTitle(title);
            block.setBlockReason(reason);
            block.setBlockedAt(OffSetDateTimeConverter.toOffsetDateTime(Timestamp.valueOf(LocalDateTime.now())));
            block.setCard(optional.get());

            new BlockService(connection).insert(block);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void unblockCard() {
        showBoard();

        System.out.print("Digite o nome do card que deseja desbloquear: ");
        String cardName = scanner.nextLine();

        try (var connection = ConnectionConfig.getConnection()) {
            var optional = new CardQueryService(connection).findByName(entity, cardName);
            if(optional.isEmpty()) {
                System.out.println("Card " + cardName + " não foi encontrado no board " + entity.getName() + "!");
                return;
            }

            var optionalBlock = new BlockQueryService(connection).findByCardName(entity.getName(), cardName);

            if(optionalBlock.isEmpty()) {
                System.out.println("Card " + cardName + " não está bloquado!");
                return;
            }

            System.out.print("Digite o motivo do desbloqueio: ");
            String reason = scanner.nextLine();

            optionalBlock.get().setUnblockReason(reason);
            optionalBlock.get().setUnblockedAt(OffSetDateTimeConverter.toOffsetDateTime(Timestamp.valueOf(LocalDateTime.now())));

            new BlockService(connection).update(entity, optional.get(), optionalBlock.get());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkCardInfo() {
        showBoard();

        System.out.print("Digite o nome do card que deseja visualizar: ");
        String cardName = scanner.nextLine();
        try (var connection = ConnectionConfig.getConnection()) {
            new BoardQueryService(connection).showCardDetails(entity, cardName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void moveCardToColumn(Connection connection, CardEntity card, int fromOrder, int toOrder) throws SQLException {
        new CardService(connection).update(entity, card, toOrder);

        var fromColumn = entity.getBoardColumns().stream()
                .filter(c -> c.getOrder() == fromOrder)
                .findFirst().orElseThrow();

        var toColumn = entity.getBoardColumns().stream()
                .filter(c -> c.getOrder() == toOrder)
                .findFirst().orElseThrow();

        fromColumn.getCards().removeIf((c -> c.getTitle().equals(card.getTitle())));
        toColumn.getCards().add(card);
        card.setBoardColumn(toColumn);

        System.out.println("Card movido com sucesso para a coluna " + toColumn.getName() + "!");
    }
}
