package fade.inject.event;

import org.jetbrains.annotations.NotNull;


/**
 * A superclass for all {@link Cancellable} events.
 * <p>
 * This class implements the {@link Cancellable#getResult()} and {@link Cancellable#setResult(Result)} methods.
 * </p>
 */
public abstract class CancellableEvent extends Event implements Cancellable {

    /**
     * The current event result.
     */
    private @NotNull Result result = Result.Continue;

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Result getResult() {
        return this.result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResult(@NotNull Result result) {
        this.result = result;
    }

}
