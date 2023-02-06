package fade.inject.event.events;

import fade.inject.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NothingEvent implements Event {

    @Override
    public @Nullable Context getContext() {
        return null;
    }
}
