# Inject

Inject is a dependency injection and event api, originally written for `suylite`. Tho it is so versatile that it can be
used for more than just that.

Inject is not yet released. The API may change at any moment without any prior warning.

## Examples

Here you can find some simple usage examples. For more, in-depth information, read the docs.

### Injector

First, create an `Injector`.

```java
Injector injector = Injector.builder().build();
```

Then, register at least one dependency resolver. While technically being completely optional, an `Injector` will be
unable to inject anything without at least one resolver. Alternatively, you can also register resolvers via the builder.
This is useful when you want to lock the Injector so no new resolvers can be registered.

```java
injector.registerDependencyResolver((id,type) -> {
        if(type.isAssignableFrom(Dependency.class)) return dependency;
        return null;
});
```

Finally, you can construct objects using the `Injector#construct(Class)` and/or inject into objects
with `Injector#inject(Object)`. Note that the parameters/fields must be annotated with `@Inject` to inject them.


### Events

First you need an event. Create a class and extend `fade.inject.event.Event`:

```java
public class ExampleEvent extends Event {}
```

Then, you need an EventManager. You can instantiate a new one using the builder:

```java
EventManager eventManager = EventManager.builder().build();
```
You will also need a handler, which you need to register to the event manager. There are two ways to specify the event
type of the handler; either via the annotation or via the first parameter of the handler method as seen below:

```java
import jdk.jfr.Event;

public class ExampleHandler {

    @Handler(event = ExampleEvent.class)
    public void handle() {
        // handle the event
    }
    
    @Handler
    public void handle(ExampleEvent event) {
        // handle the event
    }
}
```

Finally, you can invoke the handler methods by using the `EventManager#invoke(Event)` method:

```java
Event event = new ExampleEvent();
eventManager.invoke(event);
```

Handlers can modify the event object itself, so it is possible to supply some values to an event, let handlers modify
them and then use the modified values after event invocation. 
