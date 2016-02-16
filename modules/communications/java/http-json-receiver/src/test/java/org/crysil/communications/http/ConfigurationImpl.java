package org.crysil.communications.http;

import org.crysil.actor.staticKeyEncryption.StaticKeyEncryptionActor;
import org.crysil.commons.Module;
import org.crysil.communications.http.Configuration;

public class ConfigurationImpl implements Configuration {

	@Override
	public Module getAttachedModule() {
		return new StaticKeyEncryptionActor();
	}

	@Override
	public boolean isValidateSchema() {
		return true;
	}

}
