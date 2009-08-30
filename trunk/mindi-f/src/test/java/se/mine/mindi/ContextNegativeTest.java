package se.mine.mindi;

import junit.framework.TestCase;

@SuppressWarnings("unused")
public class ContextNegativeTest extends TestCase {

	public void testInstantiateIllegalField() {
		final BaseComponentWithIllegalField base = new BaseComponentWithIllegalField();
		try {
			new Context().inject(base);
			fail();
		} catch (RuntimeException rex) {
			assertEquals("Cannot modify field testComponent in class "
					+ "se.mine.mindi.ContextNegativeTest$BaseComponentWithIllegalField", rex.getMessage());
		}
	}

	public void testInstantiateIllegalImpl() {
		final BaseComponentWithIllegalImpl base = new BaseComponentWithIllegalImpl();
		try {
			new Context().inject(base);
			fail();
		} catch (RuntimeException rex) {
			assertEquals("Cannot create an instance of " + "se.mine.mindi.ContextNegativeTest$TestComponentImpl3", rex
					.getMessage());
		}
	}

	public void testInstantiateIllegalImpl2() {
		final BaseComponentWithIllegalImpl2 base = new BaseComponentWithIllegalImpl2();
		try {
			new Context().inject(base);
			fail();
		} catch (RuntimeException rex) {
			assertEquals("Cannot create an instance of " + "se.mine.mindi.ContextNegativeTest$TestComponentImpl3", rex
					.getMessage());
		}
	}

	public void testInstantiateMultiImpl() {
		final BaseComponentWithMultiImpl base = new BaseComponentWithMultiImpl();
		try {
			new Context().inject(base);
			fail();
		} catch (IllegalArgumentException iaex) {
			assertEquals("Found more than one implementation of interface: "
					+ "se.mine.mindi.ContextNegativeTest$TestComponent2" + ". Please use either of: \n"
					+ "@Dependency(impl = TestComponentImpl.class) \n"
					+ "@Dependency(impl = TestComponentImpl2.class) \n" + "private TestComponent2 testComponent", iaex
					.getMessage());
		}
	}

	public void testInstantiateNoImpl() {
		final BaseComponentWithNoImpl base = new BaseComponentWithNoImpl();
		try {
			new Context().inject(base);
			fail();
		} catch (IllegalArgumentException iaex) {
			assertEquals("Could not find concrete implementation of interface: "
					+ "se.mine.mindi.ContextNegativeTest$TestComponent", iaex.getMessage());
		}
	}

	public void testInstantiateWrongImpl() {
		final BaseComponentWithWrongImpl base = new BaseComponentWithWrongImpl();
		try {
			new Context().inject(base);
			fail();
		} catch (IllegalArgumentException iaex) {
			assertEquals("se.mine.mindi.ContextNegativeTest$TestComponentImpl " + "is not a subclass of "
					+ "se.mine.mindi.ContextNegativeTest$TestComponent", iaex.getMessage());
		}
	}

	static class BaseComponentWithIllegalField {
		@Dependency(impl = TestComponentImpl.class)
		private final static TestComponent2 testComponent = null;

	}

	static class BaseComponentWithIllegalImpl {
		@Dependency(impl = TestComponentImpl3.class)
		private TestComponent testComponent;

	}

	static class BaseComponentWithIllegalImpl2 {
		@Dependency
		private TestComponentImpl3 testComponent;

	}

	static class BaseComponentWithMultiImpl {
		@Dependency
		private TestComponent2 testComponent;

	}

	static class BaseComponentWithNoImpl {
		@Dependency
		private TestComponent testComponent;

	}

	static class BaseComponentWithWrongImpl {
		@Dependency(impl = TestComponentImpl.class)
		private TestComponent testComponent;

	}

	static interface TestComponent {

	}

	static interface TestComponent2 {

	}

	static class TestComponentImpl implements TestComponent2 {
	}

	static class TestComponentImpl2 implements TestComponent2 {

	}

	static abstract class TestComponentImpl3 implements TestComponent {
	}

}
