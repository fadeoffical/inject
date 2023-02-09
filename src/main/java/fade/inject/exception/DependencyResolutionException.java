package fade.inject.exception;

public final class MissingDependencyException extends InjectException {

    private MissingDependencyException(String message) {
        super(message);
    }

    public static MissingDependencyException from(String message) {
        return new MissingDependencyException(message);
    }
}
