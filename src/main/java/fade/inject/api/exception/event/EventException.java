package fade.inject.api.exception.event;


import fade.inject.api.exception.InjectException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A generic superclass for all exceptions relating to events.
 * <p>
 * Note, that generic in this context has nothing to do with generic types, but means that this exception does not
 * signify a specific error that might occur in the event lifecycle.
 * </p>
 * <p>
 * This exception should only be used in cases where no other, more specific exception exists. If this exception is
 * thrown on a specific error case, an exception with a more fitting name should be created and extend this class.
 * </p>
 */
public class EventException extends InjectException {

    /**
     * The internal constructor for the exception. This constructor should not be used except when creating a new event
     * exception.
     *
     * @param message The message.
     *
     * @see EventException#from(String)
     */
    EventException(String message) {
        super(message);
    }

    /**
     * The internal constructor for the exception. This constructor should not be used except when creating a new event
     * exception.
     *
     * @param message The message.
     * @param cause   The cause.
     *
     * @see EventException#from(String, Throwable)
     */
    EventException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@link EventException} from a specified {@link String message string}.
     *
     * @param message The exception message. (A null value indicates no message.)
     *
     * @return A new {@link EventException} instance.
     *
     * @see EventException The exception's documentation for more details about it.
     */
    @Contract("_ -> new")
    public static @NotNull EventException from(String message) {
        return new EventException(message);
    }

    /**
     * Constructs a new {@link EventException} from a specified {@link String message string} and a
     * {@link Throwable throwable}.
     *
     * @param message The exception message. (A null value indicates no message.)
     * @param cause   The cause of this exception. (A null value indicates no cause or the cause being unknown.)
     *
     * @return A new {@link EventException} instance.
     *
     * @see EventException The exception's documentation for more details about it.
     */
    @Contract("_, _ -> new")
    public static @NotNull EventException from(String message, Throwable cause) {
        return new EventException(message, cause);
    }
}
