package org.crysil.actor.pkcs11;

import java.util.HashMap;
import java.util.Map;

import org.crysil.actor.pkcs11.strategy.U2FKeyHandleStrategy;
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
 * Can handle U2F commands: Generating Key, signing data
 */
public class Pkcs11Actor implements Module {

	// TODO PIN configuration
	private static final String CARD_PIN = "123456";
	private static final String LIB_HSM_FILE = "/usr/local/lib/opensc-pkcs11.so";
	
	Map<Class<? extends PayloadRequest>, Command> commands;
	U2FKeyHandleStrategy strategy;
	Pkcs11KeyStore keyStore;

	public Pkcs11Actor(U2FKeyHandleStrategy strategy) {
		commands = new HashMap<>();
		commands.put(PayloadGenerateU2FKeyRequest.class, new GenerateU2FKey());
		commands.put(PayloadSignRequest.class, new Sign());
		keyStore = new Pkcs11KeyStore(LIB_HSM_FILE, CARD_PIN);
		this.strategy = strategy;
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
			response.setPayload(command.perform(request, strategy, keyStore));
		} catch (CrySILException e) {
			response.setPayload(PayloadBuilder.buildStatusResponse(e.getErrorCode()));
		}

		return response;
	}

}
