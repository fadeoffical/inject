package fade.inject.event.exception;

public final class PossibleMissingHandlerMethodsException extends RuntimeException {

    private PossibleMissingHandlerMethodsException(String message) {
        super(message);
    }

    public static PossibleMissingHandlerMethodsException from(String message) {
        return new PossibleMissingHandlerMethodsException(message);
    }
}
