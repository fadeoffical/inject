package fade.inject.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents an event that may have a context bound to it. A context is an object that holds additional information
 * about the event, which may not necessarily be directly related to the event.
 *
 * @param <T> The type of the context that this event may bind.
 */
public interface Contextual<T> {

    /**
     * Returns whether the event has a context bound to it; that is, when the context is present.
     * <p>
     * Calling this is equivalent to calling
     * <code>
     * context.getContext().isPresent()
     * </code>
     * </p>
     *
     * @return Whether the event has a context.
     */
    default boolean hasContext() {
        return this.getContext().isPresent();
    }

    /**
     * Returns the context wrapped in an {@link Optional}.
     *
     * @return The context.
     */
    @NotNull Optional<T> getContext();
}
