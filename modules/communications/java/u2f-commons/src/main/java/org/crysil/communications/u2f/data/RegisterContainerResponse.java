package org.crysil.communications.u2f.data;

import java.util.List;

public class RegisterContainerResponse {

	private final List<RegisterMultipleResponse> responses;

	public RegisterContainerResponse(List<RegisterMultipleResponse> responses) {
		this.responses = responses;
	}

	public List<RegisterMultipleResponse> getResponses() {
		return responses;
	}
}
