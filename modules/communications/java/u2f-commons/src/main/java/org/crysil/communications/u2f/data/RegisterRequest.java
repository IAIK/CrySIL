package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class RegisterRequest {

	private final String appId;

	private final String challenge;

	private final String version;

	public RegisterRequest(String appId, byte[] challenge, String version) {
		this.appId = appId;
		this.challenge = BaseEncoding.base64Url().encode(challenge);
		this.version = version;
	}

	public String getAppId() {
		return appId;
	}

	public byte[] getChallenge() {
		return BaseEncoding.base64Url().decode(challenge);
	}

	public String getVersion() {
		return version;
	}
}
