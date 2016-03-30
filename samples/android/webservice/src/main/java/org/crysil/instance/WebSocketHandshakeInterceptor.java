package org.crysil.instance;

import java.util.List;
import java.util.Map;

import org.crysil.instance.util.Constants;
import org.crysil.instance.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Intercepts the handshake of an WebSocket client (the CrySIL Android server) to our WebSocket handler. Simply converts
 * all URL parameters into session attributes, this should include e.g. {@link Constants#PARAM_TOKEN}
 * 
 * @see ServerWebSocketHandler
 */
public final class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

	private static Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			java.util.Map<java.lang.String, java.lang.Object> attributes) {
		logger.debug("Before WS handshake, URI is {}", StringUtils.cutDown(request.getURI().toString(), 64));
		UriComponents components = UriComponentsBuilder.fromUri(request.getURI()).build();
		for (Map.Entry<String, List<String>> entry : components.getQueryParams().entrySet()) {
			String val = entry.getValue().iterator().next();
			attributes.put(entry.getKey(), val);
		}
		return true;
	}
}