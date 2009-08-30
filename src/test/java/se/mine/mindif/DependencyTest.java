package se.mine.mindif;

import junit.framework.TestCase;

@SuppressWarnings("unused")
public class DependencyTest extends TestCase {

	/**
	 *  Who is on first base!
	 */
	public final void testNothing() {
		final Dependency.Nothing nothing = new Dependency.Nothing() {
		};
		assertNotNull(nothing);
	}

	public final void testSpecifiedValue() throws Exception {
		final Dependency annotation = BaseSpecified.class.getDeclaredField("name").getAnnotation(Dependency.class);
		assertEquals(StringBuilder.class, annotation.value());
	}

	public final void testUnspecifiedValue() throws Exception {
		final Dependency annotation = Base.class.getDeclaredField("name").getAnnotation(Dependency.class);
		assertEquals(Dependency.Nothing.class, annotation.value());
	}

	static class Base {
		@Dependency
		private String name;
	}

	static class BaseSpecified {
		@Dependency(StringBuilder.class)
		private CharSequence name;
	}

}
