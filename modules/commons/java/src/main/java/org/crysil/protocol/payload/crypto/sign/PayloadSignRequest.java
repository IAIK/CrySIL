package org.crysil.protocol.payload.crypto.sign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

public class PayloadSignRequest extends PayloadRequest implements PayloadWithKey {

	/** The signature key. */
	protected Key signatureKey;

	/** The algorithm. */
	protected String algorithm;

	/** The hashes to be signed. */
	protected List<String> hashesToBeSigned = new ArrayList<>();

	@Override
	public String getType() {
		return "signRequest";
	}

	/**
	 * Gets the algorithm.
	 *
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the algorithm.
	 *
	 * @param algorithm
	 *            the new algorithm
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Gets the hashes to be signed.
	 *
	 * @return the hashes to be signed
	 */
	public List<String> getHashesToBeSigned() {
		return hashesToBeSigned;
	}

	/**
	 * Sets the hashes to be signed.
	 *
	 * @param hashesToBeSigned
	 *            the new hashes to be signed
	 */
	public void setHashesToBeSigned(List<String> hashesToBeSigned) {
		this.hashesToBeSigned = hashesToBeSigned;
	}

	/**
	 * Gets the signature key.
	 *
	 * @return the signature key
	 */
	public Key getSignatureKey() {
		return signatureKey;
	}

	/**
	 * Sets the signature key.
	 *
	 * @param signatureKey
	 *            the new signature key
	 */
	public void setSignatureKey(Key signatureKey) {
		this.signatureKey = signatureKey;
	}

	@Override
	public List<Key> getKeys() {
		List<Key> result = new ArrayList<>();
		result.add(getSignatureKey());
		return result;
	}

	@Override
	public PayloadRequest getBlankedClone() {
		PayloadSignRequest result = new PayloadSignRequest();
		result.algorithm = algorithm;
		result.hashesToBeSigned = Logger.isInfoEnabled() ? hashesToBeSigned
				: Arrays.asList(new String[] { hashesToBeSigned.size() + " blinded hashes" });
		result.signatureKey = signatureKey.getBlankedClone();

		return result;
	}
}
