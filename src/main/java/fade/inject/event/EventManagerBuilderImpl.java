package fade.inject.event;

import org.jetbrains.annotations.NotNull;

public final class EventManagerBuilderImpl implements EventManager.Builder {

    private EventManagerBuilderImpl() {}

    static EventManagerBuilderImpl create() {
        return new EventManagerBuilderImpl();
    }

    @Override
    public @NotNull EventManager build() {
        return new EventManagerImpl();
    }
}
