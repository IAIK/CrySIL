package at.iaik.skytrust.element.actors.gatekeeper.authentication;

import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;

/**
 * The abstract authentication plugin. May be implemented to fit various methods.
 */
public abstract class AuthPlugin {

	/** The expected identifier. */
	private String expected;

	/**
	 * Authenticate.
	 * 
	 * @param authRequest
	 * @return the user bean
	 * @throws AuthenticationFailedException
	 */
	public void authenticate(SPayloadAuthRequest authRequest) throws AuthenticationFailedException {
		String identifier = getReceivedIdentifier(authRequest);

		if (!identifier.equals(expected))
			throw new AuthenticationFailedException();
	}

	/**
	 * Generate appropriate auth challenge.
	 * 
	 * @param response
	 * @return the response
	 */
	abstract public SResponse generateAuthChallenge(SResponse response);

	/**
	 * Sets the expected value.
	 * 
	 * @param value
	 *            the new expected value
	 */
	public void setExpectedValue(String value) {
		expected = value;
	}
	
	/**
	 * Gets the received identifier. TODO is workaround (is it?) for user authentication step
	 * 
	 * @param authRequest
	 * @return the received identifier
	 * @throws AuthenticationFailedException
	 */
	abstract public String getReceivedIdentifier(SPayloadAuthRequest authRequest) throws AuthenticationFailedException;

	/**
	 * New instance.
	 * 
	 * @return the auth plugin
	 */
	public abstract AuthPlugin newInstance();
}
