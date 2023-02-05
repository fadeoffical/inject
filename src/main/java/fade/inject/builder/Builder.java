package fade.inject.builder;

import org.jetbrains.annotations.NotNull;

public interface Builder<T> { // todo: necessary?

    @NotNull T build();
}
