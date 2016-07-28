package org.crysil.actor.spongycastle;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.logging.Logger;
import org.crysil.u2f.U2FCounterStore;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.operator.ContentSigner;

/**
 * Store the counter for U2F as the serial number in an certificate in the provided keystore
 */
public class KeystoreU2FCounterStore implements U2FCounterStore {

	private static final String ALIAS = "u2fcounteralias";

	private final KeyStore keyStore;
	private int counter = -1;

	public KeystoreU2FCounterStore(KeyStore keyStore) throws CrySILException {
		this.keyStore = keyStore;
		try {
			initialize();
		} catch (Exception e) {
			Logger.error("Error while loading keystore: " + e.getMessage());
			throw new KeyStoreUnavailableException();
		}
	}

	private void initialize() throws Exception {
		keyStore.load(null, null);
		if (keyStore.containsAlias(ALIAS)) {
			loadCounterAndCertificate();
		}
		if (counter == -1) {
			counter = 0;
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", AndroidKeyStore.PROVIDER);
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			X500Name certSubject = new X500Name("CN=" + ALIAS);
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			Date notBefore = gregorianCalendar.getTime();
			gregorianCalendar.add(Calendar.YEAR, 6);
			Date notAfter = gregorianCalendar.getTime();
			X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(certSubject, BigInteger.valueOf(counter),
					notBefore, notAfter, certSubject,
					SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));
			ContentSigner sigGen = new KeypairContentSigner("SHA256withRSA", keyPair);
			X509CertificateHolder newHolder = certGen.build(sigGen);
			Certificate newCertificate = CertificateFactory.getInstance("X.509", "SC").generateCertificate(
					new ByteArrayInputStream(newHolder.getEncoded()));
			keyStore.setKeyEntry(ALIAS, keyPair.getPrivate(), null, new Certificate[] { newCertificate });
			Logger.debug("Created new certificate with counter value: " + counter);
		}
	}

	private void incrementValue() throws Exception {
		if (keyStore.containsAlias(ALIAS)) {
			Key key = keyStore.getKey(ALIAS, null);
			Certificate certificate = loadCounterAndCertificate();
			X509CertificateHolder holder = new X509CertificateHolder(certificate.getEncoded());
			X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(holder.getSubject(),
					BigInteger.valueOf(++counter), holder.getNotBefore(), holder.getNotAfter(), holder.getIssuer(),
					holder.getSubjectPublicKeyInfo());
			ContentSigner sigGen = new KeystoreContentSigner("SHA256withRSA", ALIAS);
			X509CertificateHolder newHolder = certGen.build(sigGen);
			Certificate newCertificate = CertificateFactory.getInstance("X.509", "SC").generateCertificate(
					new ByteArrayInputStream(newHolder.getEncoded()));
			keyStore.setKeyEntry(ALIAS, key, null, new Certificate[] { newCertificate });
			Logger.debug("Incremented counter value in certificate to: " + counter);
		}
	}

	private Certificate loadCounterAndCertificate() throws KeyStoreException, CertificateException,
			CertificateEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException,
			SignatureException {
		Certificate certificate = keyStore.getCertificate(ALIAS);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate x509 = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificate
				.getEncoded()));
		x509.verify(x509.getPublicKey());
		if (x509.getSerialNumber().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0) {
			counter = x509.getSerialNumber().intValue();
			Logger.debug("Got existing certificate with counter value: " + counter);
		}
		return certificate;
	}

	@Override
	public int getCounter() {
		return counter;
	}

	@Override
	public int incrementCounter() {
		try {
			incrementValue();
		} catch (Exception e) {
			Logger.error("Error while loading keystore: " + e.getMessage());
		}
		return counter;
	}

}
