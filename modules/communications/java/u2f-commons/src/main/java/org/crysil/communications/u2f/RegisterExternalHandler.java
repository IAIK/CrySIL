package org.crysil.communications.u2f;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.u2f.data.ClientData;
import org.crysil.communications.u2f.data.RegisterInternalRequest;
import org.crysil.communications.u2f.data.RegisterInternalResponse;
import org.crysil.communications.u2f.data.RegisterRequest;
import org.crysil.communications.u2f.data.RegisterResponse;
import org.crysil.logging.Logger;

import static org.crysil.communications.u2f.U2FUtil.*;

public class RegisterExternalHandler implements Handler {

	private static final String CLIENT_DATA_REGISTER = "navigator.id.finishEnrollment";

	private final Handler handler;

	public RegisterExternalHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public String handle(String u2fRawRequest, Module actor, U2FReceiverInterface receiver) {
		RegisterRequest u2fRegisterRequest = JsonUtils.fromJson(u2fRawRequest, RegisterRequest.class);
		if (u2fRegisterRequest == null) {
			return null;
		}
		Logger.debug("Incoming request: " + JsonUtils.toJson(u2fRegisterRequest));

		String clientDataString = JsonUtils.toJson(new ClientData(u2fRegisterRequest.getAppId(), u2fRegisterRequest
				.getChallenge(), CLIENT_DATA_REGISTER));
		RegisterInternalRequest u2fInternalRegisterRequest = new RegisterInternalRequest(
				calculateDigest(u2fRegisterRequest.getAppId()), calculateDigest(clientDataString),
				u2fRegisterRequest.getVersion());

		String internalResponseStr = handler.handle(JsonUtils.toJson(u2fInternalRegisterRequest), actor, receiver);
		if (internalResponseStr.contains("authChallengeRequest"))
			return JsonUtils.toJson(internalResponseStr);

		RegisterInternalResponse internalResponse = JsonUtils.fromJson(internalResponseStr,
				RegisterInternalResponse.class);
		if (internalResponse == null) {
			return null;
		}
		RegisterResponse response = new RegisterResponse(clientDataString.getBytes(),
				internalResponse.getRegistrationData());
		Logger.debug("Answering with: " + JsonUtils.toJson(response));

		return JsonUtils.toJson(response);
	}
}
