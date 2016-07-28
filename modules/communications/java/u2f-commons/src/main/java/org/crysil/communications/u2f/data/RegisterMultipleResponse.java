package org.crysil.communications.u2f.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.io.BaseEncoding;

public class RegisterMultipleResponse {

	private final String type;

	private final int code;

	private final String version;

	private final String enrollData;

	@JsonCreator
	public RegisterMultipleResponse(String type, int code, String version, byte[] enrollData) {
		this.type = type;
		this.code = code;
		this.version = version;
		this.enrollData = BaseEncoding.base64Url().encode(enrollData);
	}

	public String getType() {
		return type;
	}

	public int getCode() {
		return code;
	}

	public String getVersion() {
		return version;
	}

	public byte[] getEnrollData() {
		return BaseEncoding.base64Url().decode(enrollData);
	}
}
