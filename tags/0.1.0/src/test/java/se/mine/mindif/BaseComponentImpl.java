package se.mine.mindif;

import se.mine.mindif.Dependency;

public class BaseComponentImpl {
	@Dependency
	private AnotherComponentImpl anotherComponent;

	public AnotherComponentImpl getAnotherComponent() {
		return anotherComponent;
	}

}
