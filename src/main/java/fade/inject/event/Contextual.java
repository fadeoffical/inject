package fade.inject.event;

import org.jetbrains.annotations.Nullable;

public interface Contextual<T> {

    @Nullable T getContext();

    boolean hasContext();
}
