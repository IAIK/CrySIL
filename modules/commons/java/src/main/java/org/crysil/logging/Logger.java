package org.crysil.logging;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

/**
 * A utility class acting as a facade to the logging subsystem.
 */
public class Logger {

	/** The Constant instances. */
	private static final Map<String, org.slf4j.Logger> instances = new HashMap<>();

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	private static synchronized org.slf4j.Logger getLogger() {
		String className = Thread.currentThread().getStackTrace()[3].getClassName();

		org.slf4j.Logger logger = instances.get(className);
		if (logger != null) {
			return logger;
		}

		logger = LoggerFactory.getLogger(className);
		instances.put(className, logger);

		return logger;
	}

	/**
	 * Trace a message.
	 *
	 * @param message
	 *            The message to trace.
	 * @param arguments
	 *            the arguments
	 */
	public static void trace(String message, Object... arguments) {
		getLogger().trace(message, arguments);
	}

	/**
	 * Log a debug message.
	 *
	 * @param message
	 *            The message to log.
	 * @param arguments
	 *            the arguments
	 */
	public static void debug(String message, Object... arguments) {
		getLogger().debug(message, arguments);
	}

	/**
	 * Log an info message.
	 *
	 * @param message
	 *            The message to log.
	 * @param arguments
	 *            the arguments
	 */
	public static void info(String message, Object... arguments) {
		getLogger().info(message, arguments);
	}

	/**
	 * Log a warning message.
	 *
	 * @param message
	 *            The message to log.
	 * @param arguments
	 *            the arguments
	 */
	public static void warn(String message, Object... arguments) {
		getLogger().warn(message, arguments);
	}

	/**
	 * Log a warning message.
	 *
	 * @param message
	 *            The message to log.
	 * @param t
	 *            An exception that may be the cause of the warning.
	 */
	public static void warn(String message, Throwable t) {
		if (getLogger().isDebugEnabled())
			getLogger().warn(message, t);
		else
			getLogger().warn("{}: {}", message, t.getMessage());
	}

	/**
	 * Log an error message.
	 *
	 * @param message
	 *            The message to log.
	 * @param arguments
	 *            the arguments
	 */
	public static void error(String message, Object... arguments) {
		getLogger().error(message, arguments);
	}

	/**
	 * Log an error message.
	 *
	 * @param message
	 *            The message to log.
	 * @param t
	 *            An exception that may be the cause of the error.
	 */
	public static void error(String message, Throwable t) {
		if (getLogger().isDebugEnabled())
			getLogger().error(message, t);
		else
			getLogger().error("{}: {}", message, t.getMessage());
	}

	/**
	 * Checks if is error enabled.
	 *
	 * @return true, if is error enabled
	 */
	public static boolean isErrorEnabled() {
		return getLogger().isErrorEnabled();
	}

	/**
	 * Checks if is info enabled.
	 *
	 * @return true, if is info enabled
	 */
	public static boolean isInfoEnabled() {
		return getLogger().isInfoEnabled();
	}

	/**
	 * Checks if is debug enabled.
	 *
	 * @return true, if is debug enabled
	 */
	public static boolean isDebugEnabled() {
		return getLogger().isDebugEnabled();
	}

	/**
	 * Checks if is trace enabled.
	 *
	 * @return true, if is trace enabled
	 */
	public static boolean isTraceEnabled() {
		return getLogger().isTraceEnabled();
	}
}
