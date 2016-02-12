package org.crysil.actor.staticKeyEncryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
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
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;

public class Decrypt implements Command {

	@Override
	public PayloadResponse perform(PayloadRequest input) throws CrySILException {
		PayloadDecryptRequest request = (PayloadDecryptRequest) input;

		try {
			// prepare stuff
			SimpleKeyStore keystore = SimpleKeyStore.getInstance();
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");

			// for each key

			PrivateKey key = keystore.getJCEPrivateKey(request.getDecryptionKey());
			cipher.init(Cipher.DECRYPT_MODE, key);

			// decrypt
			List<String> decryptedData = new ArrayList<>();
			for (String currentData : request.getEncryptedData())
				decryptedData.add(new String(cipher.doFinal(Base64.decode(currentData))));

			// assemble response
			PayloadDecryptResponse result = new PayloadDecryptResponse();
			result.setPlainData(decryptedData);

			return result;

		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchProviderException | NoSuchPaddingException e) {
			throw new UnknownErrorException();
		}
	}

}
