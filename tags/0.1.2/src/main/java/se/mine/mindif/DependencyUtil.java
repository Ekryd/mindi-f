package se.mine.mindif;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Injects dependencies into a Java component.
 *
 * @author Bjorn
 */
class DependencyUtil {
	private static final LoggerWrapper LOGGER = new LoggerWrapper(FieldWrapper.class);

	private final ConcurrentHashMap<Class<?>, Object> instancesMap;
	private final Object component;
	private final Class<? extends Object> clazz;

	/**
	 * Instantiates a new dependency util.
	 *
	 * @param component the component
	 * @param instancesMap contins previously injected dependencies, which are reused
	 */
	DependencyUtil(final Object component, final ConcurrentHashMap<Class<?>, Object> instancesMap) {
		this.component = component;
		this.clazz = component.getClass();
		this.instancesMap = instancesMap;
	}

	/**
	 * Inject dependecies into component.
	 */
	void inject() {
		final Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(Dependency.class)) {
				injectDependency(field);
			}
		}
	}

	/**
	 * Inject a dependency into a field.
	 *
	 * @param field the field
	 */
	private void injectDependency(final Field field) {
		FieldWrapper fieldWrapper = new FieldWrapper(field);
		final boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(component, fieldWrapper.createInstance());
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot modify field " + field.getName() + " in class " + clazz.getName(), e);
		}
		field.setAccessible(accessible);
	}

	/**
	 * The Class FieldWrapper adds funktionality to create dependency instances for a field.
	 */
	private final class FieldWrapper {

		private final Field field;
		private final Class<?> fieldClass;
		private final Dependency dependency;

		/**
		 * The Constructs a new FieldWrapper.
		 *
		 * @param field the field
		 */
		private FieldWrapper(final Field field) {
			this.field = field;
			fieldClass = field.getType();
			dependency = field.getAnnotation(Dependency.class);
		}

		/**
		 * Creates an instance for the field.
		 *
		 * @return the object
		 */
		private Object createInstance() {
			Object returnValue;
			final Class<?> implementingClass = dependency.value();
			if (!implementingClass.equals(Dependency.Nothing.class)) {
				if (!fieldClass.isAssignableFrom(implementingClass)) {
					throw new IllegalArgumentException(implementingClass.getName() + " is not a subclass of "
							+ fieldClass.getName());
				}
				returnValue = createObject(implementingClass, false);
			} else if (fieldClass.isInterface()) {
				returnValue = createInterfaceInstance();
			} else {
				returnValue = createObject(fieldClass, false);
			}
			if (returnValue != null) {
				new DependencyUtil(returnValue, instancesMap).inject();
			}
			return returnValue;
		}

		/**
		 * Creates an instance for an unspecified interface field.
		 *
		 * @return the object
		 */
		private Object createInterfaceInstance() {
			List<Class<?>> concreteClasses = new SubclassLocator(fieldClass).getSubClasses();
			List<Object> concreteObject = new ArrayList<Object>();
			for (Class<?> concreteClass : concreteClasses) {
				final Object object = createObject(concreteClass, true);
				if (object != null) {
					concreteObject.add(object);
				}
			}
			if (concreteObject.size() == 0) {
				throw new IllegalArgumentException("Could not find concrete implementation of interface: "
						+ fieldClass.getName());
			}
			if (concreteObject.size() > 1) {
				final StringBuilder msg = new StringBuilder().append(
						"Found more than one implementation of interface: ").append(fieldClass.getName()).append(
						". Please use either of: \n");
				for (Object object : concreteObject) {
					msg.append("@Dependency(").append(object.getClass().getSimpleName()).append(".class) \n");
				}
				msg.append(Modifier.toString(field.getModifiers())).append(" ").append(fieldClass.getSimpleName())
						.append(" ").append(field.getName());
				throw new IllegalArgumentException(msg.toString());
			}
			final StringBuilder msg = new StringBuilder().append("Found instance for interface ").append(
					fieldClass.getName()).append(". But please use \n");
			msg.append("@Dependency(").append(concreteObject.get(0).getClass().getSimpleName()).append(
					".class) to improve type safety and instantiation speed");
			LOGGER.info(msg.toString());
			return concreteObject.get(0);
		}

		/**
		 * Creates the object from a class.
		 *
		 * @param clazz the clazz
		 *
		 * @return new instance or null
		 */
		private Object createObject(final Class<?> clazz, final boolean returnNullOnException) {
			try {
				if (instancesMap.containsKey(clazz)) {
					return instancesMap.get(clazz);
				}
				final Object newInstance = clazz.newInstance();
				instancesMap.put(clazz, newInstance);
				return newInstance;
			} catch (InstantiationException e) {
				if (returnNullOnException) {
					LOGGER.debug("Could not create instance of " + clazz.getName(), e);
					return null;
				} else {
					throw new RuntimeException("Cannot create an instance of " + clazz.getName(), e);
				}
			} catch (IllegalAccessException e) {
				if (returnNullOnException) {
					LOGGER.debug("Could not create instance of " + clazz.getName(), e);
					return null;
				} else {
					throw new RuntimeException("Cannot create an instance of " + clazz.getName(), e);
				}
			}
		}

	}
}
