package org.crysil.instance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.crysil.instance.datastore.CertificatePrincipal;
import org.crysil.instance.datastore.ServerInfo;
import org.crysil.instance.util.ConditionLock;
import org.crysil.instance.util.Constants;
import org.crysil.instance.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket endpoint for a connection to an CrySIL Android server. <br />
 * This class is the same for all WebSocket connections, but there is a own {@link WebSocketSession} for each connected
 * Android server!
 */
@Controller
public class ServerWebSocketHandler extends TextWebSocketHandler {

	private static Logger logger = LoggerFactory.getLogger(ServerWebSocketHandler.class);

	private Map<WebSocketSession, ConditionLock> responseReady = new HashMap<WebSocketSession, ConditionLock>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		if (responseReady.containsKey(session)) {
			ConditionLock conditionLock = responseReady.get(session);
			conditionLock.getLock().lock();
			conditionLock.setResponse(message.getPayload());
			conditionLock.getReady().signalAll();
			conditionLock.getLock().unlock();
		} else {
			logger.error("We have received an unwanted message for session " + session.getId());
			session.close();
			return;
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.error("Transport error ", exception);
		session.close(CloseStatus.SERVER_ERROR);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if (!session.getAttributes().containsKey(Constants.PARAM_TOKEN)) {
			logger.error("No token in established WebSocket session, closing!");
			session.close();
			return;
		}
		if (session.getPrincipal() instanceof CertificatePrincipal) {
			CertificatePrincipal certificatePrincipal = (CertificatePrincipal) session.getPrincipal();
			if (certificatePrincipal.hasCertificate()) {
				logger.debug("Got client TLS certificate from '{}': permit", certificatePrincipal.getName());
			} else {
				logger.error("No client TLS certificate given, returning");
				session.close();
				return;
			}
		} else {
			logger.error("Wait, we dont know how to handle given Principal '{}', returning", session.getPrincipal());
			session.close();
			return;
		}
		String token = session.getAttributes().get(Constants.PARAM_TOKEN).toString();
		logger.debug("Opening WebSocket for token {}", token);
		WebSocketManager.getInstance().addMapping(token, new ServerInfo(token, session, this));

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		if (!session.getAttributes().containsKey(Constants.PARAM_TOKEN)) {
			return;
		}
		String token = session.getAttributes().get(Constants.PARAM_TOKEN).toString();
		logger.debug("Closing WebSocket for token {}, status is {}", token, status);
		WebSocketManager.getInstance().removeMapping(token);
	}

	/**
	 * Sends a request from a CrySIL client to a connected CrySIL server over a WebSocket. <br />
	 * Note that the WebSocket session must already exist (it was most probably initiated by the Server as it has
	 * received a push notification over GCM)
	 * 
	 * @return The response from the server, or <b>null</b> if we ran into problems
	 */
	public String sendAndWait(WebSocketSession session, String request) {
		logger.debug("Sending message {} to {}", StringUtils.cutDown(request, 16), session);
		responseReady.put(session, new ConditionLock());
		try {
			session.sendMessage(new TextMessage(request));
		} catch (IOException e) {
			logger.error("IOException on sendAndWait", e);
			return null;
		}
		String response = null;
		responseReady.get(session).getLock().lock();
		try {
			responseReady.get(session).getReady().await(60, TimeUnit.SECONDS);
			response = responseReady.get(session).getResponse();
			logger.debug("Got response: {}", StringUtils.cutDown(response, 16));
		} catch (InterruptedException e) {
			logger.error("InterruptedException on sendAndWait", e);
		}
		responseReady.get(session).getLock().unlock();
		responseReady.remove(session);
		return response != null ? new String(response) : null;
	}
}
