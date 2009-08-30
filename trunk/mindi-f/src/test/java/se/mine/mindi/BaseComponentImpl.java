package se.mine.mindi;

public class BaseComponentImpl {
	@Dependency
	private AnotherComponentImpl anotherComponent;

	public AnotherComponentImpl getAnotherComponent() {
		return anotherComponent;
	}

}
