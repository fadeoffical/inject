package fade.inject;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class InjectorBuilderImpl implements Injector.InjectorBuilder {

    private final @NotNull Set<DependencyResolver> resolvers;

    private InjectorBuilderImpl() {
        this.resolvers = new HashSet<>();
    }

    public static @NotNull InjectorBuilderImpl create() {
        return new InjectorBuilderImpl();
    }

    @Override
    public @NotNull Injector build() {
        return new InjectorImpl(this.resolvers);
    }

    @Override
    public Injector.@NotNull InjectorBuilder withResolver(@NotNull DependencyResolver resolver) {
        this.resolvers.add(resolver);
        return this;
    }

    @Override
    public @NotNull Set<DependencyResolver> getResolvers() {
        return Collections.unmodifiableSet(this.resolvers);
    }
}
