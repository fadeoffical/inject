package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DependencyManager implements DependencyRegistry, DependencyResolver {

    private final Set<Dependency<?>> dependencies;

    DependencyManager() {
        this.dependencies = new HashSet<>();
    }

    public static @NotNull DependencyManager create() {
        return new DependencyManager();
    }


    @Override
    public @NotNull List<Dependency<?>> resolveAll(@NotNull Class<?> type) {
        return this.dependencies.stream().filter(dependency -> dependency.isOfType(type)).toList();
    }

    @Override
    public @NotNull List<Dependency<?>> resolveAll(@NotNull String id) {
        return this.dependencies.stream().filter(dependency -> dependency.hasId(id)).toList();
    }

    @Override
    public @NotNull List<Dependency<?>> resolveAll(@NotNull Class<?> type, @NotNull String id) {
        return this.dependencies.stream()
                .filter(dependency -> dependency.isOfType(type))
                .filter(dependency -> dependency.hasId(id))
                .toList();
    }

    @Override
    public @NotNull Optional<Dependency<?>> resolve(@NotNull Class<?> type) {
        return this.resolveAll(type).stream().findFirst();
    }

    @Override
    public @NotNull Optional<Dependency<?>> resolve(@NotNull String id) {
        return this.resolveAll(id).stream().findFirst();
    }

    @Override
    public @NotNull Optional<Dependency<?>> resolve(@NotNull Class<?> type, @NotNull String id) {
        return this.resolveAll(type, id).stream().findFirst();
    }

    @Override
    public @NotNull DependencyRegistry register(@NotNull Dependency<?> dependency) {
        this.dependencies.add(dependency);
        return this;
    }

    @Override
    public @NotNull DependencyRegistry registerAll(@NotNull List<Dependency<?>> dependencies) {
        this.dependencies.addAll(dependencies);
        return this;
    }
}
