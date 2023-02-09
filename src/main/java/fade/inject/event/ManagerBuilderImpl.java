package fade.inject.event;

import org.jetbrains.annotations.NotNull;

public final class ManagerBuilderImpl implements EventManager.ManagerBuilder {

    private ManagerBuilderImpl() {}

    static ManagerBuilderImpl create() {
        return new ManagerBuilderImpl();
    }

    @Override
    public @NotNull EventManager build() {
        return new EventManagerImpl();
    }
}
