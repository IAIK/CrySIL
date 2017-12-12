package org.crysil.instance.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ContentVerifierProviderBuilder;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.crysil.instance.AppConfiguration;
import org.crysil.instance.datastore.DeviceRegistration;
import org.crysil.instance.datastore.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles signing the certificates from the CrySIL Android servers
 */
public class CertificateUtil {

	private static Logger logger = LoggerFactory.getLogger(CertificateUtil.class);

	private static final String KEYSTORE_TYPE = "PKCS12";
	private static final String KEYPAIR_GENERATOR_PROVIDER = "BC";
	private static final String KEYPAIR_GENERATOR_ALGORITHM = "RSA";
	private static final int KEYPAIR_SIZE = 2048;
	private static final String ISSUER_MAIL = "crysil@example.com";
	private static final String ISSUER_CN = "127.0.0.1";
	private static final String ISSUER_O = "WebVPN";
	private static final int CERT_LIFETIME = 6;

	@Autowired
	private AppConfiguration config;

	@Autowired
	private DeviceRepository repository;

	private PrivateKeyEntry privateKeyEntry;

	public CertificateUtil(AppConfiguration config, DeviceRepository repository) {
		this.config = config;
		this.repository = repository;
		this.init();
	}

	public CertificateUtil() {
		this.init();
	}

	/**
	 * Loads the keystore and signing key (if the file exists), otherwise create a new keystore with that single entry
	 */
	private void init() {
		try {
			KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			File keyFile = new File(config.getKeyFile());
			if (keyFile.exists()) {
				logger.debug("Loading keystore and keypair from {}", config.getKeyFile());
				keyStore.load(new FileInputStream(keyFile), config.getKeyFilePassword().toCharArray());
				privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(config.getKeyFileAlias(),
						new KeyStore.PasswordProtection(config.getKeyFileAliasPassword().toCharArray()));
			} else {
				logger.debug("Creating new keystore");
				keyStore.load(null, null);
				logger.debug("Generated new issuer name with {} and {}", ISSUER_CN, ISSUER_MAIL);
				KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEYPAIR_GENERATOR_ALGORITHM,
						KEYPAIR_GENERATOR_PROVIDER);
				keyPairGenerator.initialize(KEYPAIR_SIZE);
				KeyPair keyPair = keyPairGenerator.generateKeyPair();
				logger.debug("Generated new keyPair with public key {}", keyPair.getPublic());
				X500Name certIssuer = new X500NameBuilder().addRDN(BCStyle.CN, ISSUER_CN).addRDN(BCStyle.O, ISSUER_O)
						.addRDN(BCStyle.EmailAddress, ISSUER_MAIL).build();
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				Date notBefore = gregorianCalendar.getTime();
				gregorianCalendar.add(Calendar.MONTH, 6);
				Date notAfter = gregorianCalendar.getTime();
				X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(
						certIssuer, getSerialNumber(), notBefore, notAfter, certIssuer,
						SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));
				AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
						.find("SHA256withRSAEncryption");
				AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
				ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(PrivateKeyFactory
						.createKey(keyPair.getPrivate().getEncoded()));
				X509CertificateHolder holder = certGen.build(sigGen);
				CertificateFactory factory = CertificateFactory.getInstance("X.509", KEYPAIR_GENERATOR_PROVIDER);
				Certificate certificate = factory.generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
				Certificate[] certChain = new Certificate[1];
				certChain[0] = certificate;
				logger.debug("Storing new keyPair in keystore");
				keyStore.setKeyEntry(config.getKeyFileAlias(), keyPair.getPrivate(), config.getKeyFileAliasPassword()
						.toCharArray(), certChain);
				privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(config.getKeyFileAlias(),
						new KeyStore.PasswordProtection(config.getKeyFileAliasPassword().toCharArray()));
				logger.debug("Storing keystore to {}", config.getKeyFile());
				keyStore.store(new FileOutputStream(keyFile), config.getKeyFilePassword().toCharArray());
			}
		} catch (GeneralSecurityException e) {
			logger.error("Could not instantiate CertificateUtil", e);
		} catch (IOException e) {
			logger.error("Could not instantiate CertificateUtil", e);
		} catch (OperatorCreationException e) {
			logger.error("Could not instantiate CertificateUtil", e);
		}
	}

	/**
	 * nasty workaround for when the service is not run under / of the TLD
	 * 
	 * @param certificate String PEM
	 * @return X509Certificate Object
	 * @throws CertificateException
	 * @throws NoSuchProviderException
	 */
	public static X509Certificate parseCertificate(String certificate)
			throws CertificateException, NoSuchProviderException {
		CertificateFactory factory = CertificateFactory.getInstance("X.509", KEYPAIR_GENERATOR_PROVIDER);
		return (X509Certificate) factory
				.generateCertificate(
						new ByteArrayInputStream(Base64.decode(certificate.replace("-----BEGIN CERTIFICATE-----", "")
								.replace("-----END CERTIFICATE-----", "").replace(" ", ""))));
	}

	/**
	 * Returns a signed (with {@link #keyPair}) certificate containing the <code>crysilId</code> as CN
	 * 
	 * @throws Exception
	 */
	public X509Certificate signCertificate(PKCS10CertificationRequest csr, Long crysilId) throws Exception {
		if (privateKeyEntry == null) {
			return null;
		}
		if (!csr.isSignatureValid(new JcaX509ContentVerifierProviderBuilder().build(csr.getSubjectPublicKeyInfo()))) {
			throw new CertificateException("Certificate signing request is not valid");
		}
		// TODO Is this the right way to extract the certificate from the signing request?
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		Date notBefore = gregorianCalendar.getTime();
		gregorianCalendar.add(Calendar.MONTH, CERT_LIFETIME);
		Date notAfter = gregorianCalendar.getTime();
		RDN cn = csr.getSubject().getRDNs(BCStyle.CN)[0];
		String certSubjectCn = IETFUtils.valueToString(cn.getFirst().getValue());
		if (certSubjectCn == null || !certSubjectCn.equalsIgnoreCase(Long.toString(crysilId))) {
			throw new CertificateException("Certificate does not contain correct crysilId");
		}

		X509CertificateHolder certHolder = new X509CertificateHolder(privateKeyEntry.getCertificate().getEncoded());
		X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(certHolder.getIssuer(), getSerialNumber(),
				notBefore, notAfter, csr.getSubject(), csr.getSubjectPublicKeyInfo());

		AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSAEncryption");
		AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
		ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(PrivateKeyFactory
				.createKey(privateKeyEntry.getPrivateKey().getEncoded()));
		X509CertificateHolder holder = certGen.build(sigGen);
		CertificateFactory factory = CertificateFactory.getInstance("X.509", KEYPAIR_GENERATOR_PROVIDER);
		X509Certificate certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(holder
				.getEncoded()));
		return certificate;
	}

	/**
	 * Returns true if this certificate is valid and was signed by us ({@link #keyPair})
	 */
	public boolean verifyCertificate(X509Certificate cert, String deviceId) {
		if (privateKeyEntry == null) {
			return false;
		}
		Long crysilId = null;
		Principal subject = cert.getSubjectDN();
		if (subject instanceof X500Name) {
			try {
				RDN cn = ((X500Name) subject).getRDNs(BCStyle.CN)[0];
				String cnString = IETFUtils.valueToString(cn.getFirst().getValue());
				crysilId = Long.parseLong(cnString);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		DeviceRegistration deviceRegistration = repository.findOne(crysilId);
		if (deviceRegistration == null || !deviceRegistration.getDeviceId().equalsIgnoreCase(deviceId)) {
			return false;
		}
		if (!cert.getIssuerDN().equals(((X509Certificate) privateKeyEntry.getCertificate()).getIssuerDN())) {
			return false;
		}
		try {
			// this may throw an exception if validation not succeeds
			cert.verify(privateKeyEntry.getCertificate().getPublicKey());
		} catch (GeneralSecurityException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	private static BigInteger getSerialNumber() {
		return BigInteger.valueOf(Math.abs((new Random()).nextLong() + 1));
	}

}
