package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingletonDependency<T> extends Dependency<T> {

    protected SingletonDependency(@NotNull Class<T> type, @NotNull String id) {
        super(type, id);
    }

    @Override
    public @Nullable T getDependencyInstance() {
        return null;
    }
}
