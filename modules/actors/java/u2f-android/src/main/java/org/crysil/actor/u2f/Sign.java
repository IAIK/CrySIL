package org.crysil.actor.u2f;

import java.util.Map;

import org.crysil.actor.u2f.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.header.U2FHeader;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;

import com.google.common.io.BaseEncoding;

/**
 * Signs data with a U2F Key
 */
public class Sign implements Command {

	protected static final String SIG_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";

	@Override
	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, Map<String, byte[]> cachedResponses,
			U2FActivityHandler activityHandler) throws CrySILException {
		PayloadSignRequest payload = (PayloadSignRequest) request.getPayload();
		PayloadSignResponse response = new PayloadSignResponse();

		byte[] wrappedKey = ((WrappedKey) payload.getSignatureKey()).getEncodedWrappedKey();
		byte[] inputData = payload.getHashesToBeSigned().iterator().next();
		byte[] appParam = new byte[32];
		byte[] clientParam = new byte[32];
		byte[] keyHandle = null;
		String mapKey = BaseEncoding.base16().encode(wrappedKey);

		if (cachedResponses.containsKey(mapKey)) {
			// we are still registering, return response from before
			byte[] u2fResponse = cachedResponses.get(mapKey);
			cachedResponses.remove(mapKey);

			System.arraycopy(inputData, 1, appParam, 0, 32);
			System.arraycopy(inputData, 1 + 32, clientParam, 0, 32);

			Logger.debug(String.format("Sign returning from Map response='%s'",
					BaseEncoding.base16().encode(u2fResponse)));
			response.addSignedHash(u2fResponse);
		} else {
			// we are authenticating
			if (request.getHeader() instanceof U2FHeader) {
				int counter = ((U2FHeader) request.getHeader()).getCounter();
				U2FUtil.replaceCounter(inputData, counter);
			}

			byte[] counter = new byte[4];
			System.arraycopy(inputData, 0, appParam, 0, 32);
			System.arraycopy(inputData, 1 + 32, counter, 0, 4);
			System.arraycopy(inputData, 5 + 32, clientParam, 0, 32);
			keyHandle = wrappedKey;

			Logger.debug(String.format("Sign waiting for NFC"));
			U2FDeviceHandler u2fHandler = activityHandler.activateNFC();
			if (u2fHandler == null) {
				Logger.error("No handler from Android");
				throw new UnknownErrorException();
			}

			try {
				byte[] u2fResponse = u2fHandler.signPlain(keyHandle, clientParam, appParam, counter);
				Logger.debug(String.format("Sign returning fresh response='%s'",
						BaseEncoding.base16().encode(u2fResponse)));
				response.addSignedHash(u2fResponse);
			} catch (Exception e) {
				Logger.error("Error from Android", e);
				throw new UnknownErrorException();
			}

		}
		return response;
	}
}
