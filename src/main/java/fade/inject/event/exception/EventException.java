package fade.inject.event.exception;

public class EventException extends RuntimeException {

    EventException(String message) {
        super(message);
    }

    EventException(String message, Throwable cause) {
        super(message, cause);
    }

    public static EventException from(String message) {
        return new EventException(message);
    }

    public static EventException from(String message, Throwable cause) {
        return new EventException(message, cause);
    }
}
