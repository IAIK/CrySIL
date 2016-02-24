package org.crysil.actor.u2f;

import java.util.HashMap;
import java.util.Map;

import org.crysil.actor.u2f.strategy.U2FKeyHandleStrategy;
import org.crysil.actor.u2f.strategy.YubicoRandomKeyHandleStrategy;
import org.crysil.builders.PayloadBuilder;
import org.crysil.commons.Module;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;

/**
 * Uses NFC devices connected to an Android smart phone to perform U2F commands
 */
public class U2FAndroidActor implements Module {

	Map<Class<? extends PayloadRequest>, Command> commands = new HashMap<>();
	// TODO: Strategy selection!
	U2FKeyHandleStrategy strategy = new YubicoRandomKeyHandleStrategy();
	Map<byte[], byte[]> cachedResponses;
	U2FActivityHandler activityHandler;

	public U2FAndroidActor(U2FActivityHandler u2fActivityHandler) {
		commands.put(PayloadGenerateU2FKeyRequest.class, new GenerateU2FKey());
		commands.put(PayloadSignRequest.class, new Sign());
		cachedResponses = new HashMap<byte[], byte[]>();
		activityHandler = u2fActivityHandler;
	}

	@Override
	public Response take(Request request) throws UnsupportedRequestException {
		// see if we have someone capable of handling the request
		Command command = commands.get(request.getPayload().getClass());

		// if not, do tell
		if (null == command)
			throw new UnsupportedRequestException();

		// prepare the response
		Response response = new Response();
		response.setHeader(request.getHeader());

		// let someone else do the actual work
		try {
			response.setPayload(command.perform(request, strategy, cachedResponses, activityHandler));
		} catch (CrySILException e) {
			response.setPayload(PayloadBuilder.buildStatusResponse(e.getErrorCode()));
		}

		return response;
	}

}
