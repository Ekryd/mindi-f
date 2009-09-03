package se.mine.mindif;

import junit.framework.Protectable;
import junit.framework.TestCase;

@SuppressWarnings("unused")
public class ExternalLibraryTest extends TestCase {

	public void testInstantiateLibraryInterface() {
		final BaseComponentWithLibraryDependency base = new BaseComponentWithLibraryDependency();
		try {
			new Context().inject(base);
			fail();
		} catch (IllegalArgumentException iaex) {
			assertEquals("Could not find concrete implementation of interface: junit.framework.Protectable", iaex
					.getMessage());
		}
	}

	static class BaseComponentWithLibraryDependency {
		@Dependency
		private Protectable testComponent;

	}

}
