package fade.inject.event;

import fade.inject.event.events.StringEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityTest {

    @Test
    @DisplayName("prioritized handlers with ordinal")
    void testPrioritizedHandlersWithOrdinal() {
        Manager manager = Manager.create();

        // noinspection AnonymousInnerClassWithTooManyMethods
        manager.register(new Object() {
            @Handler(event = StringEvent.class, priorityOrdinal = 10)
            public void handleHighPriority(StringEvent.StringEventContext context) {
                context.setString("lower ordinal");
            }

            @Handler(event = StringEvent.class, priorityOrdinal = 12)
            public void handleNormalPriority(StringEvent.StringEventContext context) {
                context.setString("higher ordinal");
            }
        });

        StringEvent event = new StringEvent("pre_invoke");
        manager.invoke(event);

        assertEquals("higher ordinal", event.getContext().getString());
    }

    @Test
    @DisplayName("prioritized handlers with group")
    void testPrioritizedHandlersWithGroup() {
        Manager manager = Manager.create();

        // noinspection AnonymousInnerClassWithTooManyMethods
        manager.register(new Object() {
            @Handler(event = StringEvent.class, priorityGroup = PriorityGroup.High)
            public void handleHighPriority(StringEvent.StringEventContext context) {
                context.setString("high");
            }

            @Handler(event = StringEvent.class, priorityGroup = PriorityGroup.Normal)
            public void handleNormalPriority(StringEvent.StringEventContext context) {
                context.setString("normal");
            }
        });

        StringEvent event = new StringEvent("pre_invoke");
        manager.invoke(event);

        assertEquals("normal", event.getContext().getString());

    }
}
