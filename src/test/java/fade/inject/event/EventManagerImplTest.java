package fade.inject.event;

import fade.inject.Ignore;
import fade.inject.event.exception.EventException;
import fade.inject.event.exception.EventInvocationException;
import fade.inject.event.exception.PossibleMissingAnnotationException;
import fade.inject.event.exception.PossibleMissingHandlerMethodsException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventManagerImplTest {

    @Test
    void testHandlerInvocation() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull StringEvent event) {
                event.context().string("B");
            }
        });

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertEquals("B", event.context().string());
    }

    @Test
    void testHandlerInvocationWithAnnotationEventType() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler(event = StringEvent.class)
            public void handle(@NotNull StringEvent.StringEventContext context) {
                context.string("B");
            }
        });

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertEquals("B", event.context().string());
    }

    @Test
    void testHandlerWithContextParameter() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.string("B");
            }
        });

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertEquals("B", event.context().string());
    }

    @Test
    void testThrowOnMissingAnnotation() {
        EventManager eventManager = EventManager.builder().build();
        assertThrows(PossibleMissingAnnotationException.class, () -> eventManager.register(new Object() {
            @SuppressWarnings("unused") // intentional
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.string("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.context().string());
    }

    @Test
    void testThrowOnMissingAnnotationWithSuppression() {
        EventManager eventManager = EventManager.builder().build();

        // noinspection AnonymousInnerClassWithTooManyMethods
        assertDoesNotThrow(() -> eventManager.register(new Object() {
            @Ignore
            public void handle(@NotNull StringEvent event) {
                event.context().string("C");
            }

            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.string("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertEquals("B", event.context().string());
    }

    @Test
    void testIgnoreAnnotationOnIgnoredMethod() {
        EventManager eventManager = EventManager.builder().build();

        assertDoesNotThrow(() -> eventManager.register(new Object() {
            @Handler
            @Ignore
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.string("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.context().string());
    }

    @Test
    void testIgnoreAnnotationOnIgnoredClass() {
        EventManager eventManager = EventManager.builder().build();

        @Ignore
        final class IgnoredHandler {
            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.string("B");
            }
        }
        assertDoesNotThrow(() -> eventManager.register(new IgnoredHandler()));

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.context().string());
    }

    @Test
    void testDontThrowOnHandlerMethodWithIgnoreAnnotation() {
        EventManager eventManager = EventManager.builder().build();
        assertDoesNotThrow(() -> eventManager.register(new Object() {
            @Ignore
            public void handle(@NotNull StringEvent event) {
                event.context().string("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertNotEquals("B", event.context().string());
    }

    @Test
    void testThrowOnNoHandlerMethods() {
        EventManager eventManager = EventManager.builder().build();
        assertThrows(PossibleMissingHandlerMethodsException.class, () -> eventManager.register(new Object()));

        StringEvent event = new StringEvent("A");
        eventManager.invoke(event);

        assertEquals("A", event.context().string());
    }

    @Test
    void testThrowOnExceptionInHandler() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                throw EventException.from("Test Exception");
            }
        });

        StringEvent event = new StringEvent("A");
        assertThrows(EventInvocationException.class, () -> eventManager.invoke(event));

        assertNotEquals("B", event.context().string());
    }

    @Test
    void testThrowOnInvalidHandlerMethod() {
        EventManager eventManager = EventManager.builder().build();
        StringEvent event = new StringEvent("A");


        assertThrows(EventException.class, () -> eventManager.register(new Object() {
            @SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment", "ReassignedVariable", "AssignmentToMethodParameter"})
            @Handler
            public void handle(@NotNull String string) {
                string = "B";
            }
        }));
        eventManager.invoke(event);

        assertNotEquals("B", event.context().string());
    }

    @Test
    void testCancelledEvent() {
        EventManager eventManager = EventManager.builder().build();
        eventManager.register(new Object() {
            @Handler
            public void handle(@NotNull PreExecutionEvent event) {
                event.setResult(Cancellable.Result.Cancel);
            }
        });

        PreExecutionEvent event = new PreExecutionEvent();
        eventManager.invoke(event);

        assertTrue(event.isCancelled());
    }

    @Test
    void testHandlerUnregistration() {
        EventManager eventManager = EventManager.builder().build();
        StringEvent event = new StringEvent("A");

        final class TestHandler {

            @Handler
            public void handle(@NotNull StringEvent event) {
                event.context().string("B");
            }
        }

        eventManager.register(new TestHandler());
        eventManager.unregister(TestHandler.class);

        eventManager.invoke(event);

        assertNotEquals("B", event.context().string());
    }

    @SuppressWarnings("SameParameterValue")
    private static final class StringEvent implements Event {

        private final StringEventContext context;

        private StringEvent(@NotNull String string) {
            this.context = new StringEventContext(string);
        }

        @Override
        public @NotNull StringEventContext context() {
            return this.context;
        }

        private static final class StringEventContext implements Context {

            private @NotNull String string;

            private StringEventContext(@NotNull String string) {
                this.string = string;
            }

            @NotNull String string() {
                return this.string;
            }

            void string(@NotNull String string) {
                this.string = string;
            }
        }
    }

    private static final class PreExecutionEvent extends CancellableEvent {

        private final @NotNull Context context;

        private PreExecutionEvent() {
            this.context = new Context() {};
        }

        @Override
        public @NotNull Context context() {
            return this.context;
        }

    }
}
