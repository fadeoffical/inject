package fade.inject.event;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a handler method.
 * <p>
 * A handler method is a public, non-static method contained in any class, with a specified event type (either by
 * {@link Handler#event() annotation} or a parameter of the handler method).
 * </p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

    /**
     * The handler type.
     * <p>
     * If the handler does specify an event type, then the event type specified by {@link Handler this annotation} must
     * either be the same or a subclass of the handler method's event type. If the handler does not specify an event
     * type, the here given event type is used as the event type
     * </p>
     *
     * @return The handler type.
     *
     * @see Manager#register(Object)
     */
    @NotNull Class<? extends Event> event() default Event.class;

    /**
     * The handler priority.
     * <p>
     * If the handler does not specify a priority, a {@link Priority priority} of {@link Priority.Group group}
     * {@link Priority.Group#Normal normal} and an {@link Priority.Group#ordinal() ordinal} of {@code 0} is used. It is
     * recommended not to set custom handler priorities unless necessary.
     * </p>
     *
     * @return The handler priority.
     */
    // todo: tests
    @NotNull Priority priority() default @Priority;


}
