package fade.inject.exception.event;

public final class PossibleMissingHandlerMethodsException extends EventException {

    private PossibleMissingHandlerMethodsException(String message) {
        super(message);
    }

    public static PossibleMissingHandlerMethodsException from(String message) {
        return new PossibleMissingHandlerMethodsException(message);
    }
}
