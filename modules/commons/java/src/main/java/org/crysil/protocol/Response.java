package org.crysil.protocol;

import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.PayloadResponse;

/**
 * The basic response structure. Holds a header and a payload.
 */
public class Response {

	/** The header. */
	protected Header header;

	/** The payload. */
	protected PayloadResponse payload;

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * Sets the header.
	 *
	 * @param header
	 *            the new header
	 */
	public void setHeader(Header header) {
		this.header = header;
	}

	/**
	 * Gets the payload.
	 *
	 * @return the payload
	 */
	public PayloadResponse getPayload() {
		return payload;
	}

	/**
	 * Sets the payload.
	 *
	 * @param payload
	 *            the new payload
	 */
	public void setPayload(PayloadResponse payload) {
		this.payload = payload;
	}

	/**
	 * Gets the blanked clone.
	 *
	 * @return the blanked clone
	 */
	public Response getBlankedClone() {
		Response result = new Response();
		result.header = header.getBlankedClone();
		result.payload = payload.getBlankedClone();

		return result;
	}
}
