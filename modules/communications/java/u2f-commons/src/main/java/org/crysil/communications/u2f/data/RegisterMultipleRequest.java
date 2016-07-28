package org.crysil.communications.u2f.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class RegisterMultipleRequest {

	private final String type;

	private final List<RegisterInternalRequest> enrollChallenges;

	private final List<AuthInternalRequest> signData;

	@JsonCreator
	public RegisterMultipleRequest(String type, List<RegisterInternalRequest> enrollChallenges,
			List<AuthInternalRequest> signData) {
		this.type = type;
		this.enrollChallenges = enrollChallenges;
		this.signData = signData;
	}

	public String getType() {
		return type;
	}

	public List<RegisterInternalRequest> getEnrollChallenges() {
		return enrollChallenges;
	}

	public List<AuthInternalRequest> getSignData() {
		return signData;
	}
}
