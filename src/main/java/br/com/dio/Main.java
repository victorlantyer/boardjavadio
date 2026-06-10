package br.com.dio;

import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.migration.MigrationStrategy;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;
import br.com.dio.ui.BoardMenu;
import br.com.dio.ui.InputReader;
import br.com.dio.ui.MainMenu;

import java.sql.Connection;
import java.util.Scanner;

/**
 * Classe principal da aplicacao.
 */
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Inicializando aplicacao de Board de Tarefas...");
            System.out.println("Conectando ao banco de dados...");

            Connection connection = ConnectionConfig.getConnection();
            MigrationStrategy.executeMigrations(connection);
            connection.close();

            var boardDAO = new BoardDAO();
            var boardColumnDAO = new BoardColumnDAO();
            var cardDAO = new CardDAO();
            var blockDAO = new BlockDAO();

            var boardService = new BoardService(boardDAO, boardColumnDAO, cardDAO);
            var cardService = new CardService(boardColumnDAO, cardDAO, blockDAO);
            var boardQueryService = new BoardQueryService(boardDAO, boardColumnDAO, cardDAO);
            var boardColumnQueryService = new BoardColumnQueryService(boardDAO, boardColumnDAO, cardDAO);
            var cardQueryService = new CardQueryService(cardDAO, boardColumnDAO, blockDAO);

            var scanner = new Scanner(System.in);
            var inputReader = new InputReader(scanner);
            var boardMenu = new BoardMenu(
                    cardService,
                    boardQueryService,
                    boardColumnQueryService,
                    cardQueryService,
                    inputReader
            );
            var mainMenu = new MainMenu(boardService, boardMenu, inputReader);

            mainMenu.execute();
            scanner.close();
            System.out.println("Aplicacao encerrada.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar a aplicacao: " + e.getMessage());
            System.exit(1);
        }
    }
}
