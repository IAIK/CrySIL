package org.crysil.communications.u2f;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.u2f.data.AuthInternalRequest;
import org.crysil.communications.u2f.data.AuthInternalResponse;
import org.crysil.communications.u2f.data.AuthRequest;
import org.crysil.communications.u2f.data.AuthResponse;
import org.crysil.communications.u2f.data.ClientData;
import org.crysil.logging.Logger;

import static org.crysil.communications.u2f.U2FUtil.*;

public class AuthenticateExternalHandler implements Handler {

	private static final String CLIENT_DATA_AUTH = "navigator.id.getAssertion";

	private final Handler handler;

	public AuthenticateExternalHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public String handle(String u2fRawRequest, Module actor, U2FReceiverInterface receiver) {
		AuthRequest u2fAuthenticateRequest = JsonUtils.fromJson(u2fRawRequest, AuthRequest.class);
		if (u2fAuthenticateRequest == null) {
			return null;
		}
		Logger.debug("Incoming request: " + JsonUtils.toJson(u2fAuthenticateRequest));

		String clientDataString = JsonUtils.toJson(new ClientData(u2fAuthenticateRequest.getAppId(),
				u2fAuthenticateRequest.getChallenge(), CLIENT_DATA_AUTH));
		byte[] clientParam = calculateDigest(clientDataString);
		byte[] appParam = calculateDigest(u2fAuthenticateRequest.getAppId());

		AuthInternalRequest u2fInternalAuthRequest = new AuthInternalRequest(appParam, clientParam,
				u2fAuthenticateRequest.getKeyHandle(), u2fAuthenticateRequest.getVersion());

		AuthInternalResponse internalResponse = JsonUtils.fromJson(
				handler.handle(JsonUtils.toJson(u2fInternalAuthRequest), actor, receiver), AuthInternalResponse.class);
		if (internalResponse == null) {
			return null;
		}
		AuthResponse response = new AuthResponse(u2fAuthenticateRequest.getChallenge(), clientDataString.getBytes(),
				u2fAuthenticateRequest.getKeyHandle(), internalResponse.getSignatureData());
		Logger.debug("Answering with: " + JsonUtils.toJson(response));

		return JsonUtils.toJson(response);
	}

}
