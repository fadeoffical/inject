package fade.inject.api.event;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2>Handler Method</h2>
 * <p>A handler method, also less commonly referred to as a 'handler' throughout this library, is a method, which
 * handles an {@link Event}. Handler methods get invoked by an {@link EventManager} by passing an event to the manager.
 * This annotation marks a method as a handler method.</p>
 * <br><br/>
 *
 * <h3>Valid Handler Methods</h3>
 * <p>A valid handler method is a public, non-static method, contained in any class, with a specified {@link Event}
 * type. This may be done via either this annotation's {@link Handler#event() event element} or the first parameter of
 * the method.</p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

    /**
     * The event type.
     * <p>If the handler does specify an event type, then the event type specified by this annotation must
     * either be the same, or a subclass of the handler method's event type. If the handler does not specify an event
     * type, the here given type is used as the event type.</p>
     *
     * @return The handler type.
     *
     * @see EventManager#register(Object)
     */
    @NotNull Class<? extends Event> event() default Event.class;

    /**
     * The priority group
     * <p>The priority groups are five distinct groups which are executed from highest to lowest. Handlers can specify
     * their priority group based on how early they must be executed.</p>
     * <p>Note that specifying priority groups without the need to do so may result in undesired behaviour, thus is
     * strictly discouraged!</p>
     *
     * @return The handler priority.
     *
     * @see PriorityGroup
     */
    @NotNull PriorityGroup group() default PriorityGroup.Normal;

    /**
     * The priority ordinal
     * <p>The ordinal within a {@link PriorityGroup priority group} is an integer, which is used to sort all handlers
     * into a sequence of descending ordinals.</p>
     * <p>This effectively results in handler methods with a higher priority ordinal being invoked
     * with more precedence than handler methods with a lower priority ordinal.</p>
     * <p>Note that specifying priority ordinals without the need to do so may result in undesired behaviour, thus is
     * strictly discouraged!</p>
     * <p>Note that a handler method in a higher priority group will get invoked earlier, even if the ordinal of this
     * handler is lower.</p>
     *
     * @return The ordinal.
     */
    int ordinal() default 0;

}
