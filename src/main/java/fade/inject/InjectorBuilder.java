package fade.inject;

import fade.inject.dependency.Dependency;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class InjectorBuilder {

    private final List<Dependency<?>> dependencies;
    private Injector superInjector;

    InjectorBuilder() {
        this.dependencies = new ArrayList<>();
    }

    public @NotNull InjectorBuilder inheritsFrom(@NotNull Injector superInjector) {
        this.superInjector = superInjector;
        return this;
    }

    public @NotNull InjectorBuilder withDependency(@NotNull Dependency<?> dependency) {
        this.dependencies.add(dependency);
        return this;
    }

    public @NotNull ModifiableInjector createModifiable() {
        return new ModifiableInjector(this.dependencies);
    }

    public @NotNull Injector create() {
        return new UnmodifiableInjector(this.dependencies);
    }
}
