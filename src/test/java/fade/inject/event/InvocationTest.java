package fade.inject.event;

import fade.inject.event.events.PreExecutionEvent;
import fade.inject.event.events.StringEvent;
import fade.inject.event.exception.EventException;
import fade.inject.event.exception.EventInvocationException;
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
            public void handle(@NotNull StringEvent event) {
                event.getContext().setString("updated");
            }
        });

        StringEvent event = new StringEvent("initial");
        manager.invoke(event);

        assertEquals("updated", event.getContext().getString());
    }

    @Test
    @DisplayName("simple handler invocation with event type from annotation")
    void testSimpleHandlerInvocationWithAnnotationEventTypeFromAnnotation() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler(event = StringEvent.class)
            public void handle(@NotNull StringEvent.StringEventContext context) {
                context.setString("updated");
            }
        });

        StringEvent event = new StringEvent("initial");
        manager.invoke(event);

        assertEquals("updated", event.getContext().getString());
    }

    @Test
    @DisplayName("simple handler with cancellable event")
    void testSimpleHandlerWithCancellableEvent() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull PreExecutionEvent event) {
                event.setResult(Cancellable.Result.Cancel);
            }
        });

        PreExecutionEvent event = new PreExecutionEvent();
        manager.invoke(event);

        assertTrue(event.isCancelled());
    }

    @Test
    @DisplayName("simple handler invocation with context parameter")
    void testSimpleHandlerInvocationWithContextParameter() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.setString("B");
            }
        });

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertEquals("B", event.getContext().getString());
    }

    @Test
    @DisplayName("simple handler unregistration")
    void testSimpleHandlerUnregistration() {
        Manager manager = Manager.builder().build();
        StringEvent event = new StringEvent("A");

        final class TestHandler {

            @Handler
            public void handle(@NotNull StringEvent event) {
                event.getContext().setString("B");
            }
        }

        manager.register(new TestHandler());
        manager.unregister(TestHandler.class);

        manager.invoke(event);

        assertNotEquals("B", event.getContext().getString());
    }

    @Test
    @DisplayName("throw if exception occurs in handler")
    void testThrowIfExceptionOccursInHandler() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                throw EventException.from("Test Exception");
            }
        });

        StringEvent event = new StringEvent("A");
        assertThrows(EventInvocationException.class, () -> manager.invoke(event));

        assertNotEquals("B", event.getContext().getString());
    }

    @Test
    @DisplayName("throw if annotated method is not a valid handler method")
    void testThrowIfAnnotatedMethodIsNotAValidHandlerMethod() {
        Manager manager = Manager.builder().build();
        StringEvent event = new StringEvent("A");

        assertThrows(EventException.class, () -> manager.register(new Object() {
            @SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment", "ReassignedVariable", "AssignmentToMethodParameter"})
            @Handler
            public void handle() {}
        }));
        manager.invoke(event);

        assertNotEquals("B", event.getContext().getString());
    }
}
