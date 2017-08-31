package org.crysil.actor.softwarecrypto;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptResponse;
import org.crysil.protocol.payload.crypto.key.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Encrypt implements Command {

	@Override
	public PayloadResponse perform(Request input) throws CrySILException {
		PayloadEncryptRequest request = (PayloadEncryptRequest) input.getPayload();

		try {
			// prepare stuff
			SimpleKeyStore keystore = SimpleKeyStore.getInstance();
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			// assemble response
			PayloadEncryptResponse result = new PayloadEncryptResponse();

			// for each key
			for (Key currentKey : request.getKeys()) {
				PublicKey key = keystore.getJCEPublicKey(currentKey);
				cipher.init(Cipher.ENCRYPT_MODE, key);

				// encrypt
				List<byte[]> encryptedDataForKey = new ArrayList<>();
				for (byte[] currentData : request.getPlainData())
					encryptedDataForKey.add(cipher.doFinal(currentData));

				result.addEncryptedDataPerKey(encryptedDataForKey);
			}

			return result;

		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			throw new UnknownErrorException();
		}
	}

}
