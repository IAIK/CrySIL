package org.crysil.actor.staticKeyEncryption;

import java.util.HashMap;
import java.util.Map;

import org.crysil.commons.Module;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;

/**
 * Has one static key available and can use this very key to encrypt and decrypt data.
 */
public class StaticKeyEncryptionActor implements Module {
	private Map<String, Command> commands = new HashMap<>();

	public StaticKeyEncryptionActor() {
		commands.put(PayloadDiscoverKeysRequest.class.getName(), new DiscoverKeys());
		commands.put(PayloadEncryptRequest.class.getName(), new Encrypt());
		commands.put(PayloadDecryptRequest.class.getName(), new Decrypt());
		commands.put(PayloadSignRequest.class.getName(), new Sign());
		commands.put(PayloadGenerateU2FKeyRequest.class.getName(), new GenerateU2FKey());
		commands.put("EncryptCMS", new EncryptCMS());
		commands.put("DecryptCMS", new DecryptCMS());
	}

	@Override
	public Response take(Request request) throws CrySILException {

		// see if we have someone capable of handling the request
		String target = request.getPayload().getClass().getName();
		if (target.equals(PayloadEncryptRequest.class.getName()))
			if (((PayloadEncryptRequest) request.getPayload()).getAlgorithm().contains("CMS"))
				target = "EncryptCMS";
		if (target.equals(PayloadDecryptRequest.class.getName()))
			if (((PayloadDecryptRequest) request.getPayload()).getAlgorithm().contains("CMS"))
				target = "DecryptCMS";

		Command command = commands.get(target);

		// prepare the response
		Response response = new Response();
		response.setHeader(request.getHeader());

		// if not, do tell
		if (null == command)
			throw new UnsupportedRequestException();

		// let someone else do the actual work
		response.setPayload(command.perform(request));

		return response;
	}

}
