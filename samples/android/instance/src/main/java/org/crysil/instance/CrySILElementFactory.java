package org.crysil.instance;

import java.security.KeyStore;

import org.crysil.actor.u2f.U2FActivityHandler;
import org.crysil.actor.u2f.U2FAndroidActor;
import org.crysil.commons.Interlink;
import org.crysil.commons.OneToManyInterlink;
import org.crysil.communications.u2f.counter.DefaultU2FCounterStore;
import org.crysil.communications.u2f.counter.U2FCounterStore;
import org.crysil.communications.websocket.WebSocketReceiver;
import org.crysil.communications.websocket.interfaces.ActorChooser;
import org.crysil.communications.websocket.interfaces.CertificateCallback;

public class CrySILElementFactory {

	private static OneToManyInterlink receiver;

	private CrySILElementFactory() {
	}

	public static void initialize(KeyStore keyStore, KeyStore trustStore, String keystoreProvider, String keystoreType,
			U2FActivityHandler u2fActivityHandler, ActorChooser actorChooser, CertificateCallback certificateCallback) {
		U2FAndroidActor actorU2f = new U2FAndroidActor(u2fActivityHandler);
		U2FCounterStore counterStore = new DefaultU2FCounterStore();
		receiver = new WebSocketReceiver(keyStore, null, trustStore, actorChooser, certificateCallback, counterStore);
		receiver.attach(actorU2f);
	}

	public static Interlink getReceiver() {
		return receiver;
	}
}
