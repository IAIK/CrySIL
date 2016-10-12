package org.crysil.actor.staticKeyEncryption;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;
import org.crysil.protocol.payload.crypto.decryptCMS.PayloadDecryptCMSRequest;
import org.crysil.protocol.payload.crypto.decryptCMS.PayloadDecryptCMSResponse;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;

import org.apache.commons.io.IOUtils;

public class DecryptCMS implements Command {

	@Override
	public PayloadResponse perform(Request input) throws CrySILException {
		
		try {
			if (!(input.getPayload() instanceof PayloadDecryptCMSRequest)) {
				throw new UnknownErrorException();
			}

			PayloadDecryptCMSRequest payloadEncryptCMSRequest = (PayloadDecryptCMSRequest) input.getPayload();
			List<byte[]> encryptedCMSData = payloadEncryptCMSRequest.getEncryptedCMSData();
			
			Key key = (Key) payloadEncryptCMSRequest.getDecryptionKey();
			SimpleKeyStore ks = SimpleKeyStore.getInstance();
			PrivateKey decryptionKey = ks.getJCEPrivateKey(key);

			List<byte[]> decryptedCMSdataList = new ArrayList<>();
			for (byte[] encryptedCMSEntry : encryptedCMSData) {

				CMSEnvelopedDataParser ep = new CMSEnvelopedDataParser(encryptedCMSEntry);
				RecipientInformationStore recipients = ep.getRecipientInfos();
				Collection<RecipientInformation> c = recipients.getRecipients();
				// TODO: find correct recipient info
				Iterator<RecipientInformation> it = c.iterator();
				if (it.hasNext()) {
					RecipientInformation recipient = (RecipientInformation) it.next();
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
					CMSTypedStream recData = recipient .getContentStream(new JceKeyTransEnvelopedRecipient(decryptionKey).setProvider("BC"));
					InputStream decryptedCMSdata = recData.getContentStream();
					decryptedCMSdataList.add(IOUtils.toByteArray(decryptedCMSdata));
				}
				
				PayloadDecryptCMSResponse payloadDecryptCMSResponse = new PayloadDecryptCMSResponse();
				payloadDecryptCMSResponse.setPlainData(decryptedCMSdataList);
				
				return payloadDecryptCMSResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
