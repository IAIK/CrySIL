package org.crysil.actor.spongycastle;

import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;

public abstract class Command {

	protected static final String PROVIDER = "SC";
	protected static final String EC = "EC";
	protected static final String CURVE_NAME = "P-256";
	protected static final String SHA256 = "SHA-256";
	protected static final String SHA256_ECDSA = "SHA256withECDSA";
	protected static final String SHA256_HMAC = "HmacSHA256";
	protected static final String SHA256_RSA = "SHA256WithRSA";
	protected static final String SHA1_PRNG = "SHA1PRNG";
	protected static final String X_509 = "X.509";

	public abstract PayloadResponse perform(Request request, AndroidKeyStore keyStore) throws CrySILException;

}
