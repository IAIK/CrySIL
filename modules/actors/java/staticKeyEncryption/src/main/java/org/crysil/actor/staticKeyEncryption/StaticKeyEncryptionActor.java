package org.crysil.actor.staticKeyEncryption;

import org.crysil.UnsupportedRequestException;
import org.crysil.commons.Module;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

/**
 * Has one static key available and can use this very key to encrypt and decrypt data.
 */
public class StaticKeyEncryptionActor implements Module {

	@Override
	public Response take(Request request) throws UnsupportedRequestException {
		// TODO Auto-generated method stub
		return null;
	}

}
