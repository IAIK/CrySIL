package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class AuthInternalRequest {

	private final String appIdHash;

	private final String challengeHash;

	private final String keyHandle;

	private final String version;

	public AuthInternalRequest(byte[] appIdHash, byte[] challengeHash, byte[] keyHandle, String version) {
		this.appIdHash = BaseEncoding.base64Url().encode(appIdHash);
		this.challengeHash = BaseEncoding.base64Url().encode(challengeHash);
		this.keyHandle = BaseEncoding.base64Url().encode(keyHandle);
		this.version = version;
	}

	public byte[] getAppIdHash() {
		return BaseEncoding.base64Url().decode(appIdHash);
	}

	public byte[] getChallengeHash() {
		return BaseEncoding.base64Url().decode(challengeHash);
	}

	public byte[] getKeyHandle() {
		return BaseEncoding.base64Url().decode(keyHandle);
	}

	public String getVersion() {
		return version;
	}
}
