package fade.inject.api.exception.event;

public final class PossibleMissingAnnotationException extends EventException {

    private PossibleMissingAnnotationException(String message) {
        super(message);
    }

    public static PossibleMissingAnnotationException from(String message) {
        return new PossibleMissingAnnotationException(message);
    }
}
