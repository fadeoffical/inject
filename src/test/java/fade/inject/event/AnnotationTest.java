package fade.inject.event;

import fade.inject.api.Ignore;
import fade.inject.api.event.EventManager;
import fade.inject.api.event.Handler;
import fade.inject.event.events.MockEvent;
import fade.inject.api.exception.event.PossibleMissingAnnotationException;
import fade.inject.api.exception.event.PossibleMissingHandlerMethodsException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationTest {

    @Test
    @DisplayName("throw if handler class has no methods")
    void testThrowIfHandlerClassHasNoMethods() {
        EventManager eventManager = EventManager.builder().build();
        assertThrows(PossibleMissingAnnotationException.class, () -> eventManager.register(new Object() {
            @SuppressWarnings("unused") // intentional
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                context.setString("B");
            }
        }));

        MockEvent event = MockEvent.from("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("do not throw if handler is ignored")
    void testDoNotThrowIfHandlerIsIgnored() {
        EventManager eventManager = EventManager.builder().build();

        // noinspection AnonymousInnerClassWithTooManyMethods
        assertDoesNotThrow(() -> eventManager.register(new Object() {
            @Ignore
            public void handle(@NotNull MockEvent event) {
                event.getContext().get().setString("C");
            }

            @Handler
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                context.setString("B");
            }
        }));

        MockEvent event = MockEvent.from("A");
        eventManager.invoke(event);

        assertEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("do not throw if handler class has only ignored handlers")
    void testDoNotThrowIfHandlerClassHasOnlyIgnoredHandlers() {
        EventManager eventManager = EventManager.builder().build();

        assertDoesNotThrow(() -> eventManager.register(new Object() {
            @Handler
            @Ignore
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                context.setString("B");
            }
        }));

        MockEvent event = MockEvent.from("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("do not register handlers of ignored handler class")
    void testDoNotRegisterHandlersOfIgnoredHandlerClass() {
        EventManager eventManager = EventManager.builder().build();

        @Ignore
        final class IgnoredHandler {

            @Handler
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                context.setString("B");
            }
        }
        assertDoesNotThrow(() -> eventManager.register(new IgnoredHandler()));

        MockEvent event = MockEvent.from("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("do not throw if handler class has only ignored handler-like methods")
    void testDoNotThrowIfHandlerClassHasOnlyIgnoredHandlerLikeMethods() {
        EventManager eventManager = EventManager.builder().build();
        assertDoesNotThrow(() -> eventManager.register(new Object() {
            @Ignore
            public void handle(@NotNull MockEvent event) {
                event.getContext().get().setString("B");
            }
        }));

        MockEvent event = MockEvent.from("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("throw if handler class has no handler methods")
    void testThrowIfHandlerClassHasNoHandlerMethods() {
        EventManager eventManager = EventManager.builder().build();
        assertThrows(PossibleMissingHandlerMethodsException.class, () -> eventManager.register(new Object()));

        MockEvent event = MockEvent.from("A");
        eventManager.invoke(event);

        assertEquals("A", event.getContext().get().getString());
    }
}
