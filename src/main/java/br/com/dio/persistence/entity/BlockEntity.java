package br.com.dio.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BlockEntity implements Serializable {
    private Long id;
    private Long cardId;
    private String blockReason;
    private LocalDateTime blockedAt;
    private String unblockReason;
    private LocalDateTime unblockedAt;
    private boolean active;

    public BlockEntity() {
    }

    public BlockEntity(Long id, Long cardId, String blockReason) {
        this.id = id;
        this.cardId = cardId;
        this.blockReason = blockReason;
        this.blockedAt = LocalDateTime.now();
        this.active = true;
    }

    public BlockEntity(Long id,
                       Long cardId,
                       String blockReason,
                       LocalDateTime blockedAt,
                       String unblockReason,
                       LocalDateTime unblockedAt) {
        this.id = id;
        this.cardId = cardId;
        this.blockReason = blockReason;
        this.blockedAt = blockedAt;
        this.unblockReason = unblockReason;
        this.unblockedAt = unblockedAt;
        this.active = unblockedAt == null;
    }

    public Long getId() {
        return id;
    }

    public Long getCardId() {
        return cardId;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public String getUnblockReason() {
        return unblockReason;
    }

    public LocalDateTime getUnblockedAt() {
        return unblockedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void unblock(String reason) {
        this.unblockReason = reason;
        this.unblockedAt = LocalDateTime.now();
        this.active = false;
    }
}
