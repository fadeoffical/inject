package fade.inject.exception;

import org.jetbrains.annotations.NotNull;

public final class OrdinalOutOfBoundsException extends InjectException {

    private OrdinalOutOfBoundsException(String message) {
        super(message);
    }

    public static @NotNull OrdinalOutOfBoundsException from(String message) {
        return new OrdinalOutOfBoundsException(message);
    }
}
