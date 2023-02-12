package fade.inject.event.events;

import fade.inject.event.CancellableEvent;
import fade.inject.event.Contextual;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class MockEvent extends CancellableEvent implements Contextual<MockEvent.Context> {

    private final @NotNull Context context;

    private MockEvent(@Nullable String str) {
        this.context = new Context(str);
    }

    @Contract("_ -> new")
    public static @NotNull MockEvent from(@Nullable String str) {
        return new MockEvent(str);
    }

    @Contract(" -> new")
    public static @NotNull MockEvent from() {
        return new MockEvent(null);
    }

    @Override
    public @NotNull Optional<MockEvent.Context> getContext() {
        return Optional.of(this.context);
    }

    public static class Context {

        private @Nullable String str;

        Context(@Nullable String str) {
            this.str = str;
        }

        public boolean returnTrue() {
            return true;
        }

        public boolean returnFalse() {
            return false;
        }

        public @Nullable String getString() {
            return this.str;
        }

        public void setString(@Nullable String str) {
            this.str = str;
        }
    }
}
