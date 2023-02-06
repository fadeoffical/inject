package fade.inject.event;

import org.jetbrains.annotations.NotNull;

public abstract class CancellableEvent extends Event implements Cancellable {

    private @NotNull Result result = Result.Pass;

    @Override
    public @NotNull Result getResult() {
        return this.result;
    }

    @Override
    public void setResult(@NotNull Result result) {
        this.result = result;
    }

    @Override
    public boolean isResult(@NotNull Result result) {
        return this.result == result;
    }

    @Override
    public boolean isCancelled() {
        return this.isResult(Result.Cancel);
    }

    @Override
    public void setCancelled() {
        this.setResult(Result.Cancel);
    }

    @Override
    public boolean isPassing() {
        return this.isResult(Result.Pass);
    }

    @Override
    public void setPassing() {
        this.setResult(Result.Pass);
    }
}
