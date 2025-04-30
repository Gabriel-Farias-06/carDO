package br.com.cardo;

import br.com.cardo.persistence.migration.MigrationStrategy;

import java.sql.Connection;
import java.sql.SQLException;

import static br.com.cardo.persistence.config.ConnectonConfig.getConnection;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
