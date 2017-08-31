package org.crysil.actor.softwarecrypto;

import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptResponse;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;

public class EncryptCMS implements Command {

	@Override
	public PayloadResponse perform(Request input, SoftwareCryptoKeyStore keystore) throws CrySILException {
		
		try {
			if (!(input.getPayload() instanceof PayloadEncryptRequest)) {
	            throw new UnknownErrorException();
	        }
			
			PayloadEncryptRequest PayloadEncryptCMSRequest = (PayloadEncryptRequest) input.getPayload();
	        List<Key> encryptionKeys = PayloadEncryptCMSRequest.getEncryptionKeys();
	        List<byte[]> plainDataList = PayloadEncryptCMSRequest.getPlainData();
	        String algorithm = PayloadEncryptCMSRequest.getAlgorithm();

	        if (!algorithm.startsWith("CMS")) {
	            throw new UnknownErrorException();
	        }

	        List<byte[]> encryptedDataList = new ArrayList<>();
	        for (byte[] plainData : plainDataList) {
	        	
	        	// TODO: for multiple recipients
	        	InternalCertificate x = (InternalCertificate) encryptionKeys.get(0);
	        	X509Certificate recipientCert = x.getCertificate();
	        	
				CMSTypedData msg = new CMSProcessableByteArray(plainData);
				CMSEnvelopedDataGenerator edGen = new CMSEnvelopedDataGenerator();
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				edGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(recipientCert).setProvider("BC"));
				CMSEnvelopedData ed = edGen.generate(msg, new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC).setProvider("BC").build());

				byte[] encryptedData = ed.getEncoded();
				
	            encryptedDataList.add(encryptedData);
	        }

			PayloadEncryptResponse payloadEncryptCMSResponse = new PayloadEncryptResponse();
			
			List<List<byte[]>> tmp = new ArrayList<>();
			tmp.add(encryptedDataList);
			
			payloadEncryptCMSResponse.setEncryptedData(tmp);
	        
	        return payloadEncryptCMSResponse;
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
