package fade.inject.event.events;

import fade.inject.event.Event;
import org.jetbrains.annotations.NotNull;

public final class StringEvent implements Event {

    private final StringEventContext context;

    public StringEvent(@NotNull String str) {
        this.context = new StringEventContext(str);
    }

    @Override
    public @NotNull StringEventContext getContext() {
        return this.context;
    }

    public static final class StringEventContext implements Context {

        private @NotNull String str;

        private StringEventContext(@NotNull String str) {
            this.str = str;
        }

        @NotNull
        public String getString() {
            return this.str;
        }

        public void setString(@NotNull String str) {
            this.str = str;
        }
    }
}
