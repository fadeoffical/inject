package fade.inject.api.exception.event;

import org.jetbrains.annotations.NotNull;

public final class EventTypeIncompatibleException extends EventException {

    private EventTypeIncompatibleException(String message) {
        super(message);
    }

    public static @NotNull EventException from(String message) {
        return new EventTypeIncompatibleException(message);
    }

}
