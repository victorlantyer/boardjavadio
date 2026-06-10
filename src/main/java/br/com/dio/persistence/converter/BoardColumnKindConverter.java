package br.com.dio.persistence.converter;

import br.com.dio.persistence.entity.BoardColumnKindEnum;

/**
 * Conversor para o tipo BoardColumnKindEnum.
 * Converte entre string (do banco de dados) e enum (da aplicação).
 */
public class BoardColumnKindConverter {

    /**
     * Converte uma string para enum BoardColumnKindEnum.
     *
     * @param databaseValue valor armazenado no banco (ex: "INITIAL", "PENDING", "FINAL", "CANCEL")
     * @return o enum correspondente
     * @throws IllegalArgumentException se o valor não for válido
     */
    public static BoardColumnKindEnum toDatabaseColumn(String databaseValue) {
        if (databaseValue == null || databaseValue.isEmpty()) {
            throw new IllegalArgumentException("Tipo de coluna não pode ser nulo ou vazio");
        }
        return BoardColumnKindEnum.fromString(databaseValue);
    }

    /**
     * Converte um enum BoardColumnKindEnum para string.
     *
     * @param attribute o enum a converter
     * @return a string correspondente (para armazenar no banco)
     */
    public static String convertToDatabaseColumn(BoardColumnKindEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }
}
