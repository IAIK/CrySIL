package org.crysil.config;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.crysil.actor.softwarecrypto.SimpleKeyStore;
import org.crysil.actor.softwarecrypto.SoftwareCrypto;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.AutomaticAuthSelector;
import org.crysil.authentication.auth_android.authplugins.Auth1ClickConfirmation;
import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.commons.Interlink;
import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.communications.websocket.WebSocketReceiver;
import org.crysil.gatekeeper.Configuration;
import org.crysil.gatekeeper.Gatekeeper;


public class CrySILElementFactory {
	private static OneToOneInterlink receiver;

	private CrySILElementFactory() {
	}

	public static Interlink getReceiver() {
		return receiver;
	}

	public static void initialize(KeyStore keyStore, KeyStore trustStore) {

		try {
			Module jceActor = new SoftwareCrypto(new SimpleKeyStore());

			receiver = new WebSocketReceiver(keyStore, null, trustStore);

			// Reponse interceptor for 1-click confirmation on android server
			// device
			InterceptorAuth<AutomaticAuthSelector> interceptor = new InterceptorAuth<>(AutomaticAuthSelector.class);
			final List<AuthHandlerFactory<?, ?, ?>> authPluginFactories = new ArrayList<>();
			authPluginFactories.add(new Auth1ClickConfirmation.Factory(Auth1ClickConfirmation.class));
			interceptor.setAuthenticationPlugins(authPluginFactories);
			receiver.attach(interceptor);

			Configuration conf = new Config();
			Gatekeeper gatekeeper = new Gatekeeper(conf);

			interceptor.attach(gatekeeper);

			gatekeeper.attach(jceActor);
		} catch (Exception e) {

		}
	}
}
