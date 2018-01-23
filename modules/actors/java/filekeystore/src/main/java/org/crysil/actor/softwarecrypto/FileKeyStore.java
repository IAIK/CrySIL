package org.crysil.actor.softwarecrypto;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

public class FileKeyStore implements SoftwareCryptoKeyStore {
	private char[] password;
	protected KeyStore keystore = null;
    private String file;
    private String keyStoreType = "JKS";

	protected FileKeyStore() {

	}

	public FileKeyStore(String file, char[] password, String type) throws KeyStoreUnavailableException {
		this.file = file;
		this.password = password;
		this.keyStoreType = type;

		try {
			keystore = KeyStore.getInstance(keyStoreType);

			if (this.file.startsWith("classpath:")) {
				keystore.load(FileKeyStore.class.getResourceAsStream(file.replace("classpath:", "")), password);
			} else {
				keystore.load(new FileInputStream(file), password);
			}
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
			return keystore.getKey(keyHandle.getId() + keyHandle.getSubId(), password);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new KeyNotFoundException();
		}
	}

	@Override
	public java.security.cert.X509Certificate getX509Certificate(KeyHandle keyHandle) {
		try {
			return (java.security.cert.X509Certificate) keystore
					.getCertificate(keyHandle.getId() + keyHandle.getSubId());
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
		try {
			List<String> keys = Collections.list(keystore.aliases());

			for (String currentKey : keys) {
				// id/subid
				// id, id/, id/#xxxx - use cert hash for subid
				String[] rawids = currentKey.split("/", 2);
				String id = rawids[0];
				String subid = "";
				if (Array.getLength(rawids) >= 2)
					subid = rawids[1];

				KeyHandle tmp = new KeyHandle();
				tmp.setId(id);
				tmp.setSubId(subid);

				result.add(tmp);
			}
		} catch (KeyStoreException e) {
			// well, we were not able to retrieve any key
			System.err.println(e.getMessage());
		}

		return result;
	}

	@Override
	public void addFilter(Header header) {
		// TODO Auto-generated method stub
	}
}
