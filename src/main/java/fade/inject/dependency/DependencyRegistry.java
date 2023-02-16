package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DependencyRegistry {

    @NotNull DependencyRegistry register(@NotNull Dependency<?> dependency);

    @NotNull DependencyRegistry registerAll(@NotNull List<Dependency<?>> dependencies);

}
