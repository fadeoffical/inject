package fade.inject;


import java.lang.ref.WeakReference;
import java.util.List;

@FunctionalInterface
public interface DependencyResolver {

    <T> List<WeakReference<? extends T>> resolve(Class<T> dependency);
}
