package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class RegisterInternalResponse {

	private final String registrationData;

	public RegisterInternalResponse(byte[] registrationData) {
		this.registrationData = BaseEncoding.base64Url().encode(registrationData);
	}

	public byte[] getRegistrationData() {
		return BaseEncoding.base64Url().decode(registrationData);
	}
}
