package fade.inject;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class InjectorImpl implements Injector {

    private InjectorImpl() {
    }

    public static @NotNull InjectorImpl.Builder builder() {
        return new InjectorImpl.Builder.BuilderImpl();
    }

    @Override
    public <T> T construct(@NotNull Class<? extends T> cls) {
        Constructor<?>[] constructors = cls.getDeclaredConstructors();

        if (constructors.length > 1) {
            // todo: what do we do if there is more than one constructor?
            throw new RuntimeException("More than one constructor");
        }

        T object = null;
        for (Constructor<?> constructor : constructors) {
            // todo: also put in parameters and stuff, if there are any
            // Class<?>[] parameterTypes = constructor.getParameterTypes();

            try {
                object = cls.cast(constructor.newInstance());
                break;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        }

        if (object == null) {
            throw new RuntimeException("Could not construct object");
        }

        return object;
    }

    @Override
    public <T> void inject(T obj) {

    }

    public interface Builder extends fade.inject.builder.Builder<InjectorImpl> {

        final class BuilderImpl implements InjectorImpl.Builder {

            private BuilderImpl() {
            }

            @Override
            public @NotNull InjectorImpl build() {
                return new InjectorImpl();
            }
        }
    }
}
