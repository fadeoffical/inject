package fade.inject.event;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a handler method.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

    /**
     * The event type.
     * <p>
     * If the event handler method does specify an event type, then the event type specified by
     * {@link Handler this annotation} must either be the same or a subclass of the handler methods event type. If the
     * handler does not specify an event type, the here given event type is used as the event type
     * </p>
     *
     * @return The event type.
     *
     * @see Manager#register(Object)
     */
    @NotNull Class<? extends Event> event() default Event.class;

    // todo: tests
    // todo: javadocs
    @NotNull Priority priority() default @Priority;


}
