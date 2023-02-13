package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Dependency<T> {

    private final @NotNull Class<T> type;
    private final @NotNull String id;

    Dependency(@NotNull Class<T> type, @NotNull String id) {
        this.type = type;
        this.id = id;
    }

    public static <T> @NotNull DependencyBuilder<? extends T> ofType(@NotNull Class<? extends T> type) {
        return new DependencyBuilder<>(type);
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull Class<T> getType() {
        return this.type;
    }

    public boolean isOfType(@NotNull Class<?> type) {
        return type.isAssignableFrom(this.getType());
    }

    public boolean hasId(@NotNull String id) {
        return this.getId().equals(id);
    }

    public abstract @Nullable T getObject();
}
