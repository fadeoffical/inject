package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class DependencyBuilder<T> {

    private final @NotNull Class<T> type;
    private String id;

    DependencyBuilder(@NotNull Class<T> type) {
        this.type = type;
    }

    public @NotNull DependencyBuilder<T> withId(@NotNull String id) {
        this.id = id;
        return this;
    }

    public @NotNull SingletonDependency<T> andValue(@NotNull T value) {
        return new SingletonDependency<>(this.type, this.id, value);
    }

    public @NotNull SupplyingDependency<T> andSupplier(@NotNull Supplier<T> supplier) {
        return new SupplyingDependency<>(this.type, this.id, supplier);
    }

    public @NotNull WeakReferenceDependency<T> andWeakReference(@NotNull WeakReference<T> reference) {
        return new WeakReferenceDependency<>(this.type, this.id, reference);
    }
}
