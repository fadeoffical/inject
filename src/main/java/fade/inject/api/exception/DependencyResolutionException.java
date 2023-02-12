package fade.inject.api.exception;

public final class DependencyResolutionException extends InjectException {

    private DependencyResolutionException(String message) {
        super(message);
    }

    public static DependencyResolutionException from(String message) {
        return new DependencyResolutionException(message);
    }
}
