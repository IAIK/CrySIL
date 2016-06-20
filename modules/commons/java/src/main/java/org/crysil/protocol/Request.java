package org.crysil.protocol;

import java.io.Serializable;

import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.PayloadRequest;

/**
 * The basic request structure. Hold a header and a payload.
 */
public class Request implements Serializable{

	/** The header. */
	protected Header header;

	/** The payload. */
	protected PayloadRequest payload;

	public Request() {
	}

	public Request(final Header header, final PayloadRequest payload) {
		this.header = header;
		this.payload = payload;
	}

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
	public void setHeader(final Header header) {
		this.header = header;
	}

	/**
	 * Gets the payload.
	 *
	 * @return the payload
	 */
	public PayloadRequest getPayload() {
		return payload;
	}

	/**
	 * Sets the payload.
	 *
	 * @param payload
	 *            the new payload
	 */
	public void setPayload(final PayloadRequest payload) {
		this.payload = payload;
	}

	/**
	 * Gets the blanked clone.
	 *
	 * @return the blanked clone
	 */
	public Request getBlankedClone() {
		final Request result = new Request();
		result.header = header.getBlankedClone();
		result.payload = payload.getBlankedClone();
		return result;
	}
}
