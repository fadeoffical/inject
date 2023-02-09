package fade.inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

@FunctionalInterface
public interface DependencyResolver {

    @Nullable WeakReference<?> resolve(@NotNull String id, @NotNull Class<?> type);
}
