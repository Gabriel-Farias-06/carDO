package br.com.cardo.ui;

import br.com.cardo.persistence.config.ConnectonConfig;
import br.com.cardo.persistence.dao.BoardDAO;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.service.BoardQueryService;
import br.com.cardo.service.BoardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final BoardEntity entity;

    public void execute() throws SQLException {
        int option = 0;
        while (option != 6) {
            System.out.println("                        Board " + entity.getName() + "\n");
            System.out.println("                              Colunas\n");

            for (var column : entity.getBoardColumns()) {
                System.out.print("      " + column.getName());
            }

            System.out.println("\n\n1 - Criar card\n2 - Mover card\n3 - Bloquear um card\n4 - Desbloquear um card\n5 - Visualizar detalhes do card\n6 - Voltar\n7 - Sair");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> createCard();
                case 2 -> moveCard();
                case 3 -> blockCard();
                case 4 -> unblockCard();
                case 5 -> checkCardInfo();
                case 6 -> System.out.println("Voltando ao menu principal...");
                case 7 -> System.exit(0);
                default -> System.out.println("Digite uma opção válida");
            }

            MainMenu.clearConsole();
        }
    }

    private void createCard() throws SQLException {
        try(var connection = ConnectonConfig.getConnection()) {
            var dao = new BoardDAO(connection);
            var service = new BoardService(connection);
            System.out.print("Informe o nome do novo card: ");
            String name = scanner.nextLine();

        }
    }

    private void moveCard() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void checkCardInfo() {
        System.out.println("                              Colunas\n");

        for (var column : entity.getBoardColumns()) {
            System.out.print("      " + column.getName());
        }

        System.out.print("\n\nDigite a coluna do card que deseja visualizar: ");
        String columnName = scanner.nextLine();

        System.out.print("\n\nDigite o nome do card que deseja visualizar: ");
        String cardName = scanner.nextLine();
        try (var connection = ConnectonConfig.getConnection()) {
            new BoardQueryService(connection).showCardDetails(entity, columnName, cardName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
