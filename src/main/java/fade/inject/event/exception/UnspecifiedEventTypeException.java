package fade.inject.event.exception;

import org.jetbrains.annotations.NotNull;

public final class UnspecifiedEventTypeException extends EventException {

    private UnspecifiedEventTypeException(String message) {
        super(message);
    }

    public static @NotNull EventException from(String message) {
        return new UnspecifiedEventTypeException(message);
    }

}
