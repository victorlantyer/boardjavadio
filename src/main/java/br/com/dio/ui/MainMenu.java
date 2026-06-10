package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardService;

public class MainMenu {
    private final BoardService boardService;
    private final BoardMenu boardMenu;
    private final InputReader inputReader;

    public MainMenu(BoardService boardService, BoardMenu boardMenu, InputReader inputReader) {
        this.boardService = boardService;
        this.boardMenu = boardMenu;
        this.inputReader = inputReader;
    }

    public void execute() {
        while (true) {
            System.out.println();
            System.out.println("Bem vindo ao gerenciador de boards, escolha a opcao desejada");
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Listar boards");
            System.out.println("5 - Sair");

            int option = inputReader.readInt("Opcao: ");
            try {
                switch (option) {
                    case 1 -> createBoard();
                    case 2 -> selectBoard();
                    case 3 -> deleteBoard();
                    case 4 -> listBoards();
                    case 5 -> {
                        System.out.println("Finalizando...");
                        return;
                    }
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void createBoard() {
        System.out.println("Informe o nome do seu board");
        String boardName = inputReader.readText("> ");

        int extraPendingColumns = readNonNegativeInt(
                "Seu board tera colunas alem das 3 padroes? Se sim informe quantas, senao digite 0"
        );

        BoardEntity board = new BoardEntity(null, boardName);

        System.out.println("Informe o nome da coluna inicial do board");
        board.addColumn(new BoardColumnEntity(
                null,
                null,
                inputReader.readText("> "),
                0,
                BoardColumnKindEnum.INITIAL
        ));

        int order = 1;
        System.out.println("Informe o nome da coluna de tarefa pendente do board");
        board.addColumn(new BoardColumnEntity(
                null,
                null,
                inputReader.readText("> "),
                order++,
                BoardColumnKindEnum.PENDING
        ));

        for (int index = 1; index <= extraPendingColumns; index++) {
            System.out.println("Informe o nome da coluna pendente adicional " + index);
            board.addColumn(new BoardColumnEntity(
                    null,
                    null,
                    inputReader.readText("> "),
                    order++,
                    BoardColumnKindEnum.PENDING
            ));
        }

        System.out.println("Informe o nome da coluna final");
        board.addColumn(new BoardColumnEntity(
                null,
                null,
                inputReader.readText("> "),
                order++,
                BoardColumnKindEnum.FINAL
        ));

        System.out.println("Informe o nome da coluna de cancelamento do board");
        board.addColumn(new BoardColumnEntity(
                null,
                null,
                inputReader.readText("> "),
                order,
                BoardColumnKindEnum.CANCEL
        ));

        BoardEntity createdBoard = boardService.createBoard(board);
        System.out.printf("Board criado com ID %d.%n", createdBoard.getId());
    }

    private void selectBoard() {
        if (!listBoards()) {
            return;
        }
        Long boardId = inputReader.readLong("Informe o id do board: ");
        BoardEntity board = boardService.findBoard(boardId);
        boardMenu.execute(board.getId());
    }

    private void deleteBoard() {
        if (!listBoards()) {
            return;
        }
        Long boardId = inputReader.readLong("Informe o id do board que deseja excluir: ");
        String confirmation = inputReader.readText("Digite SIM para confirmar: ");
        if ("SIM".equalsIgnoreCase(confirmation.trim())) {
            boardService.deleteBoard(boardId);
            System.out.println("Board excluido.");
            return;
        }
        System.out.println("Exclusao cancelada.");
    }

    private boolean listBoards() {
        var boards = boardService.listBoards();
        if (boards.isEmpty()) {
            System.out.println("Nenhum board cadastrado.");
            return false;
        }
        boards.forEach(board -> System.out.printf("%d - %s%n", board.getId(), board.getName()));
        return true;
    }

    private int readNonNegativeInt(String message) {
        while (true) {
            int value = inputReader.readInt(message + ": ");
            if (value >= 0) {
                return value;
            }
            System.out.println("Informe um numero igual ou maior que zero.");
        }
    }
}
