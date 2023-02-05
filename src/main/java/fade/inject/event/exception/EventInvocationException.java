package fade.inject.event.exception;

import org.jetbrains.annotations.NotNull;

public final class EventInvocationException extends EventException {

    private EventInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static @NotNull EventException from(String message, Throwable cause) {
        return new EventInvocationException(message, cause);
    }
}
