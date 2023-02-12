package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a singleton dependency, that is, a dependency that holds an instance to a singleton.
 *
 * @param <T> The dependency type.
 */
public final class SingletonDependency<T> extends Dependency<T> {

    private final @Nullable T singletonInstance;

    SingletonDependency(@NotNull Class<T> type, @NotNull String id, @Nullable T singletonInstance) {
        super(type, id);
        this.singletonInstance = singletonInstance;
    }

    @Override
    public @Nullable T getObject() {
        return this.singletonInstance;
    }
}
