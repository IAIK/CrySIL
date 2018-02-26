package org.crysil.communications.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.crysil.actor.softwarecrypto.SoftwareCryptoKeyStore;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.Resources;

import capso.communication.client.CMDObjectFactory;
import capso.communication.client.Client;
import capso.communication.client.DefaultClientFactory;
import capso.config.xml.client.ClientConfig;
import capso.datastructures.asn1.CMDObject;
import capso.datastructures.asn1.DataObject;
import capso.datastructures.asn1.Request;
import capso.datastructures.asn1.SimpleData;
import capso.datastructures.asn1.profiles.Profile;
import capso.datastructures.asn1.profiles.ProfileRequest;
import capso.datastructures.asn1.profiles.Profiles;
import capso.datastructures.db.Requester;
import capso.datastructures.email.EmailAddresses;
import capso.datastructures.email.EmailAddresses.AddressType;
import iaik.asn1.structures.Name;

public class CreateKeyOnDemandFileKeyStore implements SoftwareCryptoKeyStore {
	private char[] password;
	protected KeyStore keystore = null;
    private String file;
    private String keyStoreType = "JKS";
	private String userMail = null;
	private ClientConfig clientConfig;
	private String username;
	private String alias;

	public CreateKeyOnDemandFileKeyStore(String file, char[] password, String capsoConfigFile) throws Exception {
		this.file = file;
		this.password = password;

		try {
			if (this.file.startsWith("classpath:")) {
				file = file.replace("classpath:", "");
			}

			keystore = KeyStore.getInstance(keyStoreType);

			if (!(new File(file)).exists())
				keystore.store(new FileOutputStream(file), password);

			keystore.load(new FileInputStream(file), password);

			clientConfig = new ClientConfig(Resources.getResource(capsoConfigFile).getPath());
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

	private synchronized void createKey() throws Exception {

		// The actual Capso Client
		Client client = new DefaultClientFactory().createClient(clientConfig);

		// # Usage #
		// Configurables
		String profileName = "test";
		String certType = "iaik-test-intermediate-ca-rsa-sign";

		// We need a profile (just once)
		Profiles profiles = (Profiles) client.send(CMDObjectFactory.createGetProfiles(profileName));
		Profile profile = profiles.getProfile(profileName);

		// Then we need a request object
		ProfileRequest request = new ProfileRequest(profile);
		request.setCertificateType(certType);

		EmailAddresses emails = new EmailAddresses();
		emails.put(userMail, AddressType.AltName); // Used for expiration info
														// _AND_ in certificate
		request.setEmailAddresses(emails, true);

		request.setRequester(Requester.Browser);
		request.setRequestDate(new Date());
		request.setOwnerID(username); // A String to connect the certificate
										// with a user account, e.g., freimair
		request.setRevocationPwd("revocationPassword");
		request.setRevocationPwdHint("revocationPasswordHint");

		Name subjectDN = new Name("CN=" + username);
		request.setSubjectDN(subjectDN);

		request.setPkcs12Request(true);
		request.setPKCS12Pwd(new String(password));

		CMDObject cmdObj = CMDObjectFactory.createAddRequestWithCertTypeExtensions(request);
		DataObject dataobject = client.send(cmdObj);
		Request issuedRequest = (Request) dataobject;

		SimpleData pkcs12Data = (SimpleData) client.send(CMDObjectFactory.createGetPkcs12File(issuedRequest));

		// load the pkcs12 key file into a keystore
		KeyStore tmp = KeyStore.getInstance("PKCS12");
		tmp.load(new ByteArrayInputStream(pkcs12Data.getData()), password);
		// retrieve the key
		Key key = tmp.getKey(userMail, password);

		Certificate[] certChain = new Certificate[1];
		certChain[0] = issuedRequest.getCertificate();
		keystore.setKeyEntry(alias, key, password, certChain);

		if (file.startsWith("classpath:")) {
			keystore.store(
					new FileOutputStream(new File(Resources.getResource(file.replace("classpath:", "")).getPath())),
					password);
		} else {
			keystore.store(new FileOutputStream(file), password);
		}
	}
}
