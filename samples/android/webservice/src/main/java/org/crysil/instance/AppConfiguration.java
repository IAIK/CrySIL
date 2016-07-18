package org.crysil.instance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Enables access to the settings from application.properties
 */
@Configuration
public class AppConfiguration {

	@Value("${gcmServerKey:null}")
	private String gcmServerKey;

	@Value("${keyFile:keystore.pkcs12}")
	private String keyFile;
	
	@Value("${keyFilePassword:null}")
	private String keyFilePassword;

	@Value("${keyFileAlias:alias}")
	private String keyFileAlias;
	
	@Value("${keyFileAliasPassword:null}")
	private String keyFileAliasPassword;
	
	public String getGcmServerKey() {
		return gcmServerKey;
	}

	public void setGcmServerKey(String gcmServerKey) {
		this.gcmServerKey = gcmServerKey;
	}

	public String getKeyFile() {
		return keyFile;
	}
	
	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}
	
	public String getKeyFilePassword() {
		return keyFilePassword;
	}
	
	public void setKeyFilePassword(String keyFilePassword) {
		this.keyFilePassword = keyFilePassword;
	}

	public String getKeyFileAlias() {
		return keyFileAlias;
	}
	
	public void setKeyFileAlias(String keyFileAlias) {
		this.keyFileAlias = keyFileAlias;
	}
	
	public String getKeyFileAliasPassword() {
		return keyFileAliasPassword;
	}
	
	public void setKeyFileAliasPassword(String keyFileAliasPassword) {
		this.keyFileAliasPassword = keyFileAliasPassword;
	}
	
}
