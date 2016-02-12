package org.crysil.protocol.payload.status;

import org.crysil.protocol.payload.PayloadResponse;

public class PayloadStatus extends PayloadResponse {

	/** The code. */
	protected int code;

	@Override
	public String getType() {
		return "status";
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code
	 *            the new code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadStatus result = new PayloadStatus();
		result.code = code;

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
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
		PayloadStatus other = (PayloadStatus) obj;
		if (code != other.code)
			return false;
		return true;
	}
}
