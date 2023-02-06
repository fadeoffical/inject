package fade.inject.event;

import org.jetbrains.annotations.NotNull;

/**
 * Specifies the priority of a handler.
 *
 * @see Priority#ordinal()
 * @see Priority#group()
 * @see Group
 */
@interface Priority {

    /**
     * The priority group of a handler. This allows to specify the invocation priority group of a handler.
     *
     * @return The priority group.
     *
     * @see Group
     */
    @NotNull Group group() default Group.Normal;

    /**
     * The ordinal within a group. Handlers with a higher priority ordinal get executed with more precedence than
     * handlers with a lower priority ordinal.
     * <p>
     * This method should be used when even finer control of handler priority is needed.
     * </p>
     * <p>
     * Note that a handler in a higher group will still get invoked first, even if the ordinal is lower.
     * </p>
     *
     * @return The ordinal.
     */
    int ordinal() default 0;

    /**
     * Represents the priority of an event. An event with a higher priority will be prioritized in the execution order.
     * In practice this means that handlers with the highest priority will be invoked first and handlers with the lowest
     * priority will be executed last.
     */
    enum Group {

        /**
         * Represents the highest priority group. Handlers with this priority will be prioritized over all others; they
         * will be invoked before all others.
         *
         * @see Priority#ordinal() For even finer priority control.
         */
        Highest,

        /**
         * Represents the second-highest priority group. Handlers with this priority will be prioritized over all others
         * except the {@link Group#Highest highest} group.
         *
         * @see Priority#ordinal() For even finer priority control.
         */
        High,

        /**
         * Represents the normal priority group. Handlers with this priority will be prioritized over all lower ones.
         * This is the default priority group for handlers.
         *
         * @see Priority#ordinal() For even finer priority control.
         */
        Normal,

        /**
         * Represents the low priority group. Handlers with this priority will be prioritized only over the lowest
         * group.
         *
         * @see Priority#ordinal() For even finer priority control.
         */
        Low,

        /**
         * Represents the lowest priority group. Invocation of handlers in this group will be deferred to the last
         * places in the order of handler invocation.
         *
         * @see Priority#ordinal() For even finer priority control.
         */
        Lowest
    }
}
