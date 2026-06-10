package br.com.dio.exception;

/**
 * Exceção lançada quando uma operação é tentada em um card finalizado.
 */
public class CardFinishedException extends RuntimeException {
    public CardFinishedException(String message) {
        super(message);
    }

    public CardFinishedException(String message, Throwable cause) {
        super(message, cause);
    }
}
