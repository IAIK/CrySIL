package org.crysil.communications.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import org.crysil.commons.Module;
import org.crysil.commons.OneToManyInterlink;
import org.crysil.communications.u2f.U2FReceiverHandler;
import org.crysil.communications.u2f.U2FReceiverInterface;
import org.crysil.communications.websocket.interfaces.ActionPerformedCallback;
import org.crysil.communications.websocket.interfaces.ActorChooser;
import org.crysil.communications.websocket.interfaces.CertificateCallback;
import org.crysil.communications.websocket.interfaces.WebSocketListener;
import org.crysil.communications.websocket.ssl.WebsocketKeyManagerFactory;
import org.crysil.communications.websocket.ssl.WebsocketKeyStore;
import org.crysil.communications.websocket.ssl.WebsocketTrustManagerFactory;
import org.crysil.errorhandling.CrySILException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.u2f.U2FCounterStore;

public class WebSocketReceiver extends OneToManyInterlink implements WebSocketListener, U2FReceiverInterface {

	private final KeyStore keyStore;
	private final char[] password;
	private final KeyStore trustStore;
	private final U2FReceiverHandler handler;
	private final ActorChooser actorChooser;
	private final CertificateCallback certificateCallback;

	public WebSocketReceiver(KeyStore keyStore, char[] password, KeyStore trustStore, ActorChooser actorChooser,
			CertificateCallback certificateCallback, U2FCounterStore counterStore) {
		super();
		this.keyStore = keyStore;
		this.password = password;
		this.trustStore = trustStore;
		this.handler = new U2FReceiverHandler(counterStore);
		this.actorChooser = actorChooser;
		this.certificateCallback = certificateCallback;
	}

	public boolean connect(String uriString, String alias) {
		WebsocketKeyManagerFactory keyManagerFactory = null;
		if (alias != null) {
			keyManagerFactory = new WebsocketKeyManagerFactory(new WebsocketKeyStore(keyStore, password), alias);
		}

		ForwardingWebSocket client = new ForwardingWebSocket(uriString, this, new WebsocketTrustManagerFactory(
				certificateCallback, trustStore), keyManagerFactory);
		client.start();
		return true;
	}

	/**
	 * We only want to get one actor at a time, to give the user the time needed to select it
	 */
	@Override
	public void onMessage(Channel websocketChannel, String msg) {
		if (msg.length() > 0) {
			synchronized (handler) {
				Module actor = getActor();
				if (actor == null) {
					return;
				}
				String responseString = handler.handleMessage(msg, actor, this);
				websocketChannel.writeAndFlush(new TextWebSocketFrame(responseString));
			}
		}
	}

	@Override
	public Response forwardRequest(Request request, Module actor) {
		try {
			return actor.take(request);
		} catch (CrySILException e) {
			Logger.error("Unsupported request", e);
			return null;
		}
	}

	private Module getActor() {
		final Module[] actor = new Module[1];
		final ManualResetEvent sync = new ManualResetEvent(false);
		Map<String, Module> moduleMap = new HashMap<>();
		for (Module module : getAttachedModules()) {
			moduleMap.put(module.getClass().getSimpleName(), module);
		}
		actorChooser.chooseActor(moduleMap, new ActionPerformedCallback() {
			@Override
			public void actionPerformed(Module actorChosen) {
				actor[0] = actorChosen;
				sync.set();
			}
		});
		try {
			sync.waitOne();
		} catch (InterruptedException e) {
			return null;
		}
		if (actor[0] == null) {
			return null;
		}
		return actor[0];
	}

	@Override
	public void onClose(Channel websocketChannel) {
	}

	@Override
	public void onConnect(Channel websocketChannel) {
	}
}
