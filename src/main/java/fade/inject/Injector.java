package fade.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
public interface Injector {

    /**
     * Constructs an object from the {@code cls}, injects the object with its dependencies and returns it.
     *
     * @param cls The base class of the object to construct.
     * @return The constructed and injected object.
     * @param <T> The type of the base class (automatically inferred from the {@code cls} parameter).
     */
    <T> T construct(Class<? extends T> cls);

    <T> void inject(T obj);

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @interface Inject {

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD, ElementType.PARAMETER})
        @interface Constructor {

        }
    }
}
