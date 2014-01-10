package at.iaik.skytrust.element.actors.gatekeeper;

import at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationInfo;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.FeatureSet;

/**
 * The Configuration interface provides a GateKeeper implementation the information about required authentication methods and timely constraints.
 */
public interface Configuration {

/**
	 * Gets the authentication info.
	 *
	 * @param features a {@link FeatureSet}
	 * @return an {@link AuthenticationInfo)
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	public AuthenticationInfo getAuthenticationInfo(FeatureSet features) throws AuthenticationFailedException;

	/**
	 * Checks if is user valid.
	 * 
	 * @param identifier
	 *            the identifier
	 * @throws AuthenticationFailedException
	 *             the authentication failed exception
	 */
	public void isUserValid(String identifier) throws AuthenticationFailedException;
}
