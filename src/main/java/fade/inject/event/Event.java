package fade.inject.event;

import org.jetbrains.annotations.Nullable;

public interface Event {

    @Nullable Context getContext();

    interface Context {
    }

}
