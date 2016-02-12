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
import org.bouncycastle.util.encoders.Base64;
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

			// for each key
			List<List<String>> encryptedData = new ArrayList<List<String>>();
			for (Key currentKey : request.getKeys()) {
				PublicKey key = keystore.getJCEPublicKey(currentKey);
				cipher.init(Cipher.ENCRYPT_MODE, key);

				// encrypt
				List<String> encryptedDataForKey = new ArrayList<String>();
				for (String currentData : request.getPlainData())
					encryptedDataForKey.add(Base64.toBase64String(cipher.doFinal(currentData.getBytes())));

				encryptedData.add(encryptedDataForKey);
			}

			// assemble response
			PayloadEncryptResponse result = new PayloadEncryptResponse();
			result.setEncryptedData(encryptedData);

			return result;

		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchProviderException | NoSuchPaddingException e) {
			throw new UnknownErrorException();
		}
	}

}
