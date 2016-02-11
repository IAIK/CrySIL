package org.crysil.protocol.payload.crypto.sign;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

public class PayloadSignResponse extends PayloadResponse {

	/** The signed hashes. */
	protected List<String> signedHashes;

	@Override
	public String getType() {
		return "signResponse";
	}

	/**
	 * Gets the signed hashes.
	 *
	 * @return the signed hashes
	 */
	public List<String> getSignedHashes() {
		return signedHashes;
	}

	/**
	 * Sets the signed hashes.
	 *
	 * @param signedHashes
	 *            the new signed hashes
	 */
	public void setSignedHashes(List<String> signedHashes) {
		this.signedHashes = signedHashes;
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadSignResponse result = new PayloadSignResponse();
		List<String> hashes = new ArrayList<>();
		for (String current : signedHashes)
			hashes.add(Logger.isDebugEnabled() ? current : "*****");

		result.signedHashes = hashes;

		return result;
	}
}
