package org.crysil.actor.staticKeyEncryption;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public class Decrypt implements Command {

	@Override
	public PayloadResponse perform(Request input) throws CrySILException {
		PayloadDecryptRequest request = (PayloadDecryptRequest) input.getPayload();

		try {
			// prepare stuff
			SimpleKeyStore keystore = SimpleKeyStore.getInstance();
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			PrivateKey key = keystore.getJCEPrivateKey(request.getDecryptionKey());
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
