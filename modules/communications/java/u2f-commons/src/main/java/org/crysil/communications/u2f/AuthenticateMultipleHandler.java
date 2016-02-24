package org.crysil.communications.u2f;

import java.util.ArrayList;
import java.util.List;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.u2f.data.AuthContainerResponse;
import org.crysil.communications.u2f.data.AuthInternalRequest;
import org.crysil.communications.u2f.data.AuthInternalResponse;
import org.crysil.communications.u2f.data.AuthMultipleInnerResponse;
import org.crysil.communications.u2f.data.AuthMultipleRequest;
import org.crysil.communications.u2f.data.AuthMultipleResponse;
import org.crysil.logging.Logger;

public class AuthenticateMultipleHandler implements Handler {

	private final Handler handler;

	public AuthenticateMultipleHandler(Handler handler) {
		this.handler = handler;
	}

	public String handle(String u2fRawRequest, Module actor, U2FReceiverInterface receiver) {
		AuthMultipleRequest u2fSignHelperRequest = JsonUtils.fromJson(u2fRawRequest, AuthMultipleRequest.class);
		if (u2fSignHelperRequest == null) {
			return null;
		}
		Logger.debug("Incoming request: " + JsonUtils.toJson(u2fSignHelperRequest));

		List<AuthMultipleResponse> responseData = new ArrayList<>();
		for (AuthInternalRequest request : u2fSignHelperRequest.getSignData()) {
			if (!request.getVersion().equals("U2F_V2"))
				continue;

			Logger.debug("Now handling: " + JsonUtils.toJson(request));
			byte[] signatureData = new byte[0];
			int code = 0;
			try {
				AuthInternalResponse authResponse = JsonUtils.fromJson(
						handler.handle(JsonUtils.toJson(request), actor, receiver), AuthInternalResponse.class);
				if (authResponse == null) {
					return null;
				}
				signatureData = authResponse.getSignatureData();
			} catch (Exception ex) {
				Logger.error("Can't handle this single sign request, maybe wrong actor?", ex);
				code = 4;
			}
			AuthMultipleInnerResponse innerResponse = new AuthMultipleInnerResponse("U2F_V2",
					request.getChallengeHash(), request.getAppIdHash(), request.getKeyHandle(), signatureData);
			AuthMultipleResponse singleResponse = new AuthMultipleResponse("sign_helper_reply", code, innerResponse);
			responseData.add(singleResponse);
		}

		AuthContainerResponse response = new AuthContainerResponse(responseData);
		Logger.debug("Answering with: " + JsonUtils.toJson(response));
		return JsonUtils.toJson(response);
	}

}
