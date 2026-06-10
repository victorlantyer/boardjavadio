package br.com.dio.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuracao de conexao JDBC.
 * Usa H2 local por padrao e aceita MySQL quando DB_URL/DB_USER/DB_PASSWORD estao definidos.
 */
public class ConnectionConfig {
    private static final String DEFAULT_EMBEDDED_URL =
            "jdbc:h2:file:./.board-data/board;MODE=MySQL;AUTO_SERVER=TRUE";
    private static final String DB_URL = readEnv("DB_URL", DEFAULT_EMBEDDED_URL);
    private static final String DB_USER = readEnv("DB_USER", defaultUser(DB_URL));
    private static final String DB_PASSWORD = readEnv("DB_PASSWORD", defaultPassword(DB_URL));

    static {
        try {
            Class.forName(resolveDriverClass(DB_URL));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Nao foi possivel carregar o driver JDBC para a URL configurada: " + DB_URL,
                    e
            );
        }
    }

    private ConnectionConfig() {
    }

    private static String readEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private static String defaultUser(String url) {
        if (url.startsWith("jdbc:h2:")) {
            return "sa";
        }
        return "root";
    }

    private static String defaultPassword(String url) {
        if (url.startsWith("jdbc:h2:")) {
            return "";
        }
        return "root";
    }

    private static String resolveDriverClass(String url) {
        if (url.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        }
        if (url.startsWith("jdbc:mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        }
        throw new IllegalArgumentException("URL de banco nao suportada: " + url);
    }

    /**
     * Obtem uma conexao JDBC com autoCommit desabilitado.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new SQLException(
                    "Erro ao conectar ao banco de dados. Sem variaveis de ambiente, a aplicacao usa "
                            + "o banco local H2 em .board-data. Para usar MySQL, configure DB_URL, "
                            + "DB_USER e DB_PASSWORD. Detalhes: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Fecha a conexao com seguranca.
     */
    public static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexao: " + e.getMessage());
        }
    }
}
