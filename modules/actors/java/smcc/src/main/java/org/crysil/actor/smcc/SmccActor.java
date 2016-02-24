package org.crysil.actor.smcc;

import java.util.HashMap;
import java.util.Map;

import org.crysil.actor.smcc.strategy.U2FKeyHandleStrategy;
import org.crysil.actor.smcc.strategy.YubicoRandomKeyHandleStrategy;
import org.crysil.builders.PayloadBuilder;
import org.crysil.commons.Module;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;

import at.gv.egiz.smcc.pin.gui.PINGUI;

/**
 * Has one static key available and can use this very key to encrypt and decrypt data.
 */
public class SmccActor implements Module {

	Map<Class<? extends PayloadRequest>, Command> commands = new HashMap<>();
	// TODO: Strategy selection!
	U2FKeyHandleStrategy strategy = new YubicoRandomKeyHandleStrategy();
	// TODO: Authentication!
	PINGUI pinGUI = SmccPinConfiguration.getInstance();

	public SmccActor() {
		commands.put(PayloadGenerateU2FKeyRequest.class, new GenerateU2FKey());
		commands.put(PayloadSignRequest.class, new Sign());
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
			response.setPayload(command.perform(request, strategy, pinGUI));
		} catch (CrySILException e) {
			response.setPayload(PayloadBuilder.buildStatusResponse(e.getErrorCode()));
		}

		return response;
	}

}
