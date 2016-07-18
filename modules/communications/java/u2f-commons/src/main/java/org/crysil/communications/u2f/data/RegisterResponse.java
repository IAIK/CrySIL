package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class RegisterResponse {

	private final String clientData;

	private final String registrationData;

	public RegisterResponse(byte[] clientData, byte[] registrationData) {
		this.clientData = BaseEncoding.base64Url().encode(clientData);
		this.registrationData = BaseEncoding.base64Url().encode(registrationData);
	}

	public byte[] getClientData() {
		return BaseEncoding.base64Url().decode(clientData);
	}

	public byte[] getRegistrationData() {
		return BaseEncoding.base64Url().decode(registrationData);
	}
}
