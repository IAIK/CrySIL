package org.crysil.communications.u2f;

import java.util.ArrayList;
import java.util.List;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.u2f.data.RegisterContainerResponse;
import org.crysil.communications.u2f.data.RegisterInternalRequest;
import org.crysil.communications.u2f.data.RegisterInternalResponse;
import org.crysil.communications.u2f.data.RegisterMultipleRequest;
import org.crysil.communications.u2f.data.RegisterMultipleResponse;
import org.crysil.logging.Logger;

public class RegisterMultipleHandler implements Handler {

	private final Handler handler;

	public RegisterMultipleHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public String handle(String u2fRawRequest, Module actor, U2FReceiverInterface receiver) {
		RegisterMultipleRequest u2fEnrollHelperRequest = JsonUtils.fromJson(u2fRawRequest,
				RegisterMultipleRequest.class);
		if (u2fEnrollHelperRequest == null) {
			return null;
		}
		Logger.debug("Incoming request: " + JsonUtils.toJson(u2fEnrollHelperRequest));

		// TODO: Handle signData (or do nothing? seems to work anyway)

		List<RegisterMultipleResponse> responseData = new ArrayList<>();
		for (RegisterInternalRequest request : u2fEnrollHelperRequest.getEnrollChallenges()) {
			if (!request.getVersion().equals("U2F_V2"))
				continue;

			String toJson = JsonUtils.toJson(request);
			Logger.debug("Now handling: " + toJson);
			byte[] registrationData = new byte[0];
			int code = 0;
			try {
				String handle = handler.handle(toJson, actor, receiver);
				RegisterInternalResponse registerResponse = JsonUtils.fromJson(handle, RegisterInternalResponse.class);
				if (registerResponse == null) {
					throw new NullPointerException();
				}
				registrationData = registerResponse.getRegistrationData();
			} catch (Exception ex) {
				Logger.error("Can't handle this single enroll challenge, maybe wrong actor?", ex);
				code = 4;
			}

			RegisterMultipleResponse singleResponse = new RegisterMultipleResponse("enroll_helper_reply", code,
					"U2F_V2", registrationData);
			responseData.add(singleResponse);
		}

		RegisterContainerResponse response = new RegisterContainerResponse(responseData);
		Logger.debug("Answering with: " + JsonUtils.toJson(response));
		return JsonUtils.toJson(response);
	}

}
