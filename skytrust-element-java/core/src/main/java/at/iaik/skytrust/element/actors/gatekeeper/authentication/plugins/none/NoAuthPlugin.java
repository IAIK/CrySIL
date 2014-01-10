package at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.none;

import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;

/**
 * Implements an authentication not required method.
 */
public class NoAuthPlugin extends AuthPlugin {

	@Override
	public SResponse generateAuthChallenge(SResponse response) {
		// return nothing because we do not need anything from the user
		return null;
	}

	@Override
	public String getReceivedIdentifier(SPayloadAuthRequest authRequest) throws AuthenticationFailedException {
		return "";
	}

	@Override
	public void authenticate(SPayloadAuthRequest authRequest) throws AuthenticationFailedException {
		// everything is valid
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin#newInstance()
	 */
	@Override
	public AuthPlugin newInstance() {
		return new NoAuthPlugin();
	}
}
