package org.crysil.actor.spongycastle;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import org.crysil.actor.spongycastle.model.KeyAndCertificate;
import org.crysil.actor.spongycastle.model.KeyPairRepresentation;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.header.U2FHeader;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.*;
import org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.spongycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.spongycastle.operator.OperatorException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Signs data with a U2F Key
 */
public class Sign extends Command {

	@Override
	public PayloadResponse perform(Request request, AndroidKeyStore keyStore) throws CrySILException {
		PayloadSignRequest payload = (PayloadSignRequest) request.getPayload();

		if (!payload.getAlgorithm().equals(SHA256_ECDSA))
			throw new UnknownErrorException();

		Key signingKey = payload.getSignatureKey();
		List<byte[]> base64Loads = payload.getHashesToBeSigned();
		KeyAndCertificate jceKeyAndCertificate = findKey(signingKey, keyStore);

		List<byte[]> signedHashes = new ArrayList<>();
		for (byte[] inputData : base64Loads) {
			try {
				if (request.getHeader() instanceof U2FHeader) {
					int counter = ((U2FHeader) request.getHeader()).getCounter();
					U2FUtil.replaceCounter(inputData, counter);
				}

				PrivateKey key = jceKeyAndCertificate.getPrivateKey();
				Signature signatureEngine = Signature.getInstance(payload.getAlgorithm());
				signatureEngine.initSign(key);
				signatureEngine.update(inputData);
				byte[] response = signatureEngine.sign();
				signedHashes.add(response);
			} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
				Logger.error("Error while signing hash", e);
				throw new UnknownErrorException();
			}
		}

		PayloadSignResponse response = new PayloadSignResponse();
		response.setSignedHashes(signedHashes);
		return response;
	}

	private KeyAndCertificate findKey(Key key, AndroidKeyStore keyStore) throws CrySILException {
		WrappedKey wrappedKey = (WrappedKey) key;
		try {
			KeyAndCertificate decryptionKey = keyStore.getDefaultSigningKey();
			byte[] wrappedKeyBytes = getContentFromSignedCMS(wrappedKey.getEncodedWrappedKey(), decryptionKey);
			byte[] unwrappedSignedKeybytes = decryptCMSContainer(wrappedKeyBytes, decryptionKey);
			byte[] unwrappedKeybytes = getContentFromSignedCMS(unwrappedSignedKeybytes, decryptionKey);
			KeyPairRepresentation keyPairRepresentation = new Gson().fromJson(new String(unwrappedKeybytes),
					KeyPairRepresentation.class);

			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(
					BaseEncoding.base64().decode(keyPairRepresentation.getEncodedX509Certificate())));
			KeyFactory kf = KeyFactory.getInstance(EC);
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(
					keyPairRepresentation.getEncodedKey()));
			PrivateKey privateKey = kf.generatePrivate(ks);
			return new KeyAndCertificate(privateKey, certificate, "u2f");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | CertificateException e) {
			Logger.error("Exception while parsing wrapped key", e);
			throw new UnknownErrorException();
		}
	}

	private byte[] getContentFromSignedCMS(byte[] signedCMS, KeyAndCertificate keyAndCert) {
		try {
			X509CertificateHolder signCert = new X509CertificateHolder(keyAndCert.getCertificate().getEncoded());
			CMSSignedData signedData = new CMSSignedData(signedCMS);
			SignerInformationStore signers = signedData.getSignerInfos();
			for (SignerInformation signer : signers.getSigners()) {
				if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(PROVIDER).build(signCert)))
					throw new CMSException("Certificate not valid");
			}
			CMSTypedData signedContent = signedData.getSignedContent();
			return (byte[]) signedContent.getContent();
		} catch (IOException | CMSException | OperatorException
				| GeneralSecurityException e) {
			Logger.error("Failed to parse signed CMS!", e);
		}
		return null;
	}

	private byte[] decryptCMSContainer(byte[] wrappedKeyBytes, KeyAndCertificate decryptionKey) throws CrySILException {
		try {
			CMSEnvelopedData ed = new CMSEnvelopedData(wrappedKeyBytes);
			RecipientInformationStore recipients = ed.getRecipientInfos();
			for (RecipientInformation recipient : recipients.getRecipients()) {
				byte[] content = recipient.getContent(new JceKeyTransEnvelopedRecipient(decryptionKey.getPrivateKey())
						.setProvider(AndroidKeyStore.PROVIDER));
				return content;
			}
		} catch (CMSException e) {
			Logger.error("CMS decryption error", e);
			throw new UnknownErrorException();
		}
		throw new UnknownErrorException();
	}
}
