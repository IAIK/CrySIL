package org.crysil.protocol.payload.crypto.generatekey;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

/**
 * Request for generating a key following some specifications but return a wrapped (i.e. encrypted) representation of the key.
 * Only the service that has the wrapping key (encrypting key) can use the key itself.
 */
public class PayloadGenerateWrappedKeyRequest extends PayloadRequest implements PayloadWithKey {

	/** The key type. */
	protected String keyType;

	/** The encryption keys. */
	protected List<Key> encryptionKeys;

	/** The signing key. */
	protected Key signingKey;

	/** The certificate subject. */
	protected String certificateSubject;

	/**
	 * Gets the key type.
	 *
	 * @return the key type
	 */
	public String getKeyType() {
		return keyType;
	}

	/**
	 * Sets the key type.
	 *
	 * @param keyType
	 *            the new key type
	 */
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

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
	public void setEncryptionKeys(List<Key> encryptionKeys) {
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
	public void setSigningKey(Key signingKey) {
		this.signingKey = signingKey;
	}

	/**
	 * Gets the certificate subject.
	 *
	 * @return the certificate subject
	 */
	public String getCertificateSubject() {
		return certificateSubject;
	}

	/**
	 * Sets the certificate subject.
	 *
	 * @param certificateSubject
	 *            the new certificate subject
	 */
	public void setCertificateSubject(String certificateSubject) {
		this.certificateSubject = certificateSubject;
	}

	@Override
	public String getType() {
		return "generateWrappedKeyRequest";
	}

	@Override
	public List<Key> getKeys() {
		return getEncryptionKeys();
	}

	@Override
	public PayloadRequest getBlankedClone() {
		PayloadGenerateWrappedKeyRequest result = new PayloadGenerateWrappedKeyRequest();
		result.keyType = Logger.isDebugEnabled() ? keyType : "*****";
		List<Key> keys = new ArrayList<>();
		for (Key current : encryptionKeys)
			keys.add(current.getBlankedClone());
		result.encryptionKeys = keys;
		if (null != signingKey)
			result.signingKey = signingKey.getBlankedClone();
		else
			result.signingKey = null;
		result.certificateSubject = Logger.isDebugEnabled() ? certificateSubject : "*****";

		return result;
	}
}
