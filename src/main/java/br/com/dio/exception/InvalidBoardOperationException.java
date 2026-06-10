package br.com.dio.exception;

/**
 * Exceção lançada quando uma operação inválida é tentada no board.
 */
public class InvalidBoardOperationException extends RuntimeException {
    public InvalidBoardOperationException(String message) {
        super(message);
    }

    public InvalidBoardOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
