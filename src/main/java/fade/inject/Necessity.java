package fade.inject;

import fade.inject.exception.DependencyResolutionException;

/**
 * Denotes the necessity of a value that is to be injected.
 */
public enum Necessity {

    /**
     * Denotes a dependency as optional. If an optional dependency cannot be resolved, null is passed instead.
     */
    Optional,

    /**
     * Denotes a dependency to be required. If a required dependency cannot be resolved, a
     * {@link DependencyResolutionException} is thrown.
     */
    Required
}
