package org.crysil.protocol.header;

public abstract class SessionHeader extends Header {

	/**
	 * session id
	 */
	protected String sessionId = null;

	@Override
	public String getType() {
		return "sessionHeader";
	}

	/**
	 * Set the session id.
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Get the session id.
	 */
	public String getSessionId() {
		return sessionId;
	}

}
