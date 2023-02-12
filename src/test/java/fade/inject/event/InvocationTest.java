package fade.inject.event;

import fade.inject.api.event.Cancellable;
import fade.inject.api.event.EventManager;
import fade.inject.api.event.Handler;
import fade.inject.event.events.MockEvent;
import fade.inject.api.exception.event.EventException;
import fade.inject.api.exception.event.EventInvocationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvocationTest {

    @Test
    @DisplayName("simple handler invocation with event type from parameter")
    void testSimpleHandlerInvocationWithEventTypeFromParameter() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event) {
                event.getContext().get().setString("updated");
            }
        });

        MockEvent event = MockEvent.from("initial");
        eventManager.invoke(event);

        assertEquals("updated", event.getContext().get().getString());
    }

    @Test
    @DisplayName("simple handler invocation with event type from annotation")
    void testSimpleHandlerInvocationWithAnnotationEventTypeFromAnnotation() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler(event = MockEvent.class)
            public void handle(@NotNull MockEvent.Context context) {
                context.setString("updated");
                assertTrue(context.returnTrue());
                assertFalse(context.returnFalse());
            }
        });

        MockEvent event = MockEvent.from("initial");
        eventManager.invoke(event);
    }

    @Test
    @DisplayName("simple handler with cancellable event")
    void testSimpleHandlerWithCancellableEvent() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event) {
                event.setResult(Cancellable.Result.Cancel);
            }
        });

        MockEvent event = MockEvent.from();
        eventManager.invoke(event);

        assertTrue(event.isCancelled());
    }

    @Test
    @DisplayName("simple handler invocation with context parameter")
    void testSimpleHandlerInvocationWithContextParameter() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                context.setString("B");
            }
        });

        MockEvent event = MockEvent.from("A");
        eventManager.invoke(event);

        assertEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("simple handler unregistration")
    void testSimpleHandlerUnregistration() {
        EventManager eventManager = EventManager.builder().build();
        MockEvent event = MockEvent.from("A");

        final class TestHandler {

            @Handler
            public void handle(@NotNull MockEvent event) {
                event.getContext().get().setString("B");
            }
        }

        eventManager.register(new TestHandler());
        eventManager.unregister(TestHandler.class);

        eventManager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("throw if exception occurs in handler")
    void testThrowIfExceptionOccursInHandler() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                throw EventException.from("Test Exception");
            }
        });

        MockEvent event = MockEvent.from("A");
        assertThrows(EventInvocationException.class, () -> eventManager.invoke(event));

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("throw if annotated method is not a valid handler method")
    void testThrowIfAnnotatedMethodIsNotAValidHandlerMethod() {
        EventManager eventManager = EventManager.builder().build();
        MockEvent event = MockEvent.from("A");

        assertThrows(EventException.class, () -> eventManager.register(new Object() {
            @Handler
            public void handle() {}
        }));
        eventManager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }
}
