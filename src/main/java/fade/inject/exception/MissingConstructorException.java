package fade.inject.exception;

import org.jetbrains.annotations.NotNull;

public final class MissingConstructorException extends InjectException {

    private MissingConstructorException() {
        super();
    }

    private MissingConstructorException(String message) {
        super(message);
    }

    public static MissingConstructorException from(String message) {
        return new MissingConstructorException(message);
    }

    public static @NotNull MissingConstructorException from() {
        return new MissingConstructorException();
    }
}
