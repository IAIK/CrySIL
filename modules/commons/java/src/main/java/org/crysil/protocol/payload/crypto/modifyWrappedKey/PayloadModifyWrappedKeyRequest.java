package org.crysil.protocol.payload.crypto.modifyWrappedKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

/**
 * This request lets a client ask for modification of an existing wrapped key.
 */
public class PayloadModifyWrappedKeyRequest extends PayloadRequest implements PayloadWithKey {

	/** The encryption keys. */
	protected List<Key> encryptionKeys;

	/** The signing key. */
	protected Key signingKey;

	/** The encoded wrapped key. */
	protected String encodedWrappedKey;

	/** The decryption key. */
	protected Key decryptionKey;

	/**
	 * Gets the encryption keys.
	 *
	 * @return the encryption keys
	 */
	public List<Key> getEncryptionKeys() {
		return encryptionKeys;
	}

	/**
	 * Sets the encryption keys.
	 *
	 * @param encryptionKeys
	 *            the new encryption keys
	 */
	public void setEncryptionKeys(final List<Key> encryptionKeys) {
		this.encryptionKeys = encryptionKeys;
	}

	/**
	 * Gets the signing key.
	 *
	 * @return the signing key
	 */
	public Key getSigningKey() {
		return signingKey;
	}

	/**
	 * Sets the signing key.
	 *
	 * @param signingKey
	 *            the new signing key
	 */
	public void setSigningKey(final Key signingKey) {
		this.signingKey = signingKey;
	}

	/**
	 * Gets the encoded wrapped key.
	 *
	 * @return the encoded wrapped key
	 */
	public String getEncodedWrappedKey() {
		return encodedWrappedKey;
	}

	/**
	 * Sets the encoded wrapped key.
	 *
	 * @param encodedWrappedKey
	 *            the new encoded wrapped key
	 */
	public void setEncodedWrappedKey(final String encodedWrappedKey) {
		this.encodedWrappedKey = encodedWrappedKey;
	}

	/**
	 * Gets the decryption key.
	 *
	 * @return the decryption key
	 */
	public Key getDecryptionKey() {
		return decryptionKey;
	}

	/**
	 * Sets the decryption key.
	 *
	 * @param decryptionKey
	 *            the new decryption key
	 */
	public void setDecryptionKey(final Key decryptionKey) {
		this.decryptionKey = decryptionKey;
	}

	@Override
	public String getType() {
		return "modifyWrappedKeyRequest";
	}

	@Override
	public List<Key> getKeys() {
		return getEncryptionKeys();
	}

	@Override
	public PayloadRequest getBlankedClone() {
		final PayloadModifyWrappedKeyRequest result = new PayloadModifyWrappedKeyRequest();

		final List<Key> keys = new ArrayList<>();
		for (final Key current : encryptionKeys) {
      keys.add(current.getBlankedClone());
    }
		result.encryptionKeys = keys;
		if (null != signingKey) {
      result.signingKey = signingKey.getBlankedClone();
    } else {
      result.signingKey = null;
    }
		result.encodedWrappedKey = Logger.isDebugEnabled() ? encodedWrappedKey : "*****";
		if (null != decryptionKey) {
      result.decryptionKey = decryptionKey.getBlankedClone();
    } else {
      result.decryptionKey = null;
    }

		return result;
	}

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{getType(),decryptionKey,encodedWrappedKey,encryptionKeys,signingKey});
  }
}
