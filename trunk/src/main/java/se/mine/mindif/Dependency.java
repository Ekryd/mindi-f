package se.mine.mindif;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation on class fields that indicates that the field should be
 * injected by the MinDI Framework.
 *
 * @author Bjorn
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface Dependency {

	/**
	 * Defines a concrete klass that should be used for the dependency
	 *
	 * @return
	 */
	Class<?> impl() default Nothing.class;

	/**
	 * Default value for implementation, indicated that the framework should
	 * find the concrete class.
	 *
	 * @author Bjorn
	 *
	 */
	static interface Nothing {
	}
}
