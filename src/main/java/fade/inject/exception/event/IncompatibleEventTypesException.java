package fade.inject.exception.event;

import org.jetbrains.annotations.NotNull;

public final class IncompatibleEventTypesException extends EventException {

    private IncompatibleEventTypesException(String message) {
        super(message);
    }

    public static @NotNull EventException from(String message) {
        return new IncompatibleEventTypesException(message);
    }

}
