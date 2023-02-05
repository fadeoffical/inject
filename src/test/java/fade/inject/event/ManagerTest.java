package fade.inject.event;

import fade.inject.Ignore;
import fade.inject.event.exception.EventException;
import fade.inject.event.exception.EventInvocationException;
import fade.inject.event.exception.PossibleMissingAnnotationException;
import fade.inject.event.exception.PossibleMissingHandlerMethodsException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    void testHandlerInvocation() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler
            public void handle(@NotNull StringEvent event) {
                event.getContext().setString("B");
            }
        });

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertEquals("B", event.getContext().getString());
    }

    @Test
    void testHandlerInvocationWithAnnotationEventType() {
        Manager manager = Manager.builder().build();
        manager.register(new Object() {
            @Handler(event = StringEvent.class)
            public void handle(@NotNull StringEvent.StringEventContext context) {
                context.setString("B");
            }
        });

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertEquals("B", event.getContext().getString());
    }

    @Test
    void testHandlerWithContextParameter() {
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
    void testThrowOnMissingAnnotation() {
        Manager manager = Manager.builder().build();
        assertThrows(PossibleMissingAnnotationException.class, () -> manager.register(new Object() {
            @SuppressWarnings("unused") // intentional
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.setString("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertNotEquals("B", event.getContext().getString());
    }

    @Test
    void testThrowOnMissingAnnotationWithSuppression() {
        Manager manager = Manager.builder().build();

        // noinspection AnonymousInnerClassWithTooManyMethods
        assertDoesNotThrow(() -> manager.register(new Object() {
            @Ignore
            public void handle(@NotNull StringEvent event) {
                event.getContext().setString("C");
            }

            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.setString("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertEquals("B", event.getContext().getString());
    }

    @Test
    void testIgnoreAnnotationOnIgnoredMethod() {
        Manager manager = Manager.builder().build();

        assertDoesNotThrow(() -> manager.register(new Object() {
            @Handler
            @Ignore
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.setString("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertNotEquals("B", event.getContext().getString());
    }

    @Test
    void testIgnoreAnnotationOnIgnoredClass() {
        Manager manager = Manager.builder().build();

        @Ignore
        final class IgnoredHandler {

            @Handler
            public void handle(@NotNull StringEvent event, @NotNull StringEvent.StringEventContext context) {
                context.setString("B");
            }
        }
        assertDoesNotThrow(() -> manager.register(new IgnoredHandler()));

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertNotEquals("B", event.getContext().getString());
    }

    @Test
    void testDontThrowOnHandlerMethodWithIgnoreAnnotation() {
        Manager manager = Manager.builder().build();
        assertDoesNotThrow(() -> manager.register(new Object() {
            @Ignore
            public void handle(@NotNull StringEvent event) {
                event.getContext().setString("B");
            }
        }));

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertNotEquals("B", event.getContext().getString());
    }

    @Test
    void testThrowOnNoHandlerMethods() {
        Manager manager = Manager.builder().build();
        assertThrows(PossibleMissingHandlerMethodsException.class, () -> manager.register(new Object()));

        StringEvent event = new StringEvent("A");
        manager.invoke(event);

        assertEquals("A", event.getContext().getString());
    }

    @Test
    void testThrowOnExceptionInHandler() {
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
    void testThrowOnInvalidHandlerMethod() {
        Manager manager = Manager.builder().build();
        StringEvent event = new StringEvent("A");


        assertThrows(EventException.class, () -> manager.register(new Object() {
            @SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment", "ReassignedVariable", "AssignmentToMethodParameter"})
            @Handler
            public void handle(@NotNull String unusedParam) {
                unusedParam = "B";
            }
        }));
        manager.invoke(event);

        assertNotEquals("B", event.getContext().getString());
    }

    @Test
    void testCancelledEvent() {
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
    void testHandlerUnregistration() {
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
    void testPrioritizedHandlers() {
        Manager manager = Manager.create();

        //noinspection AnonymousInnerClassWithTooManyMethods
        manager.register(new Object() {
            @Handler(event = StringEvent.class, priority = Priority.High)
            public void handleHighPriority(StringEvent.StringEventContext context) {
                context.setString("high");
            }

            @Handler(event = StringEvent.class, priority = Priority.Normal)
            public void handleNormalPriority(StringEvent.StringEventContext context) {
                context.setString("normal");
            }
        });

        StringEvent event = new StringEvent("pre_invoke");
        manager.invoke(event);

        assertEquals("normal", event.getContext().getString());

    }

    private static final class StringEvent implements Event {

        private final StringEventContext context;

        private StringEvent(@NotNull String str) {
            this.context = new StringEventContext(str);
        }

        @Override
        public @NotNull StringEventContext getContext() {
            return this.context;
        }

        private static final class StringEventContext implements Context {

            private @NotNull String str;

            private StringEventContext(@NotNull String str) {
                this.str = str;
            }

            @NotNull String getString() {
                return this.str;
            }

            void setString(@NotNull String str) {
                this.str = str;
            }
        }
    }

    private static final class PreExecutionEvent extends CancellableEvent {

        private final @NotNull Context context;

        private PreExecutionEvent() {
            this.context = new Context() {};
        }

        @Override
        public @NotNull Context getContext() {
            return this.context;
        }

    }
}
