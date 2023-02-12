package fade.inject.exception.event;

import org.jetbrains.annotations.NotNull;

public final class EventTypeNotSpecifiedException extends EventException {

    private EventTypeNotSpecifiedException(String message) {
        super(message);
    }

    public static @NotNull EventException from(String message) {
        return new EventTypeNotSpecifiedException(message);
    }

}
