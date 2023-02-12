package fade.inject.exception;

import org.jetbrains.annotations.NotNull;

public class InjectException extends RuntimeException {

    protected InjectException() {
        super();
    }

    protected InjectException(String message) {
        super(message);
    }

    protected InjectException(String message, Throwable cause) {
        super(message, cause);
    }

    protected InjectException(Throwable cause) {
        super(cause);
    }

    protected InjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static @NotNull InjectException from(String message, Throwable cause) {
        return new InjectException(message, cause);
    }
}
