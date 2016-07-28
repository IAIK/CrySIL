package org.crysil.instance.util;

import org.crysil.instance.ManagementWebSocketHandler;
import org.crysil.instance.RegistrationWebSocketHandler;

/**
 * Used for communication with CrySIL Android server when it registers
 * 
 * @see RegistrationWebSocketHandler
 * @see ManagementWebSocketHandler
 */
public class SimpleMessage {

	protected String header;
	protected String payload;

	public SimpleMessage() {
		this.header = "";
		this.payload = "";
	}

	public SimpleMessage(String header, String payload) {
		this.header = header;
		this.payload = payload;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}