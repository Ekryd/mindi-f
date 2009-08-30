package se.mine.mindif;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
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
			List<Class<?>> concreteClasses = getSubClasses(fieldClass);
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
					msg.append("@Dependency(impl = ").append(object.getClass().getSimpleName()).append(".class) \n");
				}
				msg.append(Modifier.toString(field.getModifiers())).append(" ").append(fieldClass.getSimpleName())
						.append(" ").append(field.getName());
				throw new IllegalArgumentException(msg.toString());
			}
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

		/**
		 * Gets the path name from a package name.
		 *
		 * @param packageName the package name
		 *
		 * @return the path name
		 */
		private String getPathName(final String packageName) {
			// Code from JWhich
			// ======
			// Translate the package name into an absolute path
			String path = packageName;
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			path = path.replace('.', '/');
			return path;
		}

		/**
		 * Gets the sub classes for a class.
		 *
		 * @param fieldClass the field class
		 *
		 * @return the sub classes
		 */
		private List<Class<?>> getSubClasses(final Class<?> fieldClass) {
			// Originally taken from
			// http://www.javaworld.com/javaworld/javatips/jw-javatip113.html

			List<Class<?>> returnValue = new ArrayList<Class<?>>();
			final Package[] packages = Package.getPackages();
			for (Package package1 : packages) {
				final String packageName = package1.getName();
				String pathName = getPathName(packageName);

				// Get a File object for the package
				URL url = clazz.getResource(pathName);
				if (url == null) {
					LOGGER.debug("Could not find URL for " + pathName);
					continue;
				}
				File directory = new File(url.getFile());

				if (directory.exists()) {
					// Get the list of the files contained in the package
					String[] files = directory.list();
					for (int i = 0; i < files.length; i++) {

						// we are only interested in .class files
						if (files[i].endsWith(".class")) {
							// removes the .class extension
							String classname = files[i].substring(0, files[i].length() - ".class".length());
							// Try to create an instance of the object
							final String fullClassName = packageName + "." + classname;
							try {
								final Class<?> valueClass = Class.forName(fullClassName);
								if (!valueClass.isInterface() && !valueClass.isEnum()
										&& fieldClass.isAssignableFrom(valueClass)) {
									returnValue.add(valueClass);
								}
							} catch (ClassNotFoundException e) {
								LOGGER.debug("Could not find create instance of " + fullClassName);
							}
						}
					}
				}
			}
			return returnValue;
		}
	}
}
