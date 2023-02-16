package fade.inject;

import fade.inject.dependency.Dependency;
import fade.inject.dependency.DependencyManager;
import fade.inject.dependency.DependencyRegistry;
import fade.inject.exception.DependencyResolutionException;
import fade.inject.exception.InjectException;
import fade.inject.exception.MissingConstructorException;
import fade.inject.internal.Util;
import fade.mirror.MConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static fade.mirror.Mirror.mirror;

public final class ModifiableInjector implements Injector, DependencyRegistry {

    private final DependencyManager dependencyManager;

    ModifiableInjector(@NotNull List<Dependency<?>> dependencies) {
        this.dependencyManager = DependencyManager.builder().withDependencies(dependencies).create();
    }

    @Override
    public <T> @NotNull T construct(@NotNull Class<T> type) {
        MConstructor<T> constructor = mirror(type).getConstructor()
                .orElseThrow(MissingConstructorException::from)
                .requireAccessible();
        Object[] arguments = this.populateConstructor(constructor);
        T constructed = constructor.invoke(arguments);
        this.inject(constructed);
        return constructed;
    }

    @Override
    public void inject(@NotNull Object object) {
        Class<?> type = object.getClass();

        List<Field> fields = Util.getFieldsFromObject(object);
        fields.forEach(field -> {
            Inject inject = field.getAnnotation(Inject.class);
            Optional<Dependency<?>> dependency = this.getDependencyFromAnnotated(field.getType(), field);
            if (dependency.isEmpty()) {
                if (inject.necessity() == Necessity.Required)
                    throw DependencyResolutionException.from("Required dependency '%s' in class '%s' could not be resolved".formatted(field.getName(), type.getName()));
                return;
            }

            try {
                field.set(object, dependency.get().getObject());
            } catch (IllegalAccessException e) {
                throw InjectException.from("Could not inject value for '%s' into class '%s'".formatted(field.getName(), type.getName()), e);
            }
        });
    }

    private @NotNull Optional<Dependency<?>> getDependencyFromAnnotated(@NotNull Class<?> type, @NotNull AnnotatedElement annotated) {
        if (annotated.isAnnotationPresent(Inject.class)) {
            Inject annotation = annotated.getAnnotation(Inject.class);
            String id = annotation.id();

            return this.dependencyManager.resolve(type, id);
        }
        return this.dependencyManager.resolve(type);
    }

    private @NotNull Object[] populateConstructor(@NotNull MConstructor<?> constructor) {
        //noinspection DataFlowIssue
        return constructor.getParameters().map(parameter -> {
            Optional<Inject> optionalInject = parameter.getAnnotationOfType(Inject.class);
            if (optionalInject.isEmpty()) return Optional.empty();

            Inject inject = optionalInject.get();

            Class<?> type = parameter.getType();
            Optional<Dependency<?>> dependency;
            if (!inject.id().equals("")) dependency = this.dependencyManager.resolve(type, inject.id());
            else dependency = this.dependencyManager.resolve(type);


            if (dependency.isEmpty()) {
                if (inject.necessity() == Necessity.Required)
                    throw DependencyResolutionException.from("Required dependency '%s' in constructor '%s' could not be resolved"/*.formatted(parameter.getName(), Util.getConstructorName(constructor))*/);
                return Optional.empty();
            }

            return Optional.ofNullable(dependency.get().getObject());
        }).map(dependency -> dependency.orElse(null)).toArray();
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
