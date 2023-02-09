package fade.inject.event;

import fade.inject.Ignore;
import fade.inject.exception.event.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class ManagerImpl implements Manager {

    private final Map<Class<? extends Event>, Map<Object, List<Method>>> handlers;

    ManagerImpl() {
        this.handlers = new HashMap<>();
    }

    @Override
    public void register(@NotNull Object handler) {
        // the handler is already registered
        if (this.isRegistered(handler.getClass())) return;

        int handlerMethodCount = this.registerHandlerMethodsFromHandlerObject(handler);
        if (handlerMethodCount == 0 && !handler.getClass().isAnnotationPresent(Ignore.class))
            throw PossibleMissingHandlerMethodsException.from("Handler class '%s' has zero (0) valid handler methods. This is most likely unintentional. To suppress this exception, annotate the method with fade.inject.event.@Ignore".formatted(handler.getClass()
                                                                                                                                                                                                                                                           .getSimpleName()));
    }

    private int registerHandlerMethodsFromHandlerObject(@NotNull Object handler) {
        Class<?> handlerClass = handler.getClass();
        if (handlerClass.isAnnotationPresent(Ignore.class)) return 0;

        return Arrays.stream(handlerClass.getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.canAccess(handler))
                .map(method -> this.registerHandlerFromMethod(handler, method))
                .map(methodRegistered -> methodRegistered ? 1 : 0)
                .reduce(0, Integer::sum);
    }

    private boolean registerHandlerFromMethod(@NotNull Object handler, @NotNull Method method) {
        if (method.isAnnotationPresent(Ignore.class)) return true;

        String methodSignature = method.getDeclaringClass().getName() + '#' + method.getName();
        if (!method.isAnnotationPresent(Handler.class)) {
            if (isPotentialHandlerMethod(method))
                throw PossibleMissingAnnotationException.from("Method '%s' has a first parameter that is a subclass of fade.inject.event.Event but is not annotated with fade.inject.event.@Handler. This is most likely unintentional. To suppress this exception, annotate the method with fade.inject.event.@Handler.Ignore".formatted(methodSignature));
            return false;
        }

        try {
            Class<? extends Event> eventType = this.getEventTypeFromMethod(method);
            if (eventType == null) return false; // no annotation
            this.registerHandlerMethod(eventType, handler, method);
            return true;
        } catch (EventException exception) {
            throw EventException.from("Could not get event type from method", exception);
        }
    }

    private static boolean isPotentialHandlerMethod(@NotNull Method method) {
        return method.getParameterCount() != 0 && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    private @Nullable Class<? extends Event> getEventTypeFromMethod(@NotNull Method method) {
        if (!method.isAnnotationPresent(Handler.class)) return null;
        String methodSignature = method.getDeclaringClass().getName() + '#' + method.getName();
        Class<? extends Event> handlerEventClass = method.getAnnotation(Handler.class).event();

        if (handlerEventClass == Event.class) {
            if (method.getParameterCount() == 0)
                throw UnspecifiedEventTypeException.from("Method '%s' is annotated with 'fade.inject.event.@Handler' but has no declared event type; it has no parameters nor a specified event type via the annotation".formatted(methodSignature));

            Class<?> parameterType0 = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(parameterType0))
                throw UnspecifiedEventTypeException.from("Method '%s' is annotated with fade.inject.event.@Handler but its first parameter is not a subclass of fade.inject.event.Event nor is an event type specified in the annotation".formatted(methodSignature));

            return parameterType0.asSubclass(Event.class);
        } else {
            if (method.getParameterCount() == 0) return handlerEventClass;

            Class<?> parameterType0 = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(parameterType0)) return handlerEventClass;

            if (!parameterType0.isAssignableFrom(handlerEventClass))
                throw IncompatibleEventTypesException.from("Method '%s' has conflicting event types: The annotation specifies '%s' and the method signature takes '%s'; the type specified in the annotation must be a the same or a superclass of the type specified in the method signature".formatted(methodSignature, handlerEventClass.getName(), parameterType0.getName()));

            return handlerEventClass;
        }
    }

    private void registerHandlerMethod(@NotNull Class<? extends Event> eventType, @NotNull Object handler, @NotNull Method method) {
        Map<Object, List<Method>> handlerMap = this.handlers.getOrDefault(eventType, new HashMap<>());
        List<Method> handlerMethods = handlerMap.getOrDefault(handler, new ArrayList<>());

        handlerMethods.add(method);
        handlerMap.put(handler, handlerMethods);
        this.handlers.put(eventType, handlerMap);
    }

    @Override
    public void unregister(@NotNull Object object) {
        this.unregister(object.getClass());
    }

    @Override
    public void unregister(@NotNull Class<?> cls) {
        Iterator<Map<Object, List<Method>>> iterator = this.handlers.values().iterator();
        while (iterator.hasNext()) {
            for (Map.Entry<Object, List<Method>> entry : iterator.next().entrySet()) {
                Object handler = entry.getKey();
                if (cls.isAssignableFrom(handler.getClass())) iterator.remove();
            }
        }
    }

    @Override
    public boolean isRegistered(@NotNull Class<?> cls) {
        return this.handlers.values()
                .stream()
                .flatMap(handlers -> handlers.keySet().stream())
                .anyMatch(cls::isInstance);
    }

    @Override
    public @NotNull Optional<Object> getHandler(@NotNull Class<?> cls) {
        return this.handlers.values()
                .stream()
                .flatMap(handlers -> handlers.keySet().stream())
                .filter(cls::isInstance)
                .findFirst();
    }

    // todo: throw more exceptions in error cases
    @Override
    public void invoke(@NotNull Event event) {
        Class<? extends Event> eventType = event.getClass();

        Map<Object, List<Method>> handlerMap = this.handlers.getOrDefault(eventType, new HashMap<>());
        handlerMap.forEach((handler, methods) -> methods.stream().filter(method -> {
            Class<? extends Event> methodEventType = this.getEventTypeFromMethod(method);
            return methodEventType != null && eventType.isAssignableFrom(methodEventType);
        }).sorted(ManagerImpl::sortByPriorityGroup).sorted(ManagerImpl::sortByPriorityOrdinal).forEach(method -> {
            String methodSignature = method.getDeclaringClass().getName() + '#' + method.getName();

            Object[] parameters = Arrays.stream(method.getParameterTypes()).map(parameterType -> { // thankuu darvil <3
                if (event instanceof Contextual<?> contextual) {
                    Object context = contextual.getContext();
                    if (context != null && parameterType.isAssignableFrom(context.getClass())) return context;
                }

                if (parameterType.isAssignableFrom(eventType)) return event;
                return null;
            }).toArray();

            Object[] invocationParameters = Stream.of(parameters).filter(Objects::nonNull).toArray();

            try {
                method.invoke(handler, invocationParameters);
            } catch (IllegalAccessException exception) {
                throw EventInvocationException.from("Could not invoke '%s'; the method is inaccessible".formatted(methodSignature), exception);
            } catch (InvocationTargetException exception) {
                throw EventInvocationException.from("Could not successfully '%s'; the handler threw an uncaught exception".formatted(methodSignature), exception);
            }
        }));
    }

    private static int sortByPriorityGroup(Method lhs, Method rhs) {
        PriorityGroup lhsPriorityGroup = lhs.getAnnotation(Handler.class).priorityGroup();
        PriorityGroup rhsPriorityGroup = rhs.getAnnotation(Handler.class).priorityGroup();
        return Integer.compare(lhsPriorityGroup.ordinal(), rhsPriorityGroup.ordinal());
    }

    private static int sortByPriorityOrdinal(Method lhs, Method rhs) {
        int lhsPriorityOrdinal = lhs.getAnnotation(Handler.class).ordinal();
        int rhsPriorityOrdinal = rhs.getAnnotation(Handler.class).ordinal();
        return Integer.compare(lhsPriorityOrdinal, rhsPriorityOrdinal);
    }
}
