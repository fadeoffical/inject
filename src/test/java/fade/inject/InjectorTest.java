package fade.inject;

import fade.inject.dependency.Dependency;
import fade.inject.dependency.SingletonDependency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InjectorTest {

    @Test
    @DisplayName("construction of simple object")
    void testConstructionOfSimpleObject() {
        Injector injector = Injector.create();

        class MockObject {

            @Inject
            public MockObject() {
            }
        }

        MockObject mockObject = injector.construct(MockObject.class);
        assertNotNull(mockObject);

    }

    @Test
    @DisplayName("constructor injection of simple object")
    void testConstructorInjectionOfSimpleObject() {
        MockDependency mock = new MockDependency();

        Injector injector = Injector.builder().build();
        SingletonDependency<MockDependency> dependency = SingletonDependency.ofType(MockDependency.class)
                .andValue(mock);
        injector.registerDependency(dependency);

        MockConstructorObject mockObject = injector.construct(MockConstructorObject.class);

        assertNotNull(mockObject);
        assertNotNull(mockObject.getDependency());
        assertEquals("mock", mockObject.getDependency().mockString());

    }

    @Test
    @DisplayName("field injection of simple object")
    void testFieldInjectionOfSimpleObject() {
        MockDependency mock = new MockDependency();

        Injector injector = Injector.builder().build();
        Dependency<MockDependency> dependency = SingletonDependency.ofType(MockDependency.class).andValue(mock);

        injector.registerDependency(dependency);

        MockFieldObject mockObject = new MockFieldObject();
        injector.inject(mockObject);

        assertNotNull(mockObject);
        assertNotNull(mockObject.getDependency());
        assertEquals("mock", mockObject.getDependency().mockString());

    }

    // java has some a bug that prevents us from getting the annotations of constructor parameters in inner classes
    // so these classes down here provide for now until we can, someday hopefully, put these into the methods
    public static class MockConstructorObject {

        private final MockDependency dependency;

        @Inject
        public MockConstructorObject(@Inject MockDependency dependency) {
            this.dependency = dependency;
        }

        public MockDependency getDependency() {
            return this.dependency;
        }
    }

    public static class MockFieldObject {

        @Inject
        private MockDependency dependency;

        public MockDependency getDependency() {
            return this.dependency;
        }
    }
}
