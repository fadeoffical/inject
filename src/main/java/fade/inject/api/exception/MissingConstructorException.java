package fade.inject.api.exception;

public final class MissingConstructorException extends InjectException {

    private MissingConstructorException(String message) {
        super(message);
    }

    public static MissingConstructorException from(String message) {
        return new MissingConstructorException(message);
    }
}
