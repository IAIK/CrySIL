package org.crysil.communications.u2f.data;

import java.util.List;

public class AuthMultipleRequest {

	private final String type;

	private final List<AuthInternalRequest> signData;

	public AuthMultipleRequest(String type, List<AuthInternalRequest> signData) {
		this.type = type;
		this.signData = signData;
	}

	public String getType() {
		return type;
	}

	public List<AuthInternalRequest> getSignData() {
		return signData;
	}
}
