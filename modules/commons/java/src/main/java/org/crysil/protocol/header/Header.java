package org.crysil.protocol.header;

import org.crysil.protocol.PolymorphicStuff;

/**
 * The basic header part of the message. Contains the protocol version only.
 */
public abstract class Header extends PolymorphicStuff {

	/** The protocol version. Not static because it would require additional code to serialize/deserialize to JSON.*/
	protected final String protocolVersion = "2.0";

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
	public void setProtocolVersion(String protocolVersion) {
		// the protocol version is static
	}

	/**
	 * Gets an anonymized copy for debugging and logging purposes.
	 *
	 * @return the blanked clone
	 */
	public abstract Header getBlankedClone();
}
