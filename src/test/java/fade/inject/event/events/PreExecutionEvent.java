package fade.inject.event.events;

import fade.inject.event.CancellableEvent;
import org.jetbrains.annotations.NotNull;

public final class PreExecutionEvent extends CancellableEvent {

    private final @NotNull Context context;

    public PreExecutionEvent() {
        this.context = new Context() {};
    }

    @Override
    public @NotNull Context getContext() {
        return this.context;
    }

}
