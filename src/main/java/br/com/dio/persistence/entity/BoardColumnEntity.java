package br.com.dio.persistence.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoardColumnEntity implements Serializable {
    private Long id;
    private Long boardId;
    private String name;
    private int order;
    private BoardColumnKindEnum kind;
    private final List<CardEntity> cards = new ArrayList<>();

    public BoardColumnEntity() {
    }

    public BoardColumnEntity(Long id, Long boardId, String name, int order, BoardColumnKindEnum kind) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.order = order;
        this.kind = kind;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setKind(BoardColumnKindEnum kind) {
        this.kind = kind;
    }

    public BoardColumnKindEnum getKind() {
        return kind;
    }

    public void addCard(CardEntity card) {
        cards.add(card);
    }

    public List<CardEntity> getCards() {
        return cards;
    }
}
