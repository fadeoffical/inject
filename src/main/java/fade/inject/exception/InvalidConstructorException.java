package fade.inject.exception;

public final class InvalidConstructorException extends InjectException {

    private InvalidConstructorException(String message) {
        super(message);
    }

    public static InvalidConstructorException from(String message) {
        return new InvalidConstructorException(message);
    }
}
