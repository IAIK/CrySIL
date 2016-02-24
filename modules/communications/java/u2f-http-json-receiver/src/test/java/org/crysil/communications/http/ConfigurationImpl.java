package org.crysil.communications.http;

import org.crysil.actor.smcc.SmccActor;
import org.crysil.actor.smcc.strategy.YubicoRandomKeyHandleStrategy;
import org.crysil.commons.Module;
import org.crysil.communications.http.Configuration;

public class ConfigurationImpl implements Configuration {

	@Override
	public Module getAttachedModule() {
		return new SmccActor(new YubicoRandomKeyHandleStrategy());
	}

	@Override
	public boolean isValidateSchema() {
		return true;
	}

}
