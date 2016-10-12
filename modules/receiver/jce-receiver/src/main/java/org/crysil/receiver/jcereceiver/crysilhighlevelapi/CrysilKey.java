/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.crysil.protocol.payload.crypto.key.Key;

/**
 * Abstract class representing a high-level API Crysil key model
 * All other key models are derived from this class.
 * For the compatibility with the Crysil Provider (JCE) this class implements the
 * JCE interfaces {@link java.security.PrivateKey} and {@link java.security.PublicKey}
 */
public abstract class CrysilKey implements PrivateKey, PublicKey {
    
	private static final long serialVersionUID = 3060611901311555044L;

	/**
	 * Gets the internal representation.
	 *
	 * @return the internal representation
	 */
    public abstract Key getInternalRepresentation();

    /* (non-Javadoc)
     * @see java.security.Key#getAlgorithm()
     */
    @Override
    public String getAlgorithm() {
        return "RSA";
    }

    /* (non-Javadoc)
     * @see java.security.Key#getFormat()
     */
    @Override
    public String getFormat() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.Key#getEncoded()
     */
    @Override
    public byte[] getEncoded() {
        return new byte[0];
    }
}
