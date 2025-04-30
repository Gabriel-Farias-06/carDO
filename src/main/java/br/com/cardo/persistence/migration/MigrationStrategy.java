package br.com.cardo.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static br.com.cardo.persistence.config.ConnectonConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {
    private  final Connection connection;

    public void executeMigration() {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        try(FileOutputStream fos = new FileOutputStream("liquibase.log")) {
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));
            try(
                    Connection connection = getConnection();
                    JdbcConnection jdbcConnection = new JdbcConnection(connection);
            ){
                Liquibase liquibase = new Liquibase(
                        "/db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection);
                liquibase.update();
            } catch (SQLException | LiquibaseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
