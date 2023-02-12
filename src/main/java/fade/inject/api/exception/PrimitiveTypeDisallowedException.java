package fade.inject.api.exception;

// todo: it is not necessary to treat primitives differently yet; remove if unnecessary
public final class PrimitiveTypeDisallowedException extends InjectException {

    private PrimitiveTypeDisallowedException(String message) {
        super(message);
    }

    public static PrimitiveTypeDisallowedException from(String message) {
        return new PrimitiveTypeDisallowedException(message);
    }
}
