package org.crysil.protocol.payload.crypto.sign;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import com.google.common.io.BaseEncoding;

public class PayloadSignResponse extends PayloadResponse {

	/** The signed hashes. */
	protected List<String> signedHashes = new ArrayList<>();

	@Override
	public String getType() {
		return "signResponse";
	}

	/**
	 * Gets the signed hashes.
	 *
	 * @return the signed hashes
	 */
	public List<byte[]> getSignedHashes() {
		List<byte[]> tmp = new ArrayList<>();
		for (String current : signedHashes)
			tmp.add(BaseEncoding.base64().decode(current));
		return tmp;
	}

	/**
	 * Sets the signed hashes.
	 *
	 * @param signedHashes
	 *            the new signed hashes
	 */
	public void setSignedHashes(List<byte[]> signedHashes) {
		clearSignedHashes();

		for (byte[] current : signedHashes)
			addSignedHash(current);
	}

	/**
	 * clear list of signed hashes
	 */
	public void clearSignedHashes() {
		signedHashes.clear();
	}

	/**
	 * add another signed hash to list
	 * 
	 * @param hash
	 */
	public void addSignedHash(byte[] hash) {
		signedHashes.add(BaseEncoding.base64().encode(hash));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((signedHashes == null) ? 0 : signedHashes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayloadSignResponse other = (PayloadSignResponse) obj;
		if (signedHashes == null) {
			if (other.signedHashes != null)
				return false;
		} else if (!signedHashes.equals(other.signedHashes))
			return false;
		return true;
	}
}
