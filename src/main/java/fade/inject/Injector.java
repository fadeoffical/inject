package fade.inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Set;

public interface Injector {

    static @NotNull Injector create() {
        return Injector.builder().build();
    }

    static @NotNull InjectorBuilder builder() {
        return InjectorBuilderImpl.create();
    }

    /**
     * Constructs an object from the {@code cls}, injects the object with its dependencies and returns it.
     *
     * @param cls The base class of the object to construct.
     * @param <T> The type of the base class (automatically inferred from the {@code cls} parameter).
     *
     * @return The constructed and injected object.
     */
    <T> @NotNull T construct(Class<? extends T> cls);

    <T> @NotNull T construct(Class<? extends T> cls, @Range(from = -1, to = 65535 ) int ordinal);

    void inject(@NotNull Object object);

    void registerDependencyResolver(@NotNull DependencyResolver dependencyResolver);

    @Nullable Object resolveDependency(@NotNull Inject annotation, @NotNull Class<?> type);

    @NotNull List<?> resolveDependencies(@NotNull Inject annotation, @NotNull Class<?> type);

    sealed interface InjectorBuilder extends Builder<Injector> permits InjectorBuilderImpl {

        @NotNull InjectorBuilder withResolver(@NotNull DependencyResolver resolver);

        @NotNull Set<DependencyResolver> getResolvers();

    }
}
