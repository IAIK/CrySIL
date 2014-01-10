package at.iaik.skytrust.element.actors.gatekeeper;

import at.iaik.skytrust.element.skytrustprotocol.SResponse;

public class AuthenticationRequiredException extends Exception {
	private static final long serialVersionUID = -3870991674993300867L;
	private SResponse response;

	public AuthenticationRequiredException(SResponse response) {
		this.response = response;
	}

	public SResponse getResponse() {
		return response;
	}
}
