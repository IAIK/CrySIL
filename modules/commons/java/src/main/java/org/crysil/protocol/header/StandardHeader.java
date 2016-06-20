package org.crysil.protocol.header;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;

/**
 * The most basic header one can use. Contains various fields that popped up during the CrySIL project up until now.
 */
public class StandardHeader extends SessionHeader {

	/** The command id. */
	protected String commandId = "";

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

	/**
	 * Gets the command id.
	 *
	 * @return the command id
	 */
	@Override
	public String getCommandId() {
		return commandId;
	}

	/**
	 * Sets the command id.
	 *
	 * @param commandId
	 *            the new command id
	 */
	@Override
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	@Override
	public Header getBlankedClone() {
		StandardHeader result = new StandardHeader();
		result.commandId = Logger.isInfoEnabled() ? commandId : "*****";
		result.sessionId = Logger.isDebugEnabled() ? sessionId : "*****";
		return result;
	}
}
