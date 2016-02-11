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

}
