package br.com.dio.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Estrategia de migracao do banco de dados usando Liquibase.
 */
public class MigrationStrategy {
    private static final String CHANGELOG_MASTER = "db/changelog/db.changelog-master.yml";

    public static void executeMigrations(Connection connection) throws Exception {
        try {
            ensureLiquibaseTables(connection);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new JdbcConnection(connection)
            );

            Liquibase liquibase = new Liquibase(
                    CHANGELOG_MASTER,
                    new ClassLoaderResourceAccessor(),
                    database
            );

            System.out.println("Executando migrations do Liquibase...");
            liquibase.update("");
            System.out.println("Migrations executadas com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao executar migrations: " + e.getMessage());
            throw new RuntimeException(
                    "Erro ao executar migrations. Verifique o arquivo de changelog em "
                            + CHANGELOG_MASTER + ". Detalhes: " + e.getMessage(),
                    e
            );
        }
    }

    private static void ensureLiquibaseTables(Connection connection) throws Exception {
        try (PreparedStatement changelog = connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS DATABASECHANGELOG (
                    ID VARCHAR(255) NOT NULL,
                    AUTHOR VARCHAR(255) NOT NULL,
                    FILENAME VARCHAR(255) NOT NULL,
                    DATEEXECUTED TIMESTAMP NOT NULL,
                    ORDEREXECUTED INT NOT NULL,
                    EXECTYPE VARCHAR(10) NOT NULL,
                    MD5SUM VARCHAR(35),
                    DESCRIPTION VARCHAR(255),
                    COMMENTS VARCHAR(255),
                    TAG VARCHAR(255),
                    LIQUIBASE VARCHAR(20),
                    CONTEXTS VARCHAR(255),
                    LABELS VARCHAR(255),
                    DEPLOYMENT_ID VARCHAR(10)
                )
                """)) {
            changelog.executeUpdate();
        }

        try (PreparedStatement changelogLock = connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS DATABASECHANGELOGLOCK (
                    ID INT NOT NULL PRIMARY KEY,
                    LOCKED BOOLEAN NOT NULL,
                    LOCKGRANTED TIMESTAMP NULL,
                    LOCKEDBY VARCHAR(255) NULL
                )
                """)) {
            changelogLock.executeUpdate();
        }

        try (PreparedStatement seedLock = connection.prepareStatement("""
                MERGE INTO DATABASECHANGELOGLOCK (ID, LOCKED, LOCKGRANTED, LOCKEDBY)
                KEY (ID) VALUES (1, FALSE, NULL, NULL)
                """)) {
            seedLock.executeUpdate();
        }
        connection.commit();
    }
}
