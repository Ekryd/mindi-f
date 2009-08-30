package se.mine.mindif;

import java.lang.reflect.Method;

public class LoggerWrapper {
	private Object logger;
	private Method debugMethodWithException;
	private Method debugMethod;

	public LoggerWrapper(final Class<?> classToBeLogged) {
		try {
			final Class<?> loggerFactoryClass = Class.forName("org.slf4j.LoggerFactory");
			logger = loggerFactoryClass.getMethod("getLogger", Class.class).invoke(null, classToBeLogged);
			debugMethodWithException = logger.getClass().getMethod("debug", String.class, Throwable.class);
			debugMethod = logger.getClass().getMethod("debug", String.class);
		} catch (Exception e) {
			System.err.println("Cannot find logger for MinDI F. No logging will be performed");
			logger = null;
			debugMethod = null;
		}
	}

	public void debug(final String msg) {
		if (debugMethod != null) {
			try {
				debugMethod.invoke(logger, msg);
			} catch (Exception e) {
				System.err.println("Cannot send message to logger.");
				System.err.println("Message: " + msg);
			}
		}
	}

	public void debug(final String msg, final Throwable exception) {
		if (debugMethodWithException != null) {
			try {
				debugMethodWithException.invoke(logger, msg, exception);
			} catch (Exception e) {
				System.err.println("Cannot send message to logger.");
				System.err.println("Message: " + msg);
			}
		}
	}
}
