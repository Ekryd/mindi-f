package se.mine.mindif;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

public class ContextTest extends TestCase {

	public void testInstantiateExplicitClass() {
		final BaseComponentWithExplicit base = new BaseComponentWithExplicit();
		new Context().inject(base);
		assertNotNull(base.testComponent);
		assertEquals(TestComponentImpl.class, base.testComponent.getClass());
	}

	public void testInstantiateInterface() {
		final BaseComponentWithUnspec base = new BaseComponentWithUnspec();
		new Context().inject(base);
		assertNotNull(base.testComponent);
		assertEquals(TestComponentImpl.class, base.testComponent.getClass());
	}

	public void testInstantiateKlass() {
		final BaseComponentWithImpl base = new BaseComponentWithImpl();
		new Context().inject(base);
		assertNotNull(base.testComponent);
		assertEquals(TestComponentImpl.class, base.testComponent.getClass());
	}

	public void testInstantiateMapInterface() {
		final BaseComponentWithMap base = new BaseComponentWithMap();
		new Context().inject(base);
		assertNotNull(base.map);
		assertEquals(TreeMap.class, base.map.getClass());
	}

	public void testInstantiateMultiImplExplicitClass() {
		final BaseComponentWithMultiImplExplicit base = new BaseComponentWithMultiImplExplicit();
		new Context().inject(base);
		assertNotNull(base.testComponent);
		assertEquals(TestComponentImpl2.class, base.testComponent.getClass());
	}

	public void testRecusiveInstantiation() {
		final SuperComponentImpl base = new SuperComponentImpl();
		new Context().inject(base);
		assertNotNull(base.testComponent);
		assertEquals(BaseComponentWithMultiImplExplicit.class, base.testComponent.getClass());
		assertNotNull(base.testComponent2);
		assertEquals(BaseComponentWithImpl.class, base.testComponent2.getClass());
		assertNotNull(base.testComponent3);
		assertEquals(BaseComponentWithUnspec.class, base.testComponent3.getClass());
		assertNull(base.noDependency);
	}

	public void testSingleton() {
		final SuperComponentImpl base = new SuperComponentImpl();
		new Context().inject(base);
		assertNotNull(base.testComponent);
		assertSame(base.testComponent, base.testComponent_2);
		assertNotNull(base.testComponent2);
		assertSame(base.testComponent2, base.testComponent2_2);
		assertNotNull(base.testComponent3);
		assertSame(base.testComponent3, base.testComponent3_2);
	}

	static interface BaseComponentIF {
		Object getComponent();
	}

	static interface BaseComponentIF2 {
		Object getComponent();
	}

	static class BaseComponentWithExplicit {
		@Dependency(TestComponentImpl.class)
		private TestComponent testComponent;

		public Object getComponent() {
			return testComponent;
		}
	}

	static class BaseComponentWithImpl implements BaseComponentIF {
		@Dependency
		private TestComponentImpl testComponent;

		public Object getComponent() {
			return testComponent;
		}
	}

	static class BaseComponentWithMap {
		@Dependency(TreeMap.class)
		private SortedMap<String, String> map;

		public SortedMap<String, String> getMap() {
			return map;
		}
	}

	static class BaseComponentWithMultiImplExplicit implements BaseComponentIF {
		@Dependency(TestComponentImpl2.class)
		private TestComponent2 testComponent;

		public Object getComponent() {
			return testComponent;
		}
	}

	static class BaseComponentWithUnspec implements BaseComponentIF2 {
		@Dependency
		private TestComponent testComponent;

		public Object getComponent() {
			return testComponent;
		}
	}

	static class SuperComponentImpl {
		@Dependency(BaseComponentWithMultiImplExplicit.class)
		private BaseComponentIF testComponent;

		@Dependency
		private BaseComponentWithImpl testComponent2;

		@Dependency
		private BaseComponentIF2 testComponent3;

		@Dependency(BaseComponentWithMultiImplExplicit.class)
		private BaseComponentIF testComponent_2;

		@Dependency
		private BaseComponentWithImpl testComponent2_2;

		@Dependency
		private BaseComponentIF2 testComponent3_2;

		private TestComponent2 noDependency;
	}

	static interface TestComponent {
	}

	static interface TestComponent2 {

	}

	static class TestComponentImpl implements TestComponent, TestComponent2 {
	}

	static class TestComponentImpl2 implements TestComponent2 {
	}

}
