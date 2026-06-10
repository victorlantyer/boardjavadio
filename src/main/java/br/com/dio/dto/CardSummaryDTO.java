package br.com.dio.dto;

public record CardSummaryDTO(
        Long id,
        String title,
        String description,
        boolean blocked
) {
}
