package org.crysil.actor.staticKeyEncryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptResponse;
import org.crysil.protocol.payload.crypto.key.Key;

public class Encrypt implements Command {

	@Override
	public PayloadResponse perform(PayloadRequest input) throws CrySILException {
		PayloadEncryptRequest request = (PayloadEncryptRequest) input;

		try {
			// prepare stuff
			SimpleKeyStore keystore = SimpleKeyStore.getInstance();
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");

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
				| NoSuchProviderException | NoSuchPaddingException e) {
			throw new UnknownErrorException();
		}
	}

}
