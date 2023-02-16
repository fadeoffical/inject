package fade.inject.internal;

import fade.inject.Inject;
import fade.inject.exception.*;
import fade.mirror.MParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Util {

    private Util() {}

    public static @NotNull List<Field> getFieldsFromObject(@NotNull Object object) {
        Class<?> type = object.getClass();
        return getFieldsFromClassIncludingSuperclasses(type).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> assertAccess(field, object))
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .filter(field -> {
                    try {
                        return field.get(object) == null;
                    } catch (IllegalAccessException exception) {
                        throw InjectException.from("Could not check if field was already injected", exception);
                    }
                })
                .toList();
    }

    public static @NotNull List<Field> getFieldsFromClassIncludingSuperclasses(@NotNull Class<?> type) {
        List<Field> fields = new ArrayList<>(List.of(type.getDeclaredFields()));

        Class<?> current = type;
        while ((current = current.getSuperclass()) != null) {
            fields.addAll(List.of(current.getDeclaredFields()));
        }

        return fields;
    }

    // todo: fix the spaghetti
    public static @NotNull Constructor<?> getConstructorFromClass(@NotNull Class<?> type, int ordinal) {
        Constructor<?>[] constructors = type.getConstructors();
        if (constructors.length == 0)
            throw MissingConstructorException.from("Class '%s' has no constructors".formatted(type.getName()));

        if (ordinal != -1) return getConstructorWithOrdinal(type, ordinal);

        Inject inject = type.getAnnotation(Inject.class);
        if (inject != null) return getConstructorWithOrdinal(type, inject.ordinal());

        return Arrays.stream(constructors)
                .filter(Util::isConstructorValid)
                .filter(constructor -> assertAccess(constructor, null))
                .findFirst()
                .orElseThrow(() -> MissingConstructorException.from("Class '%s' has no valid constructors".formatted(type.getName())));
    }

    public static @NotNull Constructor<?> getConstructorWithOrdinal(@NotNull Class<?> type, @Range(from = 0, to = 65535) int ordinal) {
        Constructor<?>[] constructors = type.getConstructors();
        if (ordinal >= constructors.length)
            throw OrdinalOutOfBoundsException.from("Ordinal '%s' is out of bounds".formatted(ordinal));

        Constructor<?> constructor = constructors[ordinal];
        if (!isConstructorValid(constructor))
            throw InvalidConstructorException.from("Constructor '%s' in class '%s' is not a valid injectable constructor".formatted(getConstructorName(constructor), type.getName()));

        if (!assertAccess(constructor, null))
            throw AccessException.from("Cannot access constructor '%s' in class '%s'".formatted(getConstructorName(constructor), type.getName()));

        return constructor;
    }

    public static boolean isConstructorValid(@NotNull Constructor<?> constructor) {
        return true;
    }

    public static @NotNull String getConstructorName(@NotNull Constructor<?> constructor) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(constructor.getName()).append('(');

        for (Class<?> parameterType : constructor.getParameterTypes()) {
            stringBuilder.append(parameterType.getName());
            stringBuilder.append(", ");
        }

        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    public static boolean isParameterValid(@NotNull MParameter<?> parameter) {
        return parameter.isAnnotatedWith(Inject.class);
    }

    private static boolean assertAccess(@NotNull AccessibleObject accessible, @Nullable Object reference) {
        return accessible.canAccess(reference) || accessible.trySetAccessible();
    }
}
