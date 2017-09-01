package org.crysil.actor.softwarecrypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;

public class Decrypt implements Command {

	@Override
	public PayloadResponse perform(Request input, SoftwareCryptoKeyStore keystore) throws CrySILException {
		PayloadDecryptRequest request = (PayloadDecryptRequest) input.getPayload();

		try {
			// prepare stuff
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			PrivateKey key = (PrivateKey) keystore.getJCEPrivateKey(request.getDecryptionKey());
			cipher.init(Cipher.DECRYPT_MODE, key);

			// assemble response
			PayloadDecryptResponse result = new PayloadDecryptResponse();

			// decrypt
			for (byte[] currentData : request.getEncryptedData())
				result.addPlainData(cipher.doFinal(currentData));

			return result;

		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			throw new UnknownErrorException();
		}
	}

}
