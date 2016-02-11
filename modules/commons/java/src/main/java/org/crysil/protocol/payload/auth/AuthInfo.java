package org.crysil.protocol.payload.auth;

import org.crysil.protocol.PolymorphicStuff;

/**
 * An interface for providing data for arbitrary types of authentication.
 */
public abstract class AuthInfo extends PolymorphicStuff {

	/**
	 * Gets the blanked clone.
	 *
	 * @return the blanked clone
	 */
	public abstract AuthInfo getBlankedClone();
}
