package br.com.dio.persistence.entity;

public enum BoardColumnKindEnum {
    INITIAL,
    PENDING,
    FINAL,
    CANCEL;

    /**
     * Converte uma string para enum BoardColumnKindEnum.
     *
     * @param value string do tipo de coluna
     * @return o enum correspondente
     * @throws IllegalArgumentException se a string não corresponder a nenhum tipo
     */
    public static BoardColumnKindEnum fromString(String value) {
        try {
            return BoardColumnKindEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de coluna inválido: " + value, e);
        }
    }

    /**
     * Converte o enum para string.
     *
     * @return string do tipo de coluna
     */
    @Override
    public String toString() {
        return this.name();
    }
}
