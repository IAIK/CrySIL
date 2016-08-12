package org.crysil.actor.staticKeyEncryption;

import java.util.HashMap;
import java.util.Map;

import org.crysil.commons.Module;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decryptCMS.PayloadDecryptCMSRequest;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.encryptCMS.PayloadEncryptCMSRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;

/**
 * Has one static key available and can use this very key to encrypt and decrypt data.
 */
public class StaticKeyEncryptionActor implements Module {
	Map<Class<? extends PayloadRequest>, Command> commands = new HashMap<>();

	public StaticKeyEncryptionActor() {
		commands.put(PayloadDiscoverKeysRequest.class, new DiscoverKeys());
		commands.put(PayloadEncryptRequest.class, new Encrypt());
		commands.put(PayloadDecryptRequest.class, new Decrypt());
		commands.put(PayloadEncryptCMSRequest.class, new EncryptCMS());
		commands.put(PayloadDecryptCMSRequest.class, new DecryptCMS());
	}

	@Override
	public Response take(Request request) throws CrySILException {
		// see if we have someone capable of handling the request
		Command command = commands.get(request.getPayload().getClass());

		// prepare the response
		Response response = new Response();
		response.setHeader(request.getHeader());

		// if not, do tell
		if (null == command)
			throw new UnsupportedRequestException();

		// let someone else do the actual work
		response.setPayload(command.perform(request.getPayload()));

		return response;
	}

}
