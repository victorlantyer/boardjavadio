package br.com.dio.ui;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.dto.BoardDetailsDTO;
import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.dto.CardSummaryDTO;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;

public class BoardMenu {
    private final CardService cardService;
    private final BoardQueryService boardQueryService;
    private final BoardColumnQueryService boardColumnQueryService;
    private final CardQueryService cardQueryService;
    private final InputReader inputReader;

    public BoardMenu(CardService cardService,
                     BoardQueryService boardQueryService,
                     BoardColumnQueryService boardColumnQueryService,
                     CardQueryService cardQueryService,
                     InputReader inputReader) {
        this.cardService = cardService;
        this.boardQueryService = boardQueryService;
        this.boardColumnQueryService = boardColumnQueryService;
        this.cardQueryService = cardQueryService;
        this.inputReader = inputReader;
    }

    public void execute(Long boardId) {
        while (true) {
            System.out.println();
            System.out.println("Bem vindo ao board, selecione a operacao desejada");
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card para a proxima coluna");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Ver board");
            System.out.println("7 - Ver coluna com cards");
            System.out.println("8 - Ver card");
            System.out.println("9 - Voltar para o menu anterior");
            System.out.println("10 - Sair");

            int option = inputReader.readInt("Opcao: ");
            try {
                switch (option) {
                    case 1 -> createCard(boardId);
                    case 2 -> moveCardToNextColumn(boardId);
                    case 3 -> blockCard(boardId);
                    case 4 -> unblockCard(boardId);
                    case 5 -> cancelCard(boardId);
                    case 6 -> showBoard(boardId);
                    case 7 -> showColumn(boardId);
                    case 8 -> showCardDetails(boardId);
                    case 9 -> {
                        return;
                    }
                    case 10 -> {
                        System.out.println("Finalizando...");
                        System.exit(0);
                    }
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void createCard(Long boardId) {
        String title = inputReader.readText("Informe o titulo do card: ");
        String description = inputReader.readText("Informe a descricao do card: ");
        var card = cardService.createCard(boardId, title, description);
        System.out.printf("Card criado com ID %d.%n", card.getId());
    }

    private void moveCardToNextColumn(Long boardId) {
        Long cardId = inputReader.readLong("Informe o id do card: ");
        cardService.moveToNextColumn(boardId, cardId);
        System.out.println("Card movido para a proxima coluna.");
    }

    private void blockCard(Long boardId) {
        Long cardId = inputReader.readLong("Informe o id do card: ");
        String reason = inputReader.readText("Informe o motivo do bloqueio: ");
        cardService.block(boardId, cardId, reason);
        System.out.println("Card bloqueado.");
    }

    private void unblockCard(Long boardId) {
        Long cardId = inputReader.readLong("Informe o id do card: ");
        String reason = inputReader.readText("Informe o motivo do desbloqueio: ");
        cardService.unblock(boardId, cardId, reason);
        System.out.println("Card desbloqueado.");
    }

    private void cancelCard(Long boardId) {
        Long cardId = inputReader.readLong("Informe o id do card: ");
        cardService.cancel(boardId, cardId);
        System.out.println("Card cancelado.");
    }

    private void showBoard(Long boardId) {
        BoardDetailsDTO board = boardQueryService.findById(boardId);
        System.out.printf("%nBoard %d - %s%n", board.id(), board.name());

        for (BoardColumnInfoDTO column : board.columns()) {
            System.out.printf(
                    "%d - %s [%s] - %d card(s)%n",
                    column.id(),
                    column.name(),
                    column.kind(),
                    column.cardsAmount()
            );
        }
    }

    private void showColumn(Long boardId) {
        var columns = boardColumnQueryService.findByBoardId(boardId);
        System.out.println("Colunas disponiveis:");
        columns.forEach(column -> System.out.printf(
                "%d - %s [%s]%n",
                column.id(),
                column.name(),
                column.kind()
        ));

        Long columnId = inputReader.readLong("Informe o id da coluna: ");
        BoardColumnInfoDTO column = boardColumnQueryService.findById(boardId, columnId);
        var cards = cardQueryService.findSummariesByColumn(boardId, columnId);

        System.out.printf("%nColuna %s%n", column.name());
        System.out.println("Tipo: " + column.kind());
        if (cards.isEmpty()) {
            System.out.println("Nenhum card nesta coluna.");
            return;
        }

        cards.forEach(card -> printCardSummary(card));
    }

    private void showCardDetails(Long boardId) {
        Long cardId = inputReader.readLong("Informe o id do card: ");
        CardDetailsDTO card = cardQueryService.findById(boardId, cardId);

        System.out.println("\n=== Detalhes do card ===");
        System.out.println("ID: " + card.id());
        System.out.println("Titulo: " + card.title());
        System.out.println("Descricao: " + (card.description().isBlank() ? "Sem descricao" : card.description()));
        System.out.printf("Coluna atual: %d - %s%n", card.currentColumnId(), card.currentColumnName());
        System.out.println("Criado em: " + card.createdAt());
        System.out.println("Bloqueado: " + (card.blocked() ? "Sim" : "Nao"));
        if (card.blocked()) {
            System.out.println("Motivo do bloqueio ativo: " + card.blockReason());
        }
        System.out.println("Quantidade total de bloqueios: " + card.totalBlocks());
    }

    private void printCardSummary(CardSummaryDTO card) {
        System.out.printf(
                "%d - %s%n%s%n",
                card.id(),
                card.title(),
                card.description().isBlank() ? "Sem descricao" : card.description()
        );
    }
}
