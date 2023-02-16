package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Dependency<T> {

    private final Class<T> type;
    private final String id;

    Dependency(@NotNull Class<T> type, @NotNull String id) {
        this.type = type;
        this.id = id;
    }

    public static <T> @NotNull DependencyBuilder<T> ofType(@NotNull Class<T> type) {
        return new DependencyBuilder<>(type);
    }

    public @Nullable String getId() {
        return this.id;
    }

    public @NotNull Class<T> getType() {
        return this.type;
    }

    public boolean isOfType(@NotNull Class<?> type) {
        return type.isAssignableFrom(this.getType());
    }

    public boolean isIdEqual(@NotNull String id) {
        return "".equals(id) || id.equals(this.getId());
    }

    public abstract @Nullable T getObject();
}
