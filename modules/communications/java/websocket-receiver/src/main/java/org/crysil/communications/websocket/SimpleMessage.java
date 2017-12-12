package org.crysil.communications.websocket;

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
