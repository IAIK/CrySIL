package org.crysil.communications.u2f.data;

import com.google.common.io.BaseEncoding;

public class AuthRequest {

	private final String appId;

	private final String challenge;

	private final String keyHandle;

	private final String version;

	public AuthRequest(String appId, byte[] challenge, byte[] keyHandle, String version) {
		this.appId = appId;
		this.challenge = BaseEncoding.base64Url().encode(challenge);
		this.keyHandle = BaseEncoding.base64Url().encode(keyHandle);
		this.version = version;
	}

	public String getAppId() {
		return appId;
	}

	public byte[] getChallenge() {
		return BaseEncoding.base64Url().decode(challenge);
	}

	public byte[] getKeyHandle() {
		return BaseEncoding.base64Url().decode(keyHandle);
	}

	public String getVersion() {
		return version;
	}
}
