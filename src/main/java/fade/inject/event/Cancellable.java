package fade.inject.event;

import org.jetbrains.annotations.NotNull;

public interface Cancellable {

    @NotNull Result getResult();

    void setResult(@NotNull Result result);

    boolean isResult(@NotNull Result result);

    boolean isCancelled();

    void setCancelled();

    boolean isPassing();

    void setPassing();

    enum Result {
        Cancel, Pass
    }
}
