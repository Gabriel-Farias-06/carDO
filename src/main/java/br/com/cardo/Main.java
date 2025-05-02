package br.com.cardo;

import br.com.cardo.persistence.migration.MigrationStrategy;
import br.com.cardo.ui.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;

import static br.com.cardo.persistence.config.ConnectonConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
            new MainMenu().execute();
        }
    }

}
