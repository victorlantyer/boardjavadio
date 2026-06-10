package br.com.dio.dto;

import java.time.LocalDateTime;

public record CardDetailsDTO(
        Long id,
        String title,
        String description,
        Long currentColumnId,
        String currentColumnName,
        boolean blocked,
        String blockReason,
        int totalBlocks,
        LocalDateTime createdAt
) {
}
