package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class WeakReferenceDependency<T> extends Dependency<T> {

    private final @NotNull WeakReference<T> reference;

    WeakReferenceDependency(@NotNull Class<T> type, @NotNull String id, @NotNull WeakReference<T> reference) {
        super(type, id);
        this.reference = reference;
    }

    @Override
    public @Nullable T getObject() {
        return this.reference.get();
    }
}
