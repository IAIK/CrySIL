package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class AuthResponse {

	private final String challenge;

	private final String clientData;

	private final String keyHandle;

	private final String signatureData;

	public AuthResponse(byte[] challenge, byte[] clientData, byte[] keyHandle, byte[] signatureData) {
		this.challenge = BaseEncoding.base64Url().encode(challenge);
		this.clientData = BaseEncoding.base64Url().encode(clientData);
		this.keyHandle = BaseEncoding.base64Url().encode(keyHandle);
		this.signatureData = BaseEncoding.base64Url().encode(signatureData);
	}

	public byte[] getChallenge() {
		return BaseEncoding.base64Url().decode(challenge);
	}

	public byte[] getClientData() {
		return BaseEncoding.base64Url().decode(clientData);
	}

	public byte[] getKeyHandle() {
		return BaseEncoding.base64Url().decode(keyHandle);
	}

	public byte[] getSignatureData() {
		return BaseEncoding.base64Url().decode(signatureData);
	}
}
