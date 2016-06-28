package org.crysil.protocol.header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;

/**
 * The most basic header one can use. Contains various fields that popped up during the CrySIL project up until now.
 */
public class StandardHeader extends SessionHeader {

	/** The path. */
	protected List<String> path = new ArrayList<String>();

	@Override
	public String getType() {
		return "standardHeader";
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	@Override
	public List<String> getPath() {
		return path;
	}

	/**
	 * Sets the path.
	 *
	 * @param path
	 *            the new path
	 */
	@Override
	public void setPath(List<String> path) {
		this.path = path;
	}

	@Override
	public Header getBlankedClone() {
		final StandardHeader result = new StandardHeader();
		result.commandId = Logger.isInfoEnabled() ? commandId : "*****";
		result.sessionId = Logger.isDebugEnabled() ? sessionId : "*****";
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { type, commandId, sessionId });
	}
}
