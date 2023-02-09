package fade.inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface Inject {

    @Range(from = -1, to = 65535) int ordinal() default -1;

    @NotNull String id() default "";

    @NotNull Necessity necessity() default Necessity.Optional;
}
