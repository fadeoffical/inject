package fade.inject.event;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Manager {

    static @NotNull Manager create() {
        return Manager.builder().build();
    }

    static @NotNull Manager.ManagerBuilder builder() {
        return ManagerBuilderImpl.create();
    }

    void register(@NotNull Object handler);

    void unregister(@NotNull Object object);

    void unregister(@NotNull Class<?> handler);

    boolean isRegistered(@NotNull Class<?> handler);

    @NotNull Optional<Object> getHandler(@NotNull Class<?> handler);

    void invoke(@NotNull Event event);

    interface ManagerBuilder extends fade.inject.builder.Builder<Manager> {}
}
