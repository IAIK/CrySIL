/*
 * crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.protocol.payload.crypto;

import org.crysil.protocol.payload.crypto.key.Key;

import java.util.List;

/**
 * Indicates that a payload has some key material that might be relevant for
 * authorizing the command.
 */
public interface PayloadWithKey {

	/**
	 * Gets the key that might be relevant for authorizing the command.
	 *
	 * @return the key
	 */
	public List<Key> getKeys();
}
