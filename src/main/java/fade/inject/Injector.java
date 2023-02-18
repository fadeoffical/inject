package fade.inject;

import org.jetbrains.annotations.NotNull;

public interface Injector {

    static @NotNull InjectorBuilder builder() {
        return new InjectorBuilder();
    }

    /**
     * Constructs an object from the {@code type}, injects the object with its dependencies and returns it.
     *
     * @param type The base class of the object to construct.
     * @param <T> The type of the base class (automatically inferred from the {@code type} parameter).
     * @return The constructed and injected object.
     */
    <T> @NotNull T construct(Class<T> type);

    /**
     * Injects the dependencies of the {@code object}.
     *
     * @param object The object to inject.
     */
    void inject(@NotNull Object object);
}
