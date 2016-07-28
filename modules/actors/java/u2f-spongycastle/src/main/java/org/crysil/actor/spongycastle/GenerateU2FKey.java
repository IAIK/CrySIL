package org.crysil.actor.spongycastle;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import org.crysil.actor.spongycastle.model.KeyAndCertificate;
import org.crysil.actor.spongycastle.model.KeyPairRepresentation;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.cms.*;
import org.spongycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.spongycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.spongycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.JCEECPrivateKey;
import org.spongycastle.jce.provider.JCEECPublicKey;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.util.Arrays;
import java.util.Date;

/**
 * Can generate U2F Keys
 */
public class GenerateU2FKey extends Command {

	private static final int MAC_LEN = 32;
	private static final int RANDOM_LEN = 32;
	private static final int HANDLE_LEN = MAC_LEN + RANDOM_LEN;

	@Override
	public PayloadResponse perform(Request request, AndroidKeyStore keyStore) throws CrySILException {
		PayloadGenerateU2FKeyRequest payload = (PayloadGenerateU2FKeyRequest) request.getPayload();
		byte[] appParam = payload.getAppParam();
		X500Name certificateSubject = new X500Name(payload.getCertificateSubject());
		byte[] encodedRandom = payload.getEncodedRandom();
		PrivateKey signingKey = keyStore.getDefaultSigningKey().getPrivateKey();
		X509Certificate signingCertificate = keyStore.getDefaultSigningKey().getCertificate();

		byte[] issuer = signingCertificate.getIssuerDN().toString().getBytes();
		byte[] serial = signingCertificate.getSerialNumber().toByteArray();
		byte[] time = Long.toString(new Date().getTime()).getBytes();

		byte[] randomNumber = new byte[RANDOM_LEN];
		byte[] keyHandle = new byte[HANDLE_LEN];
		byte[] existingMac = null;

		try {
			if (encodedRandom != null && encodedRandom.length > 0) {
				existingMac = new byte[MAC_LEN];
				keyHandle = payload.getEncodedRandom();
				System.arraycopy(keyHandle, 0, existingMac, 0, MAC_LEN);
				System.arraycopy(keyHandle, MAC_LEN, randomNumber, 0, RANDOM_LEN);
			} else {
				SecureRandom.getInstance(SHA1_PRNG).nextBytes(randomNumber);
			}

			Mac mac = Mac.getInstance(SHA256_HMAC);
			Key secretKey = getSecret(signingKey);

			mac.init(secretKey);
			mac.update(appParam);
			mac.update(randomNumber);
			byte[] internalMac = mac.doFinal();

			mac.init(secretKey);
			mac.update(appParam);
			mac.update(internalMac);
			byte[] externalMac = mac.doFinal();

			// Check whether externally provided MAC matches the calculated MAC
			if (existingMac != null && !Arrays.equals(externalMac, existingMac)) {
				throw new Exception("MACs do not match, invalid keyHandle given");
			}

			// keyHandle = [externalMac || randomNumber]
			keyHandle = Bytes.concat(externalMac, randomNumber);

			// Derive EC key from known random
			ECParameterSpec params = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
			BigInteger privateInt = new BigInteger(internalMac).abs();
			ECPrivateKeySpec privateKey = new ECPrivateKeySpec(privateInt, params);
			JCEECPrivateKey jceecPrivateKey = new JCEECPrivateKey(EC, privateKey);
			ECPublicKeySpec publicKey = new ECPublicKeySpec(params.getG().multiply(privateInt), params);
			JCEECPublicKey jceecPublicKey = new JCEECPublicKey(EC, publicKey);
			SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(jceecPublicKey.getEncoded());

			// Create certificate
			MessageDigest messageDigest = MessageDigest.getInstance(SHA256);
			byte[] hashedSerialNumber = messageDigest.digest(Bytes.concat(issuer, serial, time, randomNumber));

			X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(new X509CertificateHolder(
					signingCertificate.getEncoded()).getIssuer(), new BigInteger(hashedSerialNumber),
					signingCertificate.getNotBefore(), signingCertificate.getNotAfter(), certificateSubject,
					subjectPublicKeyInfo);
			ContentSigner sigGen = new KeystoreContentSigner(SHA256_RSA, keyStore.getDefaultSigningKey()
					.getAlias());
			X509CertificateHolder holder = certGen.build(sigGen);
			CertificateFactory factory = CertificateFactory.getInstance(X_509, PROVIDER);
			Certificate wrappedKeyCertificate = factory.generateCertificate(new ByteArrayInputStream(holder
					.getEncoded()));

			// Create json representation for keypair
			KeyPairRepresentation keyPairRepresentation = new KeyPairRepresentation();
			keyPairRepresentation.setEncodedKey(BaseEncoding.base64().encode(jceecPrivateKey.getEncoded()));
			keyPairRepresentation.setEncodedX509Certificate(BaseEncoding.base64().encode(
					wrappedKeyCertificate.getEncoded()));

			String jsonKeyPair = new Gson().toJson(keyPairRepresentation);

			// Create CMS container for encryption certs and sign it with signing key
			byte[] encryptedWrappedKey = encryptCMSContainer(jsonKeyPair.getBytes(), keyStore.getDefaultSigningKey());
			// Sign the container using the given signing key or the default key
			byte[] encryptedAndSignedWrappedKey = signCMS(encryptedWrappedKey, keyStore.getDefaultSigningKey());

			PayloadGenerateU2FKeyResponse generateWrappedKeyResponse = new PayloadGenerateU2FKeyResponse();
			generateWrappedKeyResponse.setEncodedWrappedKey(encryptedAndSignedWrappedKey);
			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			generateWrappedKeyResponse.setCertificate((X509Certificate) cf.generateCertificate(new ByteArrayInputStream(wrappedKeyCertificate.getEncoded())));
			generateWrappedKeyResponse.setEncodedRandom(keyHandle);

			return generateWrappedKeyResponse;
		} catch (Exception e) {
			throw new UnknownErrorException();
		}
	}

	private byte[] signCMS(byte[] dataToSign, KeyAndCertificate keyAndCert) throws CrySILException {
		try {
			X509CertificateHolder signCert = new X509CertificateHolder(keyAndCert.getCertificate().getEncoded());
			CMSTypedData msg = new CMSProcessableByteArray(dataToSign);
			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
			ContentSigner signer = new KeystoreContentSigner(SHA256_RSA, keyAndCert.getAlias());
			gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder()
					.setProvider(PROVIDER).build()).build(signer, signCert));
			gen.addCertificate(signCert);
			CMSSignedData sigData = gen.generate(msg, true);
			return sigData.getEncoded();
		} catch (CertificateEncodingException | IOException | OperatorCreationException | CMSException e) {
			Logger.error("CMS signing error", e);
			throw new UnknownErrorException();
		}
	}

	private byte[] encryptCMSContainer(byte[] dataToEncrypt, KeyAndCertificate keyAndCert) throws CrySILException {
		try {
			dataToEncrypt = signCMS(dataToEncrypt, keyAndCert);
			CertificateFactory factory = CertificateFactory.getInstance(X_509, PROVIDER);
			java.security.cert.X509Certificate signingCert = (java.security.cert.X509Certificate) factory
					.generateCertificate(new ByteArrayInputStream(keyAndCert.getCertificate().getEncoded()));
			CMSTypedData msg = new CMSProcessableByteArray(dataToEncrypt);
			CMSEnvelopedDataGenerator generator = new CMSEnvelopedDataGenerator();
			generator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(signingCert)
					.setProvider(AndroidKeyStore.PROVIDER));
			CMSEnvelopedData ed = generator.generate(msg, new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
					.setProvider(PROVIDER).build());
			return ed.getEncoded();
		} catch (CertificateException | CMSException | NoSuchProviderException | IOException e) {
			Logger.error("CMS encryption error", e);
			throw new UnknownErrorException();
		}
	}

	/**
	 * Deterministically derive a secret from a signing key to be used as a HMAC key <br />
	 * We can't use the key directly, because a private key (asymmetric) is not a secret key (symmetric)
	 */
	private Key getSecret(PrivateKey signingKey) throws Exception {
		Signature signature = Signature.getInstance(SHA256_RSA, AndroidKeyStore.PROVIDER);
		signature.initSign(signingKey);
		signature.update("super secret u2f".getBytes("UTF-8"));
		byte[] key = signature.sign();
		return new SecretKeySpec(key, SHA256_HMAC);
	}
}
