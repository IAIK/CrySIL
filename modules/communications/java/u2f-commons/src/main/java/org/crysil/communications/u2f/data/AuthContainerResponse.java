package org.crysil.communications.u2f.data;

import java.util.List;

public class AuthContainerResponse {

	private final List<AuthMultipleResponse> responses;

	public AuthContainerResponse(List<AuthMultipleResponse> responses) {
		this.responses = responses;
	}

	public List<AuthMultipleResponse> getResponses() {
		return responses;
	}
}
