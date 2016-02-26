package org.crysil.actor.spongycastle;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.crysil.builders.PayloadBuilder;
import org.crysil.commons.Module;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.spongycastle.jce.provider.BouncyCastleProvider;

/**
 * Can handle U2F commands: Generating Key, signing data. Uses spongycastle, a fork of bouncycastle to perform crypto
 * operations using the Android Keystore
 */
public class SpongycastleActor implements Module {

	Map<Class<? extends PayloadRequest>, Command> commands;
	AndroidKeyStore keyStore;

	public SpongycastleActor(String defaultSigningKey) {
		Security.addProvider(new BouncyCastleProvider());
		commands = new HashMap<>();
		commands.put(PayloadGenerateU2FKeyRequest.class, new GenerateU2FKey());
		commands.put(PayloadSignRequest.class, new Sign());
		keyStore = new AndroidKeyStore(defaultSigningKey);
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
			response.setPayload(command.perform(request, keyStore));
		} catch (CrySILException e) {
			response.setPayload(PayloadBuilder.buildStatusResponse(e.getErrorCode()));
		}

		return response;
	}

}
