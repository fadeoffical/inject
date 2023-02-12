package fade.inject.api.event;

import fade.inject.impl.event.EventManagerBuilderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * An event manager is a class that holds a reference to all handler methods. It can register handlers and invoke them
 * by calling the {@link EventManager#invoke(Event)} method.
 */
public interface EventManager {

    static @NotNull EventManager create() {
        return EventManager.builder().build();
    }

    static @NotNull EventManagerBuilder builder() {
        return EventManagerBuilderImpl.create();
    }

    void register(@NotNull Object handler);

    void unregister(@NotNull Object object);

    void unregister(@NotNull Class<?> handler);

    boolean isRegistered(@NotNull Class<?> handler);

    @NotNull Optional<Object> getHandler(@NotNull Class<?> handler);

    void invoke(@NotNull Event event);
}
