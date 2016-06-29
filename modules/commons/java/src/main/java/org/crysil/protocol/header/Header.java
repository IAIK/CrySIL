package org.crysil.protocol.header;

import java.util.ArrayList;
import java.util.List;

import org.crysil.protocol.PolymorphicStuff;

/**
 * The basic header part of the message. Contains the protocol version only.
 */
public abstract class Header extends PolymorphicStuff {

	/** The protocol version. Not static because it would require additional code to serialize/deserialize to JSON.*/
	protected final String protocolVersion = "2.0";
	protected String commandId;
	/** The path. */
	protected List<String> requestPath = new ArrayList<>();
	protected List<String> responsePath = new ArrayList<>();

	public List<String> getResponsePath() {
    return responsePath;
  }

  public void setResponsePath(final List<String> responsePath) {
    this.responsePath = responsePath;
  }

  /**
	 * Gets the protocol version.
	 *
	 * @return the protocol version
	 */
	public String getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * Does not do anything because the protocol version of this implementation is static.
	 *
	 * @param protocolVersion
	 *            the new protocol version
	 */
	public void setProtocolVersion(final String protocolVersion) {
		// the protocol version is static
	}

	/**
	 * Gets an anonymized copy for debugging and logging purposes.
	 *
	 * @return the blanked clone
	 */
	public abstract Header getBlankedClone();

	/**
	 * Gets the command id.
	 *
	 * @return the command id
	 */
	public String getCommandId() {
		return commandId;
	}

	/**
	 * Sets the command id.
	 *
	 * @param commandId
	 *            the new command id
	 */
	public void setCommandId(final String commandId) {
		this.commandId = commandId;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public List<String> getRequestPath() {
		return requestPath;
	}

	/**
	 * Sets the path.
	 *
	 * @param path
	 *            the new path
	 */
	public void setRequestPath(final List<String> path) {
		this.requestPath = path;
	}
}
