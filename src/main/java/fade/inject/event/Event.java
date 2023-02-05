package fade.inject.event;

import org.jetbrains.annotations.NotNull;

public interface Event {

    @NotNull Context context();

    interface Context {
    }

}
