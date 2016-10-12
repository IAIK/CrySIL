package org.crysil.instance.jce_receiver_demo.model;

import java.io.File;
import java.security.Key;
import java.security.Provider;

import org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider;

/**
 * Holds all the data necessary for performing the encryption/decryption.
 */
public class Data {

	private byte[] fileContent;
	private Key key;
	private Provider provider;
	private byte[] result;
	private File sourceFile;

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setProvider(CrysilProvider crysilProvider) {
		this.provider = crysilProvider;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}

	public byte[] getResult() {
		return result;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}
}
