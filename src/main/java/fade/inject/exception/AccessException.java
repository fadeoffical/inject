package fade.inject.exception;

import org.jetbrains.annotations.NotNull;

public final class AccessException extends InjectException {

    private AccessException(String message) {
        super(message);
    }

    public static @NotNull AccessException from(String message) {
        return new AccessException(message);
    }
}
