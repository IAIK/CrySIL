package org.crysil.protocol.payload.auth;

import org.crysil.protocol.PolymorphicStuff;

/**
 * An interface for requesting arbitrary types of authentication.
 */
public abstract class AuthType extends PolymorphicStuff {

	/**
	 * Gets the blanked clone.
	 *
	 * @return the blanked clone
	 */
	public abstract AuthType getBlankedClone();
}
