package fade.inject.event;

import fade.inject.api.event.EventManager;
import fade.inject.api.event.Handler;
import fade.inject.api.event.PriorityGroup;
import fade.inject.event.events.MockEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityTest {

    @Test
    @DisplayName("prioritized handlers with ordinal")
    void testPrioritizedHandlersWithOrdinal() {
        EventManager eventManager = EventManager.create();

        // noinspection AnonymousInnerClassWithTooManyMethods
        eventManager.register(new Object() {
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
        eventManager.invoke(event);

        assertEquals("higher ordinal", event.getContext().get().getString());
    }

    @Test
    @DisplayName("prioritized handlers with group")
    void testPrioritizedHandlersWithGroup() {
        EventManager eventManager = EventManager.create();

        // noinspection AnonymousInnerClassWithTooManyMethods
        eventManager.register(new Object() {
            @Handler(event = MockEvent.class, group = PriorityGroup.High)
            public void handleHighPriority(MockEvent.Context context) {
                context.setString("high");
            }

            @Handler(event = MockEvent.class, group = PriorityGroup.Normal)
            public void handleNormalPriority(MockEvent.Context context) {
                context.setString("normal");
            }
        });

        MockEvent event = MockEvent.from("pre_invoke");
        eventManager.invoke(event);

        assertEquals("normal", event.getContext().get().getString());
    }
}
