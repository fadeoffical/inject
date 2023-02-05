package fade.inject.event;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface EventManager {

    static @NotNull EventManager create() {
        return EventManager.builder().build();
    }

    static @NotNull Builder builder() {
        return EventManagerBuilderImpl.create();
    }

    void register(@NotNull Object handler);

    void unregister(@NotNull Object object);

    void unregister(@NotNull Class<?> handler);

    boolean isRegistered(@NotNull Class<?> handler);

    @NotNull Optional<Object> getHandler(@NotNull Class<?> handler);

    void invoke(@NotNull Event event);

    interface Builder extends fade.inject.builder.Builder<EventManager> {}
}
