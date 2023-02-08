package fade.inject.exception;

import org.jetbrains.annotations.NotNull;

public final class TooManyConstructorsException extends InjectException {

    private TooManyConstructorsException(@NotNull String message) {
        super(message);
    }

    public static TooManyConstructorsException from(@NotNull String message) {
        return new TooManyConstructorsException(message);
    }
}
