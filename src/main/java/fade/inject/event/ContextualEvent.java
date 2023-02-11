package fade.inject.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * <h2>Contextual Event</h2>
 * <p>This abstract subclass of {@link Event} is an implementation of the {@link Contextual} interface and is intended
 * to be used as a base for contextual events.</p>
 *
 * @param <T> The context type.
 */
public abstract class ContextualEvent<T> extends Event implements Contextual<T> {

    private final @Nullable T context;

    public ContextualEvent(@Nullable T context) {
        this.context = context;
    }

    @Override
    public @NotNull Optional<T> getContext() {
        return Optional.ofNullable(this.context);
    }
}
