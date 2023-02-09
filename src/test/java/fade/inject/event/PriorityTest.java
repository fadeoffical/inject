package fade.inject.event;

import fade.inject.event.events.MockEvent;
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
            @Handler(event = MockEvent.class, ordinal = 10)
            public void handleHighPriority(MockEvent.Context context) {
                context.setString("lower ordinal");
            }

            @Handler(event = MockEvent.class, ordinal = 12)
            public void handleNormalPriority(MockEvent.Context context) {
                context.setString("higher ordinal");
            }
        });

        MockEvent event = MockEvent.from("pre_invoke");
        manager.invoke(event);

        assertEquals("higher ordinal", event.getContext().getString());
    }

    @Test
    @DisplayName("prioritized handlers with group")
    void testPrioritizedHandlersWithGroup() {
        Manager manager = Manager.create();

        // noinspection AnonymousInnerClassWithTooManyMethods
        manager.register(new Object() {
            @Handler(event = MockEvent.class, priorityGroup = PriorityGroup.High)
            public void handleHighPriority(MockEvent.Context context) {
                context.setString("high");
            }

            @Handler(event = MockEvent.class, priorityGroup = PriorityGroup.Normal)
            public void handleNormalPriority(MockEvent.Context context) {
                context.setString("normal");
            }
        });

        MockEvent event = MockEvent.from("pre_invoke");
        manager.invoke(event);

        assertEquals("normal", event.getContext().getString());
    }
}
