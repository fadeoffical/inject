package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface DependencyResolver {

    @NotNull List<Dependency<?>> resolveAll(@NotNull Class<?> type);

    @NotNull List<Dependency<?>> resolveAll(@NotNull String id);

    @NotNull List<Dependency<?>> resolveAll(@NotNull Class<?> type, @NotNull String id);

    @NotNull Optional<Dependency<?>> resolve(@NotNull Class<?> type);

    @NotNull Optional<Dependency<?>> resolve(@NotNull String id);

    @NotNull Optional<Dependency<?>> resolve(@NotNull Class<?> type, @NotNull String id);
}
