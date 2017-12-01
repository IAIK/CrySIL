package org.crysil.actor.softwarecrypto;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.NotImplementedException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

public class DecryptCMS implements Command {

	@Override
	public PayloadResponse perform(Request input, SoftwareCryptoKeyStore keystore) throws CrySILException {

		if (!(input.getPayload() instanceof PayloadDecryptRequest))
			throw new UnknownErrorException();
		
		if (!(((PayloadDecryptRequest) input.getPayload()).getDecryptionKey() instanceof KeyHandle))
			throw new NotImplementedException();
		
		try {

			PayloadDecryptRequest payloadEncryptCMSRequest = (PayloadDecryptRequest) input.getPayload();
			List<byte[]> encryptedCMSData = payloadEncryptCMSRequest.getEncryptedData();
			
			KeyHandle key = (KeyHandle) payloadEncryptCMSRequest.getDecryptionKey();
			PrivateKey decryptionKey = (PrivateKey) keystore.getPrivateKey(key);

			List<byte[]> decryptedCMSdataList = new ArrayList<>();
			for (byte[] encryptedCMSEntry : encryptedCMSData) {

				CMSEnvelopedDataParser ep = new CMSEnvelopedDataParser(encryptedCMSEntry);
				RecipientInformationStore recipients = ep.getRecipientInfos();
				Collection<RecipientInformation> c = recipients.getRecipients();
				// TODO: find correct recipient info
				Iterator<RecipientInformation> it = c.iterator();
				if (it.hasNext()) {
					RecipientInformation recipient = it.next();
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
					CMSTypedStream recData = recipient .getContentStream(new JceKeyTransEnvelopedRecipient(decryptionKey).setProvider("BC"));
					InputStream decryptedCMSdata = recData.getContentStream();
					// FIXME
					decryptedCMSdataList.add(IOUtils.toByteArray(decryptedCMSdata));
				}
				
				PayloadDecryptResponse payloadDecryptCMSResponse = new PayloadDecryptResponse();
				payloadDecryptCMSResponse.setPlainData(decryptedCMSdataList);
				
				return payloadDecryptCMSResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
