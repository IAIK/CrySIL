package org.crysil.instance;

import java.security.KeyStore;

import org.crysil.actor.spongycastle.KeystoreU2FCounterStore;
import org.crysil.actor.spongycastle.SpongycastleActor;
import org.crysil.actor.u2f.U2FActivityHandler;
import org.crysil.actor.u2f.U2FAndroidActor;
import org.crysil.commons.Interlink;
import org.crysil.commons.OneToManyInterlink;
import org.crysil.communications.websocket.WebSocketReceiver;
import org.crysil.communications.websocket.interfaces.ActorChooser;
import org.crysil.communications.websocket.interfaces.CertificateCallback;
import org.crysil.errorhandling.CrySILException;
import org.crysil.u2f.DefaultU2FCounterStore;
import org.crysil.u2f.U2FCounterStore;

/**
 * This gets called from the Android app to setup a CrySIL instance
 */
public class CrySILElementFactory {

	private static OneToManyInterlink receiver;

	private CrySILElementFactory() {
	}

	public static void initialize(KeyStore keyStore, KeyStore trustStore, String defaultSigningKey,
			U2FActivityHandler u2fActivityHandler, ActorChooser actorChooser, CertificateCallback certificateCallback) {
		U2FAndroidActor actorU2f = new U2FAndroidActor(u2fActivityHandler);
		SpongycastleActor actorSpongy = new SpongycastleActor(defaultSigningKey);
		U2FCounterStore counterStore = null;
		try {
			counterStore = new KeystoreU2FCounterStore(keyStore);
		} catch (CrySILException e) {
			counterStore = new DefaultU2FCounterStore();
		}
		receiver = new WebSocketReceiver(keyStore, null, trustStore, actorChooser, certificateCallback, counterStore);
		receiver.attach(actorU2f);
		receiver.attach(actorSpongy);
	}

	public static Interlink getReceiver() {
		return receiver;
	}
}
