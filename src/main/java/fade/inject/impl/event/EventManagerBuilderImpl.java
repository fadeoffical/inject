package fade.inject.impl.event;

import fade.inject.event.EventManager;
import fade.inject.event.EventManagerBuilder;
import org.jetbrains.annotations.NotNull;

public final class EventManagerBuilderImpl implements EventManagerBuilder {

    private EventManagerBuilderImpl() {}

    public static EventManagerBuilderImpl create() {
        return new EventManagerBuilderImpl();
    }

    @Override
    public @NotNull EventManager build() {
        return new EventManagerImpl();
    }
}
