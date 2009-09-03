package se.mine.mindif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper för a slf4j logger.
 *
 * @author Bjorn
 */
public class LoggerWrapper {

	private Logger logger;
	private static boolean foundLogger;

	static {
		try {
			final Class<?> loggerFactoryClass = Class.forName("org.slf4j.LoggerFactory");
			if (loggerFactoryClass != null) {
				foundLogger = true;
			}
		} catch (Exception e) {
			System.err.println("Cannot find slf4j logger for MinDI F. No logging will be performed");
		}
	}

	/**
	 * Instantiates a new logger wrapper.
	 *
	 * @param classToBeLogged the class to be logged
	 */
	public LoggerWrapper(final Class<?> classToBeLogged) {
		if (foundLogger) {
			logger = LoggerFactory.getLogger(classToBeLogged);
		}
	}

	/**
	 * Add a debug message
	 *
	 * @param msg the msg
	 */
	public void debug(final String msg) {
		if (logger != null) {
			logger.debug(msg);
		}
	}

	/**
	 * Add a debug message
	 *
	 * @param msg the msg
	 * @param exception the exception
	 */
	public void debug(final String msg, final Throwable exception) {
		if (logger != null) {
			logger.debug(msg, exception);
		}
	}

	/**
	 * Add a info message
	 *
	 * @param msg the msg
	 */
	public void info(final String msg) {
		if (logger != null) {
			logger.info(msg);
		}
	}
}
