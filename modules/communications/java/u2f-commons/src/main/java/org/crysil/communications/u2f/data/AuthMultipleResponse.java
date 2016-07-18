package org.crysil.communications.u2f.data;

public class AuthMultipleResponse {

	private final String type;

	private final int code;

	private final AuthMultipleInnerResponse responseData;

	public AuthMultipleResponse(String type, int code, AuthMultipleInnerResponse responseData) {
		this.type = type;
		this.code = code;
		this.responseData = responseData;
	}

	public String getType() {
		return type;
	}

	public int getCode() {
		return code;
	}

	public AuthMultipleInnerResponse getResponseData() {
		return responseData;
	}
}
