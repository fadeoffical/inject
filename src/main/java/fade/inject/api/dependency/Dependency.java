package fade.inject.api.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Dependency<T> {

    private final @NotNull Class<T> type;
    private final @NotNull String id;

    protected Dependency(@NotNull Class<T> type, @NotNull String id) {
        this.type = type;
        this.id = id;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull Class<T> getType() {
        return this.type;
    }

    public abstract @Nullable T getDependencyInstance();
}
