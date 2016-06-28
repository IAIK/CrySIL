package org.crysil.protocol.payload.crypto.keydiscovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

/**
 * Lets one get different representations of a single key.
 */
public class PayloadGetKeyRequest extends PayloadRequest implements PayloadWithKey {

	/** The key. */
	protected Key key;

	/** The representation. */
	protected String representation = "";

	@Override
	public String getType() {
		return "getKeyRequest";
	}

	/**
	 * Gets the representation.
	 *
	 * @return the representation
	 */
	public String getRepresentation() {
		return representation;
	}

	/**
	 * Sets the representation.
	 *
	 * @param representation
	 *            the new representation
	 */
	public void setRepresentation(final String representation) {
		this.representation = representation;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key
	 *            the new key
	 */
	public void setKey(final Key key) {
		this.key = key;
	}

	@Override
	public List<Key> getKeys() {
		final List<Key> result = new ArrayList<>();
		result.add(getKey());
		return result;
	}

	@Override
	public PayloadRequest getBlankedClone() {
		final PayloadGetKeyRequest result = new PayloadGetKeyRequest();
		result.representation = Logger.isDebugEnabled() ? representation : "*****";
		result.key = key;
		return result;
	}

	@Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,key,representation});
  }

}
