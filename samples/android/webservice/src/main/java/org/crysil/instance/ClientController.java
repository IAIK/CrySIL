package org.crysil.instance;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.crysil.instance.datastore.DeviceRegistration;
import org.crysil.instance.datastore.DeviceRepository;
import org.crysil.instance.datastore.ServerInfo;
import org.crysil.instance.util.Constants;
import org.crysil.instance.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Handles messages incoming from a CrySIL client
 */
@RestController
public class ClientController {

	private static Logger logger = LoggerFactory.getLogger(ClientController.class);

	@Autowired
	private AppConfiguration config;

	@Autowired
	private DeviceRepository repository;

	/**
	 * Processes a request from a CrySIL client for a specific CrySIL Android server (identified by
	 * <code>crysilId</code>). If we do not have an open WebSocket connection, we send a token over GCM to the server,
	 * so that it will open a connection to us.
	 * 
	 * @see #forwardToCrysilServer(String, Long, HttpServletRequest)
	 * @return Response from the CrySIL Android server (received over its WebSocket connection to us)
	 */
	@RequestMapping(value = Constants.API_CRYSIL_CLIENT, method = RequestMethod.POST, produces = Constants.APPLICATION_JSON, consumes = Constants.APPLICATION_JSON)
	public @ResponseBody String crysil(@RequestBody String crysilRequest,
			@RequestParam(value = Constants.PARAM_ID) Long crysilId, HttpServletRequest request) {
		return forwardToCrysilServer(crysilRequest, crysilId, request);
		// return JsonUtils.fromJson(responseJson, Response.class);
	}

	/**
	 * Processes a request from a U2F client for a specific CrySIL Android server (identified by <code>crysilId</code>).
	 * If we do not have an open WebSocket connection, we send a token over GCM to the server, so that it will open a
	 * connection to us.
	 *
	 * Marked with {@link CrossOrigin} to allow the Chrome extension to forward requests to this server
	 *
	 * @see #forwardToCrysilServer(String, Long, HttpServletRequest)
	 * @return Response from the CrySIL Android server (received over its WebSocket connection to us)
	 */
	@CrossOrigin
	@RequestMapping(value = Constants.API_U2F, method = RequestMethod.POST, produces = Constants.APPLICATION_JSON, consumes = Constants.APPLICATION_JSON)
	public @ResponseBody String u2f(@RequestBody String u2fRequest,
			@RequestParam(value = Constants.PARAM_ID) Long crysilId, HttpServletRequest request) {
		return forwardToCrysilServer(u2fRequest, crysilId, request);
	}

	/**
	 * Forwards a request form a client to a specific CrySIL Android server (identified by <code>crysilId</code>). If we
	 * do not have an open WebSocket connection, we send a token over GCM to the server, so that it will open a
	 * connection to us.
	 *
	 * @return Response from the CrySIL Android server (received over its WebSocket connection to us)
	 */
	private String forwardToCrysilServer(String requestJson, Long crysilId, HttpServletRequest request) {
		logger.debug("New request for CrySILId {}", crysilId);
		ServerInfo serverInfo = null;
		String token = null;

		// checking reusable connections
		String reusableToken = WebSocketManager.getInstance().getReusableConnectionToken(crysilId);
		while (serverInfo == null && reusableToken != null) {
			serverInfo = WebSocketManager.getInstance().getMapping(reusableToken);
			token = reusableToken;
			reusableToken = WebSocketManager.getInstance().getReusableConnectionToken(crysilId);
		}

		DeviceRegistration reg = repository.findOne(crysilId);
		if (reg == null) {
			logger.error("DeviceId for CrySILId {} not found", crysilId);
			return null;
		}
		if (!reg.getActive()) {
			logger.debug("Server with CrySILId {} is paused", crysilId);
			return null;
		}

		// no reusable connection - creating a new one
		if (serverInfo == null) {
			token = UUID.randomUUID().toString();
			logger.debug("Creating new channel with token {}", token);
			try {
				String url = config.getLocalUrl();
				if (url.isEmpty())
					url = String.format("%s:%d", request.getLocalAddr(), request.getLocalPort());
				if (!postToGCM(token, crysilId, reg.getDeviceId(), url)) {
					return null;
				}
			} catch (IOException e) {
				logger.error("IOException on posting to GCM", e);
				return null;
			}
			// wait for the Android server to connect to us
			serverInfo = WebSocketManager.getInstance().waitForServerInfo(token);
			if (serverInfo == null) {
				logger.error("Timeout: Android server has not connected to us, for token " + token);
				return null;
			}
		} else {
			logger.debug("Found existing channel for token {}", token);
		}

		ServerWebSocketHandler handler = serverInfo.getWebSocketHandler();
		WebSocketSession session = serverInfo.getWebSocketSession();
		String responseJson = handler.sendAndWait(session, requestJson);

		if (responseJson == null) {
			logger.error("No response from Android server with CrySILId {}", crysilId);
			return null;
		}
		WebSocketManager.getInstance().addReusableConnectionToken(crysilId, token);
		return responseJson;
	}

	/**
	 * Sends a message containing the URL of our WebSocket endpoint to the CrySIL Android server identified by
	 * <code>crysilId</code> and <code>deviceId</code>. The URL is build by using the values from the request of the
	 * CrySIL client.
	 *
	 * @param deviceId
	 *            Uniquely identifies the device for GCM
	 * @param crysilId
	 *            Our internal ID for the Android server
	 * @param host
	 *            Our host name, to build the WebSocket URI
	 * @param post
	 *            The port we are running on, to build the WebSocket URI
	 * @return <b>true</b> on success
	 */
	private boolean postToGCM(String token, Long crysilId, String deviceId, String url) throws IOException {
		String path = String.format("%s%s?token=%s", url, Constants.API_CRYSIL_SERVER, token);
		logger.debug("GCM: Sending path {} to device {}", path, StringUtils.cutDown(deviceId, 32));
		Message message = new Message.Builder().timeToLive(100).addData(Constants.PARAM_URI, path).build();
		Sender sender = new Sender(config.getGcmServerKey());
		Result result = sender.sendNoRetry(message, deviceId);

		String messageId = result.getMessageId();
		if (messageId != null) {
			// deviceId might has changed, so update it in our datastore
			String newDeviceId = result.getCanonicalRegistrationId();
			DeviceRegistration reg = repository.findOne(crysilId);
			if (newDeviceId != null && reg != null) {
				// TODO test this update
				reg.setDeviceId(newDeviceId);
				repository.save(reg);
			}
			return true;
		} else {
			logger.error("Error from GCM is {}", result.getErrorCodeName());
			return false;
		}
	}
}
