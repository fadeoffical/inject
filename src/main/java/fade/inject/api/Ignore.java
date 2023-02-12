package fade.inject.api;

import fade.inject.api.event.EventManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a method or class that should be ignored by the {@link EventManager}. A class annotated by this will not be
 * scanned for
 *
 * @see EventManager#register(Object)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {}
