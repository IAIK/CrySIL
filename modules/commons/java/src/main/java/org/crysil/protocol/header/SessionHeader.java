package org.crysil.protocol.header;

public abstract class SessionHeader extends Header {

  private static final long serialVersionUID = 3512833320267450375L;
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
	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Get the session id.
	 */
	public String getSessionId() {
		return sessionId;
	}

}
