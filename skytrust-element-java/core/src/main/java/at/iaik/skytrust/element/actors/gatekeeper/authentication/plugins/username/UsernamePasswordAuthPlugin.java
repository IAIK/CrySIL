package at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.username;

import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthType;

/**
 * Implements the username/password authentication method.
 */
public class UsernamePasswordAuthPlugin extends AuthPlugin {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.iaik.skytrust.element.actors.gatekeeper.authentication.IAuthPlugin#generateAuthChallenge(at.iaik.skytrust.element.skytrustprotocol.SResponse
	 * )
	 */
    public SResponse generateAuthChallenge(SResponse response) {
        SUserPasswordAuthType userPasswordAuthType = new SUserPasswordAuthType();
        SPayloadAuthResponse authResponse = new SPayloadAuthResponse();
        authResponse.setAuthType(userPasswordAuthType);
        response.setPayload(authResponse);
        return response;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.iaik.skytrust.element.actors.gatekeeper.authentication.IAuthPlugin#getReceivedIdentifier(at.iaik.skytrust.element.skytrustprotocol.payload
	 * .auth.SPayloadAuthRequest)
	 */
	public String getReceivedIdentifier(SPayloadAuthRequest authRequest) throws AuthenticationFailedException {
		// check authRequest format
		SAuthInfo authInfo = authRequest.getAuthInfo();
		if (!(authInfo instanceof SUserPasswordAuthInfo))
			throw new AuthenticationFailedException();
		SUserPasswordAuthInfo userPasswordAuthInfo = (SUserPasswordAuthInfo) authInfo;

		// FIXME define uid
		return userPasswordAuthInfo.getUserName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin#newInstance()
	 */
	@Override
	public AuthPlugin newInstance() {
		return new UsernamePasswordAuthPlugin();
	}

}
