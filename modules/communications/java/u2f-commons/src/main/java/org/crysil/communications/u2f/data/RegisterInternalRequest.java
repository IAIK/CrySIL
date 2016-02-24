package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class RegisterInternalRequest {

	private final String appIdHash;

	private final String challengeHash;

	private final String version;

	public RegisterInternalRequest(byte[] appIdHash, byte[] challengeHash, String version) {
		this.appIdHash = BaseEncoding.base64Url().encode(appIdHash);
		this.challengeHash = BaseEncoding.base64Url().encode(challengeHash);
		this.version = version;
	}

	public byte[] getAppIdHash() {
		return BaseEncoding.base64Url().decode(appIdHash);
	}

	public byte[] getChallengeHash() {
		return BaseEncoding.base64Url().decode(challengeHash);
	}

	public String getVersion() {
		return version;
	}
}
