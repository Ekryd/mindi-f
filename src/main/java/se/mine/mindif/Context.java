package se.mine.mindif;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Injects dependencies into a Java object. Injected instances will be reused as
 * long as they belong to the same Context.
 * Example:
 * <code>
 * public class BaseComponentImpl {
 *   @Dependency
 *   private AnotherComponentImpl anotherComponent;
 *
 * ...
 * }
 * </code>
 * Calling code:
 * <code>
 * // Create base component
 * final BaseComponentImpl component = new BaseComponentImpl();
 * // Inject dependencies recursively
 * new Context().inject(component);
 * </code>
 *
 * The dependency can be an interface with a specified implementing class
 * <code>
 *   @Dependency(AnotherComponentImpl.class)
 *   private AnotherComponent anotherComponent;
 * </code>
 *
 * If there is only one implementing class then the framework can find it
 * <code>
 *   @Dependency
 *   private AnotherComponent anotherComponent;
 * </code>
 *
 * @author Bjorn
 */
public class Context {
	private final ConcurrentHashMap<Class<?>, Object> instancesMap = new ConcurrentHashMap<Class<?>, Object>();

	/**
	 * Instantiates a new dependency util.
	 *
	 * @param component the component
	 */
	public Context() {
	}

	/**
	 * Inject dependencies into the component..
	 *
	 * @param component the component
	 */
	public void inject(final Object component) {
		final DependencyUtil dependencyUtil = new DependencyUtil(component, instancesMap);
		dependencyUtil.inject();
	}

}
