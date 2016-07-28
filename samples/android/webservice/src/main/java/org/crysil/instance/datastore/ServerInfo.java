package org.crysil.instance.datastore;

import org.crysil.instance.ServerWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

/**
 * Holds information about a client connected to the {@link org.crysil.instance.ServerWebSocketHandler}
 */
public class ServerInfo {

	private final String token;
	private final WebSocketSession session;
	private final ServerWebSocketHandler handler;

	public ServerInfo(String token, WebSocketSession session, ServerWebSocketHandler handler) {
		this.token = token;
		this.session = session;
		this.handler = handler;
	}

	/**
	 * Token with that the CrySIL server has connected (that we have sent over GCM)
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Session to the client, check if it {@link org.springframework.web.socket.WebSocketSession#isOpen() is open}!
	 */
	public WebSocketSession getWebSocketSession() {
		return session;
	}

	/**
	 * Handler for local side of connection (to the server)
	 */
	public ServerWebSocketHandler getWebSocketHandler() {
		return handler;
	}
}