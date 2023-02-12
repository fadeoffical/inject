package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SupplyingDependency<T> extends Dependency<T> {

    private final @NotNull Supplier<T> supplier;

    SupplyingDependency(@NotNull Class<T> type, @NotNull String id, @NotNull Supplier<T> supplier) {
        super(type, id);
        this.supplier = supplier;
    }

    @Override
    public @Nullable T getObject() {
        return this.supplier.get();
    }
}
