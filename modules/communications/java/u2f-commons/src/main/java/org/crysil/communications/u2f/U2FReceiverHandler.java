package org.crysil.communications.u2f;

import java.util.ArrayList;
import java.util.List;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.header.U2FHeader;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;
import org.crysil.u2f.U2FCounterStore;

public class U2FReceiverHandler {

	private final CrySILForwarder crysilForwarder;
	private final U2FCounterStore counterStore;
	private final List<String> counterMap = new ArrayList<>();

	public U2FReceiverHandler(U2FCounterStore counterStore) {
		this.crysilForwarder = new CrySILForwarder();
		this.counterStore = counterStore;
	}

	public String handleMessage(String msg, Module actor, U2FReceiverInterface receiver) {
		if (!msg.contains("U2F_V2")) {
			// insert U2F counter if needed
			Request request = JsonUtils.fromJson(msg, Request.class);
			boolean replaceCounter = false;
			if (request.getPayload() instanceof PayloadSignRequest && request.getHeader() instanceof U2FHeader) {
				replaceCounter = true;
				request.setHeader(cloneHeaderWithCounter((StandardHeader) request.getHeader(), true));
			}
			if (request.getHeader() instanceof StandardHeader
					&& counterMap.contains(((StandardHeader) request.getHeader()).getCommandId())) {
				replaceCounter = true;
			}
			Response response = receiver.forwardRequest(request, actor);
			if (response == null) {
				return null;
			}

			if (response.getPayload() instanceof PayloadAuthResponse && replaceCounter
					&& response.getHeader() instanceof StandardHeader) {
				StandardHeader standardHeader = (StandardHeader) response.getHeader();
				counterMap.add(standardHeader.getCommandId());
			}
			if (response.getPayload() instanceof PayloadSignResponse && replaceCounter
					&& response.getHeader() instanceof StandardHeader) {
				StandardHeader standardHeader = (StandardHeader) response.getHeader();
				if (counterMap.contains(standardHeader.getCommandId())) {
					counterMap.remove(standardHeader.getCommandId());
				}
				response.setHeader(cloneHeaderWithCounter(standardHeader, false));
			}
			return JsonUtils.toJson(response);
		}
		if (msg.contains("helper_request")) {
			if (msg.contains("sign_helper_request")) {
				if (msg.contains("challengeHash")) {
					return new AuthenticateMultipleHandler(new AuthenticateInternalHandler(crysilForwarder,
							counterStore)).handle(msg, actor, receiver);
				} else {
					return new AuthenticateMultipleHandler(new AuthenticateExternalHandler(
							new AuthenticateInternalHandler(crysilForwarder, counterStore))).handle(msg, actor,
							receiver);
				}
			} else {
				if (msg.contains("challengeHash")) {
					return new RegisterMultipleHandler(new RegisterInternalHandler(crysilForwarder)).handle(msg, actor,
							receiver);
				} else {
					return new RegisterMultipleHandler(new RegisterExternalHandler(new RegisterInternalHandler(
							crysilForwarder))).handle(msg, actor, receiver);
				}
			}
		}
		if (msg.contains("challengeHash")) {
			if (msg.contains("keyHandle")) {
				return new AuthenticateInternalHandler(crysilForwarder, counterStore).handle(msg, actor, receiver);
			} else {
				return new RegisterInternalHandler(crysilForwarder).handle(msg, actor, receiver);
			}
		} else {
			if (msg.contains("keyHandle")) {
				return new AuthenticateExternalHandler(new AuthenticateInternalHandler(crysilForwarder, counterStore))
						.handle(msg, actor, receiver);
			} else {
				return new RegisterExternalHandler(new RegisterInternalHandler(crysilForwarder)).handle(msg, actor,
						receiver);
			}
		}
	}

	private U2FHeader cloneHeaderWithCounter(StandardHeader header, boolean increment) {
		U2FHeader result = new U2FHeader();
		result.setCommandId(header.getCommandId());
		result.setCounter(increment ? counterStore.incrementCounter() : counterStore.getCounter());
		result.setPath(header.getPath());
		result.setProtocolVersion(header.getProtocolVersion());
		result.setSessionId(header.getSessionId());
		return result;
	}
}
