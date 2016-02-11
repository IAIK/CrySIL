package org.crysil.protocol.payload.crypto.key;

import org.crysil.protocol.PolymorphicStuff;

/**
 * An interface to represent a variety of different key representations.
 */
public abstract class Key extends PolymorphicStuff {

	/**
	 * Gets the blanked clone.
	 *
	 * @return the blanked clone
	 */
	public abstract Key getBlankedClone();
}
