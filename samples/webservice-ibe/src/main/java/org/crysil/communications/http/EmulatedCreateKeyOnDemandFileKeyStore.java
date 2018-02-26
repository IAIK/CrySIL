package org.crysil.communications.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.crysil.actor.softwarecrypto.SoftwareCryptoKeyStore;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.Resources;

import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Name;
import iaik.security.random.SecRandom;
import iaik.security.ssl.KeyAndCert;
import iaik.security.ssl.Utils;
import iaik.x509.V3Extension;
import iaik.x509.X509Certificate;
import iaik.x509.extensions.BasicConstraints;
import iaik.x509.extensions.KeyUsage;

public class EmulatedCreateKeyOnDemandFileKeyStore implements SoftwareCryptoKeyStore {
	private char[] password;
	protected KeyStore keystore = null;
    private String file;
    private String keyStoreType = "JKS";
	private String userMail = null;
	private String username;
	private String alias;
	private KeyAndCert rootCred;

	public EmulatedCreateKeyOnDemandFileKeyStore(String file, char[] password, String capsoConfigFile)
			throws Exception {
		this.file = file;
		this.password = password;

		try {
			keystore = KeyStore.getInstance(keyStoreType);

			if (!(new File(file)).exists())
				keystore.load(null);
			else
				keystore.load(new FileInputStream(file), password);

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			keystore = null;
			System.err.println("Error while loading keystore from file system: " + e.getMessage());
			throw new KeyStoreUnavailableException();
		}

	}

    public String getKeyStoreType() {
        return keyStoreType;
    }

	@Override
	public Key getPrivateKey(KeyHandle keyHandle) throws KeyNotFoundException {

		try {
			return keystore.getKey(keyHandle.getId() + "/" + keyHandle.getSubId(), password);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new KeyNotFoundException();
		}
	}

	@Override
	public java.security.cert.X509Certificate getX509Certificate(KeyHandle keyHandle) {
		try {
			return (java.security.cert.X509Certificate) keystore
					.getCertificate(keyHandle.getId() + "/" + keyHandle.getSubId());
		} catch (KeyStoreException e) {
		}

		return null;
	}

	@Override
	public PublicKey getPublicKey(KeyHandle keyHandle)
			throws InvalidCertificateException, KeyNotFoundException {
		return getX509Certificate(keyHandle).getPublicKey();
	}

	@Override
	public List<KeyHandle> getKeyList() {
		List<KeyHandle> result = new ArrayList<>();

		// in case we got no valid filter. And yes, that is sketchy.
		// Prototype...
		if (null == alias)
			return result;

		try {
			if (!keystore.containsAlias(alias)) {
				// create the key on demand
				createKey();
			}

			// finally, add the key to the result set
			KeyHandle tmp = new KeyHandle();
			tmp.setId(username);
			tmp.setSubId(userMail);
			result.add(tmp);
		} catch (Exception e) {
			// well, we were not able to retrieve any key
			System.err.println(e.getMessage());
		}

		return result;
	}

	@Override
	public void addFilter(Header header) {
		if (!(header instanceof FeatureSetHeader))
			return;
		
		Feature feature = ((FeatureSetHeader) header)
				.getFeature(ActiveDirectoryAttributeAuthResult.class.getSimpleName());

		if (null == feature)
			return;

		userMail = ((ActiveDirectoryAttributeAuthResult) feature).geteMailAddress();
		username = ((ActiveDirectoryAttributeAuthResult) feature).getUsername();
		alias = username + "/" + userMail;
	}

	/**
	 * Generate a KeyPair.
	 *
	 * @param bits the length of the key (modulus) in bits
	 * @return the KeyPair
	 */
	public static KeyPair generateKeyPair(String algorithm, int bits) {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, "IAIK");
			generator.initialize(bits, SecRandom.getDefault());
			KeyPair kp = Utils.generateKeyPair(generator);
			System.out.println();
			return kp;
		} catch (NoSuchProviderException ex) {
			throw new RuntimeException("Required provider IAIK not installed!");
		} catch (NoSuchAlgorithmException e) {
			System.out.println();
			System.out.println("Failed: " + e.toString());
			return null;
		}
	}

	public KeyAndCert createCertificate(byte[] commonSubjectDER, KeyAndCert issuer, String cn, String ou, KeyPair kp,
			V3Extension[] extensions) {
		try {
			System.out.print("Generating " + ((ou != null) ? ou : cn) + " certificate...");
			if (kp == null) {
				System.out.println("KeyPair not available!");
				return null;
			}

			PrivateKey issuerKey;
			java.security.cert.X509Certificate[] issuerChain;
			if (issuer == null) {
				issuerKey = kp.getPrivate();
				issuerChain = null;
			} else {
				issuerKey = issuer.getPrivateKey();
				issuerChain = issuer.getCertificateChain();
			}

			Name subject = new Name(commonSubjectDER);
			if (ou != null) {
				subject.addRDN(ObjectID.organizationalUnit, ou);
			}
			if (cn != null) {
				subject.addRDN(ObjectID.commonName, cn);
			}

			X509Certificate cert = new X509Certificate();

			cert.setSerialNumber(new BigInteger(20, new Random()));
			cert.setSubjectDN(subject);
			cert.setPublicKey(kp.getPublic());
			if (issuerChain != null) {
				cert.setIssuerDN(issuerChain[0].getSubjectDN());
			} else {
				cert.setIssuerDN(subject);
			}

			GregorianCalendar date = new GregorianCalendar();
			cert.setValidNotBefore(date.getTime()); // not before

			date.add(Calendar.YEAR, 5); // five years for this demo
			cert.setValidNotAfter(date.getTime());

			AlgorithmID algorithm;
			if (issuerKey instanceof java.security.interfaces.DSAPrivateKey) {
				algorithm = AlgorithmID.dsaWithSHA;
			} else {
				algorithm = AlgorithmID.sha256WithRSAEncryption;
			}

			if (extensions != null) {
				for (int i = 0; i < extensions.length; i++) {
					cert.addExtension(extensions[i]);
				}
			}
			cert.sign(algorithm, issuerKey);

			X509Certificate[] chain;
			if (issuerChain == null) {
				chain = new X509Certificate[] { cert };
			} else {
				int n = issuerChain.length;
				chain = new X509Certificate[n + 1];
				chain[0] = cert;
				System.arraycopy(issuerChain, 0, chain, 1, n);
			}
			KeyAndCert kac = new KeyAndCert(chain, kp.getPrivate());

			System.out.println();
			return kac;
		} catch (Exception e) {
			System.out.println();
			System.out.println("Failed: " + e.toString());
			return null;
		}
	}

	private synchronized void createKey() throws Exception {
		if (null == rootCred) {
			Name commonSubject = new Name();
			String uid = BigInteger.valueOf(SecRandom.getDefault().nextInt() & 0x7fffff).toString(16).toUpperCase();
			commonSubject.addRDN(ObjectID.locality, "Internet");
			commonSubject.addRDN(ObjectID.organization, "CrySIL Demo Certificate");
			commonSubject.addRDN(ObjectID.organization, "User Generated Demo Certificates UID " + uid);
			byte[] commonSubjectDER = commonSubject.getEncoded();

			if (!keystore.containsAlias("root")) {
				V3Extension[] extensions = { new BasicConstraints(true), new KeyUsage(KeyUsage.keyCertSign
						| KeyUsage.cRLSign | KeyUsage.dataEncipherment | KeyUsage.digitalSignature) };
				KeyPair rootRSAKeyPair = generateKeyPair("RSA", 2048);
				rootCred = createCertificate(commonSubjectDER, null, "IAIK", "CrySILSoftwareTestCAKey", rootRSAKeyPair,
						extensions);

				PrivateKey privateKey = rootCred.getPrivateKey();
				keystore.setKeyEntry("root", privateKey, password, rootCred.getCertificateChain());
			} else {
				rootCred = new KeyAndCert(null, (PrivateKey) keystore.getKey("root", password));
			}
		}
		Name commonSubject = new Name();
		String uid = BigInteger.valueOf(SecRandom.getDefault().nextInt() & 0x7fffff).toString(16).toUpperCase();
		commonSubject.addRDN(ObjectID.locality, "Internet");
		commonSubject.addRDN(ObjectID.organization, "CrySIL Demo Certificate");
		commonSubject.addRDN(ObjectID.organization, "User Generated Demo Certificates UID " + uid);
		byte[] commonSubjectDER = commonSubject.getEncoded();

		V3Extension[] extensions = { new BasicConstraints(false), new KeyUsage(
				KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.dataEncipherment | KeyUsage.digitalSignature) };

		KeyPair rootRSAKeyPair = generateKeyPair("RSA", 2048);
		KeyAndCert kac = createCertificate(commonSubjectDER, rootCred, username, "CrySILSoftwareTestKey",
				rootRSAKeyPair,
				extensions);
		PrivateKey privateKey = kac.getPrivateKey();
		keystore.setKeyEntry(alias, privateKey, password, kac.getCertificateChain());

		if (file.startsWith("classpath:")) {
			keystore.store(
					new FileOutputStream(new File(Resources.getResource(file.replace("classpath:", "")).getPath())),
					password);
		} else {
			keystore.store(new FileOutputStream(file), password);
		}
	}
}
