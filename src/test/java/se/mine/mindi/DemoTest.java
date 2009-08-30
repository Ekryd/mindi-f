package se.mine.mindi;

import junit.framework.TestCase;

public class DemoTest extends TestCase {

	public void testDemo() {
		// Create base component
		final BaseComponentImpl component = new BaseComponentImpl();
		// Inject dependencies recursively
		new Context().inject(component);

		assertNotNull(component.getAnotherComponent());
	}
}
