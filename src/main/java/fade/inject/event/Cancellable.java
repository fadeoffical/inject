package fade.inject.event;

import org.jetbrains.annotations.NotNull;

/**
 * Marks an event as be cancellable.
 * <p>
 * A cancelled event is an indication by at least one handler, that it requested to cancel the execution of the subject
 * code, which is dependent on this event, without another, less prioritized handler setting the result to continue.
 * </p>
 * <p>
 * Note that the actual effect that an event result has, is entirely dependent on the implementation of the event
 * invoker.
 * </p>
 */
public interface Cancellable {

    /**
     * Returns {@code true} if the current {@link Result result} is equal to {@link Result#Cancel cancel}, else false.
     *
     * @return Whether the current {@link Result result} is {@link Result#Cancel cancel}.
     */
    default boolean isCancelled() {
        return this.isResult(Result.Cancel);
    }

    /**
     * Checks if the supplied result is equal to the current result of the event and returns the corresponding boolean
     * value.
     * <p>
     * Calling "{@code event.isResult(result)}" is equivalent to "{@code event.getResult() == result}".
     * </p>
     *
     * @param result The result to check for.
     *
     * @return True, if the event result is equal to {@code result}.
     */
    default boolean isResult(@NotNull Result result) {
        return this.getResult() == result;
    }

    /**
     * Gets the current event result.
     *
     * @return The result after the event execution.
     */
    @NotNull Result getResult();

    /**
     * Sets the current event result to the value specified for {@code result}.
     *
     * @param result The new {@link Result}
     */
    void setResult(@NotNull Result result);

    /**
     * Sets the current {@link Result result} to {@link Result#Cancel cancelled}.
     */
    default void setCancelled() {
        this.setResult(Result.Cancel);
    }

    /**
     * Returns {@code true} if the current {@link Result result} is equal to {@link Result#Continue continue}, else
     * false.
     *
     * @return Whether the current {@link Result result} is {@link Result#Continue continue}.
     */
    default boolean isContinuing() {
        return this.isResult(Result.Continue);
    }

    /**
     * Sets the current {@link Result result} to {@link Result#Continue continue}.
     */
    default void setContinuing() {
        this.setResult(Result.Continue);
    }

    /**
     * An enum, representing the possible states of a cancellable event. These are '{@link Result#Continue continue}'
     * and '{@link Result#Cancel cancel}'.
     */
    enum Result {

        /**
         * Represents an event result of cancelled.
         * <p>
         * This result indicates that the event passed with a request to cancel the execution of the subject code
         * dependent on this event state.
         * </p>
         * <p>
         * Note that the actual effect that an event result has, is entirely dependent on the implementation of the
         * event invoker.
         * </p>
         */
        Cancel,

        /**
         * Represents an event result of continue.
         * <p>
         * This result indicates that the event passed without any request to cancel the execution of the subject code
         * dependent on this event state.
         * </p>
         * <p>
         * Note that the actual effect that an event result has, is entirely dependent on the implementation of the
         * event invoker.
         * </p>
         */
        Continue
    }
}
