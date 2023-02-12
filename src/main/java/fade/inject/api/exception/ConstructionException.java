package fade.inject.api.exception;

import org.jetbrains.annotations.NotNull;

public final class ConstructionException extends InjectException {

    private ConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static @NotNull ConstructionException from(String message, Throwable cause) {
        return new ConstructionException(message, cause);
    }
}
