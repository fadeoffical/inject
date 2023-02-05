package fade.inject.event.exception;

public final class PossibleMissingAnnotationException extends RuntimeException {

    private PossibleMissingAnnotationException(String message) {
        super(message);
    }

    public static PossibleMissingAnnotationException from(String message) {
        return new PossibleMissingAnnotationException(message);
    }
}
