package org.crysil.protocol.payload.auth;

import java.util.ArrayList;
import java.util.List;

import org.crysil.protocol.payload.PayloadResponse;

/**
 * Container for issuing authentication requests. Thus, mostly issued by serving side.
 */
public class PayloadAuthResponse extends PayloadResponse {

	/** The requested authentication method. */
	protected List<AuthType> authTypes;

	@Override
	public String getType() {
		return "authChallengeRequest";
	}

	/**
	 * Gets the requested authentication method.
	 *
	 * @return the requested authentication method
	 */
	public List<AuthType> getAuthTypes() {
		return authTypes;
	}

	/**
	 * Sets the requested authentication method.
	 *
	 * @param authTypes
	 *            the requested authentication method
	 */
	public void setAuthTypes(List<AuthType> authTypes) {
		this.authTypes = authTypes;
	}

	/**
	 * Adds the requested authentication method.
	 *
	 * @param authType
	 *            the requested authentication method
	 */
	public void addAuthType(AuthType authType) {
		if (null == authTypes)
			authTypes = new ArrayList<>();

		authTypes.add(authType);
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadAuthResponse result = new PayloadAuthResponse();
		List<AuthType> types = new ArrayList<>();
		for (AuthType current : authTypes)
			types.add(current.getBlankedClone());
		result.authTypes = types;

		return result;
	}
}
