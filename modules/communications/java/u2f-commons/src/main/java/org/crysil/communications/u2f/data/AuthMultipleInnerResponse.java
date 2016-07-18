package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class AuthMultipleInnerResponse {

	private final String version;

	private final String challengeHash;

	private final String appIdHash;

	private final String keyHandle;

	private final String signatureData;

	public AuthMultipleInnerResponse(String version, byte[] challengeHash, byte[] appIdHash, byte[] keyHandle,
			byte[] signatureData) {
		this.version = version;
		this.challengeHash = BaseEncoding.base64Url().encode(challengeHash);
		this.appIdHash = BaseEncoding.base64Url().encode(appIdHash);
		this.keyHandle = BaseEncoding.base64Url().encode(keyHandle);
		this.signatureData = BaseEncoding.base64Url().encode(signatureData);
	}

	public String getVersion() {
		return version;
	}

	public byte[] getChallengeHash() {
		return BaseEncoding.base64Url().decode(challengeHash);
	}

	public byte[] getAppIdHash() {
		return BaseEncoding.base64Url().decode(appIdHash);
	}

	public byte[] getKeyHandle() {
		return BaseEncoding.base64Url().decode(keyHandle);
	}

	public byte[] getSignatureData() {
		return BaseEncoding.base64Url().decode(signatureData);
	}
}
