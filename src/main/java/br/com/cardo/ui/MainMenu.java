package br.com.cardo.ui;

import br.com.cardo.persistence.config.ConnectonConfig;
import br.com.cardo.persistence.dao.BoardDAO;
import br.com.cardo.persistence.entity.BoardColumnEntity;
import br.com.cardo.persistence.entity.BoardColumnKindEnum;
import br.com.cardo.persistence.entity.BoardEntity;
import br.com.cardo.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public static void clearConsole()
    {
        for (int i = 0; i < 50; i++)
            System.out.println();
    }

    public void execute() throws SQLException {
        int option = 0;

        while(option != 4) {
            System.out.println("                Bem vindo ao Cardo\n     Seu Gerenciador de Tarefas Personalizado\n");
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair\n");
            System.out.print("Digite a operação desejada: ");
            option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Digite uma opção válida");
            }

            System.out.print("Digite ENTER para continuar");
            scanner.nextLine();

            clearConsole();
        }
    }

    private void createBoard() {
        var entity = new BoardEntity();
        try(var connection = ConnectonConfig.getConnection()) {
            var dao = new BoardDAO(connection);
            var service = new BoardService(connection);
            System.out.print("Informe o nome do novo board: ");
            String name = scanner.nextLine();
            if (dao.findByName(name).isPresent()) {
                System.out.println("Os nomes dos boards precisam ser únicos!");
                return;
            }

            entity.setName(name);

            List<BoardColumnEntity> columns = new ArrayList<>();

            columns.add(createColumn("A fazer", BoardColumnKindEnum.INITIAL, 0));
            columns.add(createColumn("Fazendo", BoardColumnKindEnum.PENDING, 1));
            columns.add(createColumn("Finalizada", BoardColumnKindEnum.FINAL, 2));
            columns.add(createColumn("Cancelada", BoardColumnKindEnum.CANCEL, 3));

            entity.setBoardColumns(columns);

            service.insert(entity);

            System.out.println("Board " + entity.getName() + " criado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao criar o board " + entity.getName());
            e.printStackTrace();
        }
    }

    private void selectBoard() throws SQLException {
        System.out.print("Informe o nome do board a ser selecionado: ");
        String name = scanner.nextLine();
        try(var connection = ConnectonConfig.getConnection()) {
            var dao = new BoardDAO(connection);
            if (dao.findByName(name).isEmpty()) {
                System.out.println("O board " + name + " não foi encontrado");
                return;
            }

            var boardMenu = new BoardMenu(dao.findByName(name).get());
            boardMenu.execute();
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.print("Informe o nome do board a ser excluído: ");
        var name = scanner.nextLine();
        try(var connection = ConnectonConfig.getConnection()) {
            var service = new BoardService(connection);
            if(service.delete(name))
                System.out.println("Board " + name + " deletado com sucesso!");
            else
                System.out.println("Board " + name + " não foi encontrado");
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setId(java.util.UUID.randomUUID());
        boardColumn.setName(name);
        boardColumn.setOrder(order);
        boardColumn.setKind(kind);
        return boardColumn;
    }
}
