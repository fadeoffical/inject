package fade.inject.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DependencyManagerBuilder {

    private final Set<Dependency<?>> dependencies;

    DependencyManagerBuilder() {
        this.dependencies = new HashSet<>();
    }

    public @NotNull DependencyManagerBuilder withDependencies(@NotNull Collection<Dependency<?>> dependencies) {
        this.dependencies.addAll(dependencies);
        return this;
    }

    public @NotNull DependencyManagerBuilder withDependency(@NotNull Dependency<?> dependency) {
        this.dependencies.add(dependency);
        return this;
    }

    public @NotNull DependencyManager create() {
        return new DependencyManager();
    }
}
