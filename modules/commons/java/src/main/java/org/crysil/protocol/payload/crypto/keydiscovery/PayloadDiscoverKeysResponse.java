package org.crysil.protocol.payload.crypto.keydiscovery;

import java.util.ArrayList;
import java.util.List;

import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.Key;

/**
 * Holds a list of keys ready for use at a certain CrySIL service.
 */
public class PayloadDiscoverKeysResponse extends PayloadResponse {

	/** The keys. */
	protected List<Key> key = new ArrayList<>();

	@Override
	public String getType() {
		return "discoverKeysResponse";
	}

	/**
	 * Gets the keys.
	 *
	 * @return the keys
	 */
	public List<Key> getKey() {
		return key;
	}

	/**
	 * Sets the keys.
	 *
	 * @param key
	 *            the new keys
	 */
	public void setKey(List<Key> key) {
		clearKeys();

		for (Key current : key)
			addKey(current);
	}

	/**
	 * clear keys
	 */
	public void clearKeys() {
		key.clear();
	}

	/**
	 * add single key to the list
	 * 
	 * @param key
	 */
	public void addKey(Key key) {
		this.key.add(key);
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadDiscoverKeysResponse result = new PayloadDiscoverKeysResponse();
		List<Key> keys = new ArrayList<>();
		for (Key current : key)
			keys.add(current.getBlankedClone());
		result.key = keys;

		return result;
	}
}
