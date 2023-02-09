package fade.inject.event;

import fade.inject.event.events.MockEvent;
import fade.inject.exception.event.EventException;
import fade.inject.exception.event.EventInvocationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvocationTest {

    @Test
    @DisplayName("simple handler invocation with event type from parameter")
    void testSimpleHandlerInvocationWithEventTypeFromParameter() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event) {
                event.getContext().get().setString("updated");
            }
        });

        MockEvent event = MockEvent.from("initial");
        manager.invoke(event);

        assertEquals("updated", event.getContext().get().getString());
    }

    @Test
    @DisplayName("simple handler invocation with event type from annotation")
    void testSimpleHandlerInvocationWithAnnotationEventTypeFromAnnotation() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler(event = MockEvent.class)
            public void handle(@NotNull MockEvent.Context context) {
                context.setString("updated");
                assertTrue(context.returnTrue());
                assertFalse(context.returnFalse());
            }
        });

        MockEvent event = MockEvent.from("initial");
        manager.invoke(event);
    }

    @Test
    @DisplayName("simple handler with cancellable event")
    void testSimpleHandlerWithCancellableEvent() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event) {
                event.setResult(Cancellable.Result.Cancel);
            }
        });

        MockEvent event = MockEvent.from();
        manager.invoke(event);

        assertTrue(event.isCancelled());
    }

    @Test
    @DisplayName("simple handler invocation with context parameter")
    void testSimpleHandlerInvocationWithContextParameter() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                context.setString("B");
            }
        });

        MockEvent event = MockEvent.from("A");
        manager.invoke(event);

        assertEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("simple handler unregistration")
    void testSimpleHandlerUnregistration() {
        Manager manager = Manager.builder().build();
        MockEvent event = MockEvent.from("A");

        final class TestHandler {

            @Handler
            public void handle(@NotNull MockEvent event) {
                event.getContext().get().setString("B");
            }
        }

        manager.register(new TestHandler());
        manager.unregister(TestHandler.class);

        manager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("throw if exception occurs in handler")
    void testThrowIfExceptionOccursInHandler() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull MockEvent event, @NotNull MockEvent.Context context) {
                throw EventException.from("Test Exception");
            }
        });

        MockEvent event = MockEvent.from("A");
        assertThrows(EventInvocationException.class, () -> manager.invoke(event));

        assertNotEquals("B", event.getContext().get().getString());
    }

    @Test
    @DisplayName("throw if annotated method is not a valid handler method")
    void testThrowIfAnnotatedMethodIsNotAValidHandlerMethod() {
        Manager manager = Manager.builder().build();
        MockEvent event = MockEvent.from("A");

        assertThrows(EventException.class, () -> manager.register(new Object() {
            @Handler
            public void handle() {}
        }));
        manager.invoke(event);

        assertNotEquals("B", event.getContext().get().getString());
    }
}
