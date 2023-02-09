package fade.inject;

import fade.inject.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class InjectorImpl implements Injector {

    private static final int UNSPECIFIED_ORDINAL = -1;

    private final @NotNull Set<DependencyResolver> resolvers;

    InjectorImpl(@NotNull InjectorBuilder builder) {
        this.resolvers = builder.getResolvers();
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
            List<?> dependencies = this.resolveDependencies(inject, field.getType());
            if (dependencies.isEmpty()) {
                if (inject.necessity() == Necessity.Required)
                    throw DependencyResolutionException.from("Required dependency '%s' in class '%s' could not be resolved".formatted(field.getName(), type.getName()));
                return;
            }

            try {
                field.set(object, dependencies.get(0));
            } catch (IllegalAccessException e) {
                throw InjectException.from("Could not inject value for '%s' into class '%s'".formatted(field.getName(), type.getName()), e);
            }
        });
    }

    @Override
    public void registerDependencyResolver(@NotNull DependencyResolver dependencyResolver) {
        this.resolvers.add(dependencyResolver);
    }

    @Override
    public @Nullable Object resolveDependency(@NotNull Inject annotation, @NotNull Class<?> type) {
        return this.resolveDependencies(annotation, type).stream().findFirst().orElse(null);
    }

    @Override
    public @NotNull List<?> resolveDependencies(@NotNull Inject annotation, @NotNull Class<?> type) {
        return this.resolvers.stream()
                .map(resolver -> resolver.resolve(annotation.id(), type))
                .filter(Objects::nonNull)
                .filter(reference -> !reference.refersTo(null))
                .map(Reference::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private static @NotNull List<Field> getFieldsFromObject(@NotNull Object object) {
        Class<?> type = object.getClass();
        return Arrays.stream(type.getDeclaredFields())
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

    private static @NotNull Constructor<?> getConstructorFromClass(@NotNull Class<?> cls, int ordinal) {
        Constructor<?>[] constructors = cls.getConstructors();
        if (constructors.length == 0)
            throw MissingConstructorException.from("Class '%s' has no constructors".formatted(cls.getName()));

        if (ordinal != -1) {
            Constructor<?> constructor = constructors[ordinal];
            if (!isConstructorValid(constructor))
                throw InvalidConstructorException.from("Constructor '%s' in class '%s' is not a valid injectable constructor".formatted(getConstructorName(constructor), cls.getName()));
            return constructor;
        }

        Inject inject = cls.getAnnotation(Inject.class);
        if (inject != null) {
            int injectOrdinal = inject.ordinal();
            Constructor<?> constructor = constructors[injectOrdinal];
            if (!isConstructorValid(constructor))
                throw InvalidConstructorException.from("Constructor '%s' in class '%s' is not a valid injectable constructor".formatted(getConstructorName(constructor), cls.getName()));
            return constructor;
        }

        return Arrays.stream(constructors)
                .filter(InjectorImpl::isConstructorValid)
                .findFirst()
                .orElseThrow(() -> MissingConstructorException.from("Class '%s' has no valid constructors".formatted(cls.getName())));
    }

    private @NotNull Object[] populateConstructor(@NotNull Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = Arrays.stream(parameters).map(parameter -> {
            Class<?> type = parameter.getType();

            if (!isParameterValid(parameter)) return null;

            Inject inject = parameter.getAnnotation(Inject.class);
            List<?> dependencies = this.resolveDependencies(inject, type);
            if (dependencies.isEmpty()) {
                if (inject.necessity() == Necessity.Required)
                    throw DependencyResolutionException.from("Required dependency '%s' in constructor '%s' could not be resolved".formatted(parameter.getName(), getConstructorName(constructor)));
                return null;
            }

            return dependencies.get(0);
        }).toArray();

        return arguments;
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
}
