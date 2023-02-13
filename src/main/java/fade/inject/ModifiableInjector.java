package fade.inject;

import fade.inject.dependency.Dependency;
import fade.inject.dependency.DependencyManager;
import fade.inject.dependency.DependencyRegistry;
import fade.inject.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ModifiableInjector implements Injector, DependencyRegistry {

    private final DependencyManager dependencyManager;

    ModifiableInjector(@NotNull List<Dependency<?>> dependencies) {
        this.dependencyManager = DependencyManager.create();
    }

    private static @NotNull List<Field> getFieldsFromObject(@NotNull Object object) {
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

    @SuppressWarnings("NestedAssignment")
    private static @NotNull List<Field> getFieldsFromClassIncludingSuperclasses(@NotNull Class<?> type) {
        List<Field> fields = new ArrayList<>(List.of(type.getDeclaredFields()));

        Class<?> current = type;
        while ((current = current.getSuperclass()) != null) {
            fields.addAll(List.of(current.getDeclaredFields()));
        }

        return fields;
    }

    // todo: fix the spaghetti
    private static @NotNull Constructor<?> getConstructorFromClass(@NotNull Class<?> type, int ordinal) {
        Constructor<?>[] constructors = type.getConstructors();
        if (constructors.length == 0)
            throw MissingConstructorException.from("Class '%s' has no constructors".formatted(type.getName()));

        if (ordinal != -1) return getConstructorWithOrdinal(type, ordinal);

        Inject inject = type.getAnnotation(Inject.class);
        if (inject != null) return getConstructorWithOrdinal(type, inject.ordinal());

        return Arrays.stream(constructors)
                .filter(ModifiableInjector::isConstructorValid)
                .filter(constructor -> assertAccess(constructor, null))
                .findFirst()
                .orElseThrow(() -> MissingConstructorException.from("Class '%s' has no valid constructors".formatted(type.getName())));
    }

    private static @NotNull Constructor<?> getConstructorWithOrdinal(@NotNull Class<?> type, @Range(from = 0, to = 65535) int ordinal) {
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

    private static boolean isConstructorValid(@NotNull Constructor<?> constructor) {
        return true;
    }

    private static @NotNull String getConstructorName(@NotNull Constructor<?> constructor) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(constructor.getName()).append('(');

        for (Class<?> parameterType : constructor.getParameterTypes()) {
            stringBuilder.append(parameterType.getName());
            stringBuilder.append(", ");
        }

        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    private static boolean isParameterValid(@NotNull Parameter parameter) {
        Inject inject = parameter.getAnnotation(Inject.class);
        return inject != null;
    }

    private static boolean assertAccess(@NotNull AccessibleObject accessible, @Nullable Object reference) {
        return accessible.canAccess(reference) || accessible.trySetAccessible();
    }

    @Override
    public <T> @NotNull T construct(@NotNull Class<? extends T> cls) {
        return this.construct(cls, ModifiableInjector.UNSPECIFIED_ORDINAL);
    }

    @Override
    public <T> @NotNull T construct(Class<? extends T> cls, @Range(from = -1, to = 65535) int ordinal) {
        Constructor<?> constructor = getConstructorFromClass(cls, ordinal);
        Object[] arguments = this.populateConstructor(constructor);

        try {
            Object constructed = constructor.newInstance(arguments);
            this.inject(constructed);
            return cls.cast(constructed);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw ConstructionException.from("Could not construct class '%s'".formatted(cls.getName()), exception);
        }
    }

    @Override
    public void inject(@NotNull Object object) {
        Class<?> type = object.getClass();

        List<Field> fields = getFieldsFromObject(object);
        fields.forEach(field -> {
            Inject inject = field.getAnnotation(Inject.class);
            Dependency<?> dependency = this.resolveDependency(inject, field.getType());
            if (dependency == null) {
                if (inject.necessity() == Necessity.Required)
                    throw DependencyResolutionException.from("Required dependency '%s' in class '%s' could not be resolved".formatted(field.getName(), type.getName()));
                return;
            }

            try {
                field.set(object, dependency.getObject());
            } catch (IllegalAccessException e) {
                throw InjectException.from("Could not inject value for '%s' into class '%s'".formatted(field.getName(), type.getName()), e);
            }
        });
    }

    @Override
    public @NotNull Injector withDependency(@NotNull Dependency<?> dependency) {
        this.dependencies.add(dependency);
        return this;
    }

    @Override
    public @Nullable Dependency<?> resolveDependency(@NotNull Inject inject, @NotNull Class<?> type) {
        return this.dependencies.stream()
                .filter(dependency -> type.isAssignableFrom(dependency.getType()))
                .filter(dependency -> {
                    if (inject.id().equals("")) return true;

                    return dependency.getId().equals(inject.id());
                })
                .findFirst()
                .orElse(null);
    }

    private @NotNull Object[] populateConstructor(@NotNull Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = Arrays.stream(parameters).map(parameter -> {

            if (!isParameterValid(parameter)) return null;

            Inject inject = parameter.getAnnotation(Inject.class);
            Class<?> type = parameter.getType();

            Dependency<?> dependency = this.resolveDependency(inject, type);

            if (dependency == null) {
                if (inject.necessity() == Necessity.Required)
                    throw DependencyResolutionException.from("Required dependency '%s' in constructor '%s' could not be resolved".formatted(parameter.getName(), getConstructorName(constructor)));
                return null;
            }

            return dependency.getObject();
        }).toArray();

        return arguments;
    }

    @Override
    public @NotNull DependencyRegistry register(@NotNull Dependency<?> dependency) {
        this.dependencyManager.register(dependency);
        return this;
    }

    @Override
    public @NotNull DependencyRegistry registerAll(@NotNull List<Dependency<?>> dependencies) {
        this.dependencyManager.registerAll(dependencies);
        return this;
    }
}
