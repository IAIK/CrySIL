package org.crysil.communications.u2f;

import java.util.ArrayList;
import java.util.List;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.u2f.counter.U2FCounterStore;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.header.U2FHeader;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;

public class U2FReceiverHandler {

	private final CrySILForwarder skytrustHandler;
	private final U2FCounterStore counterStore;
	private final List<String> counterMap = new ArrayList<>();

	public U2FReceiverHandler(U2FCounterStore counterStore) {
		this.skytrustHandler = new CrySILForwarder();
		this.counterStore = counterStore;
	}

	public String handleMessage(String msg, Module actor, U2FReceiverInterface receiver) {
		if (!msg.contains("U2F_V2")) {
			Request skyTrustRequest = JsonUtils.fromJson(msg, Request.class);
			boolean isCounter = false;
			if (skyTrustRequest.getPayload() instanceof PayloadSignRequest
					&& skyTrustRequest.getHeader() instanceof U2FHeader) {
				isCounter = true;
				skyTrustRequest.setHeader(cloneHeaderWithCounter((StandardHeader) skyTrustRequest.getHeader(), true));
			}
			if (counterMap.contains(((StandardHeader) skyTrustRequest.getHeader()).getCommandId())) {
				isCounter = true;
			}
			Response skyTrustResponse = receiver.forwardRequest(skyTrustRequest, actor);
			if (skyTrustResponse == null) {
				return null;
			}
			if (skyTrustResponse.getPayload() instanceof PayloadAuthResponse && isCounter
					&& skyTrustResponse.getHeader() instanceof StandardHeader) {
				counterMap.add(((StandardHeader) skyTrustResponse.getHeader()).getCommandId());
			}
			if (skyTrustResponse.getPayload() instanceof PayloadSignResponse && isCounter
					&& skyTrustResponse.getHeader() instanceof StandardHeader) {
				if (counterMap.contains(((StandardHeader) skyTrustResponse.getHeader()).getCommandId())) {
					counterMap.remove(((StandardHeader) skyTrustResponse.getHeader()).getCommandId());
				}
				skyTrustResponse
						.setHeader(cloneHeaderWithCounter((StandardHeader) skyTrustResponse.getHeader(), false));
			}
			String skyTrustRawResponse = JsonUtils.toJson(skyTrustResponse);
			return skyTrustRawResponse;
		}
		if (msg.contains("helper_request")) {
			if (msg.contains("sign_helper_request")) {
				if (msg.contains("challengeHash")) {
					return new AuthenticateMultipleHandler(new AuthenticateInternalHandler(skytrustHandler,
							counterStore)).handle(msg, actor, receiver);
				} else {
					return new AuthenticateMultipleHandler(new AuthenticateExternalHandler(
							new AuthenticateInternalHandler(skytrustHandler, counterStore))).handle(msg, actor,
							receiver);
				}
			} else {
				if (msg.contains("challengeHash")) {
					return new RegisterMultipleHandler(new RegisterInternalHandler(skytrustHandler)).handle(msg, actor,
							receiver);
				} else {
					return new RegisterMultipleHandler(new RegisterExternalHandler(new RegisterInternalHandler(
							skytrustHandler))).handle(msg, actor, receiver);
				}
			}
		}
		if (msg.contains("challengeHash")) {
			if (msg.contains("keyHandle")) {
				return new AuthenticateInternalHandler(skytrustHandler, counterStore).handle(msg, actor, receiver);
			} else {
				return new RegisterInternalHandler(skytrustHandler).handle(msg, actor, receiver);
			}
		} else {
			if (msg.contains("keyHandle")) {
				return new AuthenticateExternalHandler(new AuthenticateInternalHandler(skytrustHandler, counterStore))
						.handle(msg, actor, receiver);
			} else {
				return new RegisterExternalHandler(new RegisterInternalHandler(skytrustHandler)).handle(msg, actor,
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
