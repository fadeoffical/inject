package fade.inject;

import fade.inject.dependency.Dependency;
import fade.inject.dependency.DependencyManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnmodifiableInjector implements Injector {

    private final DependencyManager dependencyManager;

    public UnmodifiableInjector(@NotNull List<Dependency<?>> dependencies) {
        this.dependencyManager = DependencyManager.builder()
                .withDependencies(dependencies)
                .create();
    }

    @Override
    public <T> @NotNull T construct(Class<T> type) {
        return null;
    }

    @Override
    public void inject(@NotNull Object object) {

    }
}
