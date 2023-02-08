package fade.inject;

import fade.inject.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class InjectorImpl implements Injector {

    private final @NotNull Set<DependencyResolver> resolvers;

    InjectorImpl() {
        this.resolvers = new HashSet<>();
    }

    @Override
    public <T> T construct(@NotNull Class<? extends T> cls) {
        Constructor<?> constructor = this.getConstructorFromClass(cls);
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
        Class<?> cls = object.getClass();

        Field[] fields = cls.getFields();
        if (fields.length == 0) {}


    }

    @Override
    public void registerDependencyResolver(@NotNull DependencyResolver dependencyResolver) {
        this.resolvers.add(dependencyResolver);
    }

    @Override
    public @Nullable Object resolveDependency(@NotNull Class<?> type) {
        return this.resolveDependencies(type).stream().findFirst().orElse(null);
    }

    @Override
    public @NotNull List<?> resolveDependencies(@NotNull Class<?> type) {
        return this.resolvers.stream()
                .map(resolver -> resolver.resolve(type))
                .flatMap(Collection::stream)
                .filter(reference -> !reference.refersTo(null))
                .map(Reference::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private @NotNull Constructor<?> getConstructorFromClass(@NotNull Class<?> cls) {
        Constructor<?>[] constructors = cls.getConstructors();
        if (constructors.length == 0)
            throw MissingConstructorException.from("Class '%s' has no constructors".formatted(cls.getName()));

        Inject inject = cls.getAnnotation(Inject.class);
        if (inject != null) {
            int ordinal = inject.ordinal();
            Constructor<?> constructor = constructors[ordinal];
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
        Object[] arguments = IntStream.rangeClosed(0, parameters.length).mapToObj(value -> null).toArray();

        for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();

            if (!isParameterValid(parameter)) continue;

            Inject inject = parameter.getAnnotation(Inject.class);

            List<?> dependencies = this.resolveDependencies(type);
            int ordinal = inject.ordinal();
            if (dependencies.size() <= ordinal) {
                if (inject.necessity() == Necessity.Required)
                    throw MissingConstructorException.from("Required dependency '%s' in constructor '%s' could not be resolved".formatted(parameter.getName(), getConstructorName(constructor)));

                continue;
            }

            Object dependency = dependencies.get(ordinal);
            arguments[i] = dependency;
        }
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
