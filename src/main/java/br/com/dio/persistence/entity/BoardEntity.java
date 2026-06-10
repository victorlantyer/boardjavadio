package br.com.dio.persistence.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BoardEntity implements Serializable {
    private Long id;
    private String name;
    private final List<BoardColumnEntity> columns = new ArrayList<>();

    public BoardEntity() {
    }

    public BoardEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addColumn(BoardColumnEntity column) {
        columns.add(column);
    }

    public List<BoardColumnEntity> getColumns() {
        columns.sort(Comparator.comparingInt(BoardColumnEntity::getOrder));
        return columns;
    }
}
