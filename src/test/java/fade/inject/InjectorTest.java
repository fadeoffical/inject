package fade.inject;

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
            public MockObject() {}
        }

        MockObject mockObject = injector.construct(MockObject.class);
        assertNotNull(mockObject);

    }

    @Test
    @DisplayName("constructor injection of simple object")
    void testConstructorInjectionOfSimpleObject() {
        MockDependency dependency = new MockDependency();

        Injector injector = Injector.builder().build();

        MockObject$testConstructorInjectionOfSimpleObject mockObject = injector.construct(MockObject$testConstructorInjectionOfSimpleObject.class);

        assertNotNull(mockObject);
        assertNotNull(mockObject.getDependency());
        assertEquals("mock", mockObject.getDependency().mockString());

    }

    @Test
    @DisplayName("field injection of simple object")
    void testFieldInjectionOfSimpleObject() {
        MockDependency dependency = new MockDependency();

        Injector injector = Injector.builder().build();

        MockObject$testFieldInjectionOfSimpleObject mockObject = new MockObject$testFieldInjectionOfSimpleObject();
        injector.inject(mockObject);

        assertNotNull(mockObject);
        assertNotNull(mockObject.getDependency());
        assertEquals("mock", mockObject.getDependency().mockString());

    }

    // java has some a bug that prevents us from getting the annotations of constructor parameters in inner classes
    // so these classes down here provide for now until we can, someday hopefully, put these into the methods
    private static class MockObject$testConstructorInjectionOfSimpleObject {

        private final MockDependency dependency;

        @Inject
        public MockObject$testConstructorInjectionOfSimpleObject(@Inject MockDependency dependency) {
            this.dependency = dependency;
        }

        public MockDependency getDependency() {
            return this.dependency;
        }
    }

    private static class MockObject$testFieldInjectionOfSimpleObject {

        @Inject
        private MockDependency dependency;

        public MockDependency getDependency() {
            return this.dependency;
        }
    }
}
