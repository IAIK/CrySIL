package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class ClientData {

	private final String origin;

	private final String challenge;

	private final String typ;

	public ClientData(String origin, byte[] challenge, String typ) {
		this.origin = origin;
		this.challenge = BaseEncoding.base64Url().encode(challenge);
		this.typ = typ;
	}

	public String getOrigin() {
		return origin;
	}

	public byte[] getChallenge() {
		return BaseEncoding.base64Url().decode(challenge);
	}

	public String getTyp() {
		return typ;
	}
}
