package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class AuthInternalResponse {

	private final String signatureData;

	public AuthInternalResponse(byte[] signatureData) {
		this.signatureData = BaseEncoding.base64Url().encode(signatureData);
	}

	public byte[] getSignatureData() {
		return BaseEncoding.base64Url().decode(signatureData);
	}
}
