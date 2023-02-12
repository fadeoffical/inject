package fade.inject.impl;

import fade.inject.api.Injector;
import fade.inject.api.InjectorBuilder;
import org.jetbrains.annotations.NotNull;

public final class InjectorBuilderImpl implements InjectorBuilder {

    private InjectorBuilderImpl() {
    }

    public static @NotNull InjectorBuilderImpl create() {
        return new InjectorBuilderImpl();
    }

    @Override
    public @NotNull Injector build() {
        return new InjectorImpl();
    }
}
