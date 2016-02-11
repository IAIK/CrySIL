package org.crysil.protocol.payload;

import org.crysil.protocol.PolymorphicStuff;

/**
 * Interface for a variety of different requests.
 */
public abstract class PayloadRequest extends PolymorphicStuff {

	/**
	 * Gets the blanked clone.
	 *
	 * @return the blanked clone
	 */
	public abstract PayloadRequest getBlankedClone();
}
