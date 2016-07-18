package org.crysil.actor.pkcs11;

import org.crysil.actor.pkcs11.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;

public abstract class Command {

	protected static final String ECDSA_ALG = "SHA256withECDSA";
	protected static final String U2F_KEY_ID = "u2fkey3";
	
	public abstract PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, Pkcs11KeyStore keyStore)
			throws CrySILException;

}
