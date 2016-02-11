package org.crysil.protocol.payload;

import org.crysil.protocol.PolymorphicStuff;

/**
 * Interface for a variety of different responses.
 */
public abstract class PayloadResponse extends PolymorphicStuff {

	/**
	 * Gets the blanked clone.
	 *
	 * @return the blanked clone
	 */
	public abstract PayloadResponse getBlankedClone();
}
