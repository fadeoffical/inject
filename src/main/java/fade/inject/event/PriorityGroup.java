package fade.inject.event;

public enum PriorityGroup {

    /**
     * Represents the highest priority group. Handlers with this priority will be prioritized over all others; they will
     * be invoked before all others.
     *
     * @see Handler#test()  For even finer priority control.
     */
    Highest,

    /**
     * Represents the second-highest priority group. Handlers with this priority will be prioritized over all others
     * except the {@link PriorityGroup#Highest highest} group.
     *
     * @see Handler#test() For even finer priority control.
     */
    High,

    /**
     * Represents the normal priority group. Handlers with this priority will be prioritized over all lower ones. This
     * is the default priority group for handlers.
     *
     * @see Handler#test() For even finer priority control.
     */
    Normal,

    /**
     * Represents the low priority group. Handlers with this priority will be prioritized only over the lowest group.
     *
     * @see Handler#test() For even finer priority control.
     */
    Low,

    /**
     * Represents the lowest priority group. Invocation of handlers in this group will be deferred to the last places in
     * the order of handler invocation.
     *
     * @see Handler#test() For even finer priority control.
     */
    Lowest
}
