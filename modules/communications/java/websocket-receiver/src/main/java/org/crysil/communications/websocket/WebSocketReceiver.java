package org.crysil.communications.websocket;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import org.crysil.commons.OneToOneInterlink;
import org.crysil.communications.json.JsonUtils;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketReceiver extends OneToOneInterlink implements WebSocketListener {

	private final KeyStore keyStore;
	private final char[] password;
	private final KeyStore trustStore;

	public WebSocketReceiver(KeyStore keyStore, char[] password, KeyStore trustStore) {
		this.keyStore = keyStore;
		this.password = password;
		this.trustStore = trustStore;
	}

	public boolean connect(String uriString, String alias) {
		WebsocketKeyManagerFactory keyManagerFactory = null;
		if (alias != null) {
			keyManagerFactory = new WebsocketKeyManagerFactory(new KeyStoreInterface() {
				@Override
				public X509Certificate getCertificate(String alias) {
					try {
						return (X509Certificate) keyStore.getCertificate(alias);
					} catch (KeyStoreException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				public PrivateKey getKey(String alias) {
					try {
						return (PrivateKey) keyStore.getKey(alias, password);
					} catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
						e.printStackTrace();
					}
					return null;
				}
			}, alias);
		}

		ForwardingWebSocket client = new ForwardingWebSocket(uriString, this, new WebsocketTrustManagerFactory(),
				keyManagerFactory);
		client.start();
		return true;
	}

	@Override
	public void onMessage(Channel websocketChannel, String msg) {
		Request request = JsonUtils.fromJson(msg, Request.class);
		if (request != null) {
			Response response;
			try {
				response = getAttachedModule().take(request);
				String responseString = JsonUtils.toJson(response);
				websocketChannel.writeAndFlush(new TextWebSocketFrame(responseString));
			} catch (CrySILException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClose(Channel websocketChannel) {
	}

	@Override
	public void onConnect(Channel websocketChannel) {
	}
}
