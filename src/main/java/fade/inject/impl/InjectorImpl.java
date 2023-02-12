package fade.inject.impl;

import fade.inject.Inject;
import fade.inject.Injector;
import fade.inject.Necessity;
import fade.inject.dependency.Dependency;
import fade.inject.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class InjectorImpl implements Injector {

    private static final int UNSPECIFIED_ORDINAL = -1;

    private final @NotNull List<Dependency<?>> dependencies;

    InjectorImpl() {
        this.dependencies = new ArrayList<>();
    }

    private static @NotNull List<Field> getFieldsFromObject(@NotNull Object object) {
        Class<?> type = object.getClass();

        List<Field> objectFields = new ArrayList<>(List.of(type.getDeclaredFields()));
        Class<?> superClass = type;
        while (superClass.getSuperclass() != null) {
            superClass = superClass.getSuperclass();
            objectFields.addAll(List.of(superClass.getDeclaredFields()));
        }

        return objectFields.stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.canAccess(object) || field.trySetAccessible())
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

    // todo: fix the spaghetti
    private static @NotNull Constructor<?> getConstructorFromClass(@NotNull Class<?> cls, int ordinal) {
        Constructor<?>[] constructors = cls.getConstructors();
        if (constructors.length == 0)
            throw MissingConstructorException.from("Class '%s' has no constructors".formatted(cls.getName()));

        if (ordinal != -1) { // todo: proper bounds check
            Constructor<?> constructor = constructors[ordinal];
            if (!isConstructorValid(constructor))
                throw InvalidConstructorException.from("Constructor '%s' in class '%s' is not a valid injectable constructor".formatted(getConstructorName(constructor), cls.getName()));

            if (!constructor.canAccess(null)) constructor.trySetAccessible();

            return constructor;
        }

        Inject inject = cls.getAnnotation(Inject.class);
        if (inject != null) {
            int injectOrdinal = inject.ordinal();
            Constructor<?> constructor = constructors[injectOrdinal];
            if (!isConstructorValid(constructor))
                throw InvalidConstructorException.from("Constructor '%s' in class '%s' is not a valid injectable constructor".formatted(getConstructorName(constructor), cls.getName()));

            if (!constructor.canAccess(null)) constructor.trySetAccessible();

            return constructor;
        }

        return Arrays.stream(constructors)
                .filter(InjectorImpl::isConstructorValid)
                .filter(constructor -> constructor.canAccess(null) || constructor.trySetAccessible())
                .findFirst()
                .orElseThrow(() -> MissingConstructorException.from("Class '%s' has no valid constructors".formatted(cls.getName())));
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

    @Override
    public <T> @NotNull T construct(@NotNull Class<? extends T> cls) {
        return this.construct(cls, InjectorImpl.UNSPECIFIED_ORDINAL);
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
    public void registerDependency(@NotNull Dependency<?> dependency) {
        this.dependencies.add(dependency);
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
}
