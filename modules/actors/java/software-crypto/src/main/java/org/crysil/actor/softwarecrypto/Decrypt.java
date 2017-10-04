package org.crysil.actor.softwarecrypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

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
			Cipher cipher = null;

			Key key = keystore.getJCEPrivateKey(request.getDecryptionKey());
			if (key instanceof SecretKey)
				cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			else {
				cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				key = keystore.getJCEPublicKey(request.getDecryptionKey());
			}

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
