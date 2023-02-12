package fade.inject.api;

import org.jetbrains.annotations.NotNull;

/**
 * A builder class implements this interface to indicate that it is a builder. This class does not yet have any uses
 * beyond the {@link Builder#build()} method.
 *
 * @param <T> The type of the object that this builder builds. Used by the {@link Builder#build()} method as a return
 *            type.
 */
public interface Builder<T> { // todo: necessary?

    /**
     * Instantiates an object from this builder. The object is configured by calling the other methods on this builder,
     * which are provided by classes implementing this interface.
     *
     * @return The built object.
     */
    @NotNull T build();
}
