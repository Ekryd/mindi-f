package se.mine.mindif;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Find subclasses for a class.
 *
 * @author Bjorn
 *
 */
public class SubclassLocator {
	private static final LoggerWrapper LOGGER = new LoggerWrapper(SubclassLocator.class);
	private final Set<String> processedJarFiles = new HashSet<String>();
	private final Class<?> fieldClass;

	/**
	 * @param fieldClass
	 */
	public SubclassLocator(final Class<?> fieldClass) {
		this.fieldClass = fieldClass;
	}

	/**
	 * Gets the sub classes for a class.
	 *
	 * @param fieldClass the field class
	 *
	 * @return the sub classes
	 */
	public List<Class<?>> getSubClasses() {
		// Originally taken from
		// http://www.javaworld.com/javaworld/javatips/jw-javatip113.html

		List<Class<?>> returnValue = new ArrayList<Class<?>>();
		final Package[] packages = Package.getPackages();
		for (Package package1 : packages) {
			final String packageName = package1.getName();
			if (packageName.startsWith("sun.") || packageName.startsWith("javax.") || packageName.startsWith("java.")) {
				continue;
			}
			String pathName = getPathName(packageName);

			// Get a File object for the package
			URL url = fieldClass.getResource(pathName);
			if (url == null) {
				LOGGER.debug("Could not find URL for " + pathName);
				continue;
			}
			String urlString = url.toString();
			if (urlString.startsWith("jar:")) {
				try {
					addClasses(returnValue, (JarURLConnection) url.openConnection());
				} catch (IOException e) {
					LOGGER.debug("Could not open jarfile", e);
				}
			} else if (urlString.startsWith("file:")) {
				addClasses(returnValue, packageName, new File(url.getFile()));
			}
		}
		return returnValue;
	}

	private void addClass(final List<Class<?>> returnValue, final String packageName, final String file) {
		if (file.endsWith(".class")) {
			// removes the .class extension
			String classname = file.substring(0, file.length() - ".class".length());
			// Try to create an instance of the object
			final String fullClassName = packageName.length() != 0 ? packageName + "." + classname : classname;
			try {
				final Class<?> valueClass = Class.forName(fullClassName);
				if (!valueClass.isInterface() && !valueClass.isEnum() && fieldClass.isAssignableFrom(valueClass)) {
					returnValue.add(valueClass);
				}
			} catch (ClassNotFoundException e) {
				LOGGER.debug("Could not find create instance of " + fullClassName);
			} catch (NoClassDefFoundError e) {
				LOGGER.debug("Could not find create instance of " + fullClassName);
			}
		}
	}

	private void addClasses(final List<Class<?>> returnValue, final String packageName, final File directory) {
		if (directory.exists()) {
			LOGGER.debug("Found package directory " + directory);
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				final String file = files[i];
				// we are only interested in .class files
				addClass(returnValue, packageName, file);
			}
		}
	}

	private void addClasses(final List<Class<?>> returnValue, final JarURLConnection connection) throws IOException {
		final JarFile jarFile = connection.getJarFile();
		final String jarFileName = jarFile.getName();
		if (processedJarFiles.contains(jarFileName)) {
			LOGGER.debug("Already processed jarfile " + jarFileName);
			return;
		} else {
			processedJarFiles.add(jarFileName);
		}
		for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
			final String jarEntry = entries.nextElement().toString().replaceAll("/", ".");
			addClass(returnValue, "", jarEntry);
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

}
