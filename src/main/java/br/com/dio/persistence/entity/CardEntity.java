package br.com.dio.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardEntity implements Serializable {
    private Long id;
    private Long boardColumnId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private final List<BlockEntity> blocks = new ArrayList<>();

    public CardEntity() {
    }

    public CardEntity(Long id, Long boardColumnId, String title, String description) {
        this.id = id;
        this.boardColumnId = boardColumnId;
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getBoardColumnId() {
        return boardColumnId;
    }

    public void setBoardColumnId(Long boardColumnId) {
        this.boardColumnId = boardColumnId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BlockEntity> getBlocks() {
        return blocks;
    }

    public Optional<BlockEntity> getActiveBlock() {
        return blocks.stream().filter(BlockEntity::isActive).findFirst();
    }

    public boolean isBlocked() {
        return getActiveBlock().isPresent();
    }
}
