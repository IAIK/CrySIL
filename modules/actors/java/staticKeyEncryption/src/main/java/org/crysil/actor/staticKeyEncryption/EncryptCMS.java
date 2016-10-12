package org.crysil.actor.staticKeyEncryption;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.cms.CMSEncryptedDataGenerator;
import org.bouncycastle.cms.CMSEncryptedGenerator;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.crypto.CryptoException;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptResponse;
import org.crysil.protocol.payload.crypto.encryptCMS.PayloadEncryptCMSRequest;
import org.crysil.protocol.payload.crypto.encryptCMS.PayloadEncryptCMSResponse;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;

public class EncryptCMS implements Command {

	@Override
	public PayloadResponse perform(Request input) throws CrySILException {
		
		try {
			if (!(input.getPayload() instanceof PayloadEncryptCMSRequest)) {
	            throw new UnknownErrorException();
	        }
			
	        PayloadEncryptCMSRequest PayloadEncryptCMSRequest = (PayloadEncryptCMSRequest) input.getPayload();
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

	        PayloadEncryptCMSResponse payloadEncryptCMSResponse = new PayloadEncryptCMSResponse();
	        payloadEncryptCMSResponse.setEncryptedCMSData(encryptedDataList);
	        
	        return payloadEncryptCMSResponse;
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
