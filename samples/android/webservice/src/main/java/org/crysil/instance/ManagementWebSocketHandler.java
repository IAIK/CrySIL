package org.crysil.instance;

import org.crysil.communications.json.JsonUtils;
import org.crysil.instance.datastore.CertificatePrincipal;
import org.crysil.instance.datastore.DeviceRegistration;
import org.crysil.instance.datastore.DeviceRepository;
import org.crysil.instance.util.CertificateUtil;
import org.crysil.instance.util.Constants;
import org.crysil.instance.util.SimpleMessage;
import org.crysil.instance.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handles the management messages of a CrySIL Android server: It can pause and resume its mapping (to temporarily
 * disable receiving client requests), or unregister. The Android server needs to prove its identity by sending a
 * certificate over the TLS connection that we have signed before.
 */
@Controller
public class ManagementWebSocketHandler extends TextWebSocketHandler {

	private static Logger logger = LoggerFactory.getLogger(ManagementWebSocketHandler.class);

	@Autowired
	private AppConfiguration config;

	@Autowired
	private DeviceRepository repository;

	@Autowired
	private CertificateUtil certificateUtil;

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String deviceId = session.getAttributes().get(Constants.PARAM_GCM).toString();
		logger.debug("Got message '{}'", StringUtils.cutDown(message.getPayload(), 32));
		SimpleMessage msg = JsonUtils.fromJson(message.getPayload(), SimpleMessage.class);

		if (msg.getHeader().equalsIgnoreCase(Constants.MSG_UNREGISTER)) {
			logger.debug("Handling unregistering request from device {}", StringUtils.cutDown(deviceId, 16));
			DeviceRegistration deviceRegistration = repository.findByDeviceId(deviceId);
			if (deviceRegistration == null) {
				logger.warn("Device {} sent a unregister request, but we don't know the device",
						StringUtils.cutDown(deviceId, 16));
				session.close();
				return;
			}
			Long crysilId = deviceRegistration.getCrysilId();
			logger.debug("Deleting registration for crysilId {}, deviceId {}", crysilId,
					StringUtils.cutDown(deviceId, 16));
			repository.delete(deviceRegistration);
			session.sendMessage(new TextMessage(JsonUtils.toJson(new SimpleMessage(Constants.MSG_UNREGISTER, crysilId
					.toString()))));
			session.close(CloseStatus.NORMAL);
		} else if (msg.getHeader().equalsIgnoreCase(Constants.MSG_PAUSE)) {
			logger.debug("Handling pause request from device {}", StringUtils.cutDown(deviceId, 16));
			DeviceRegistration deviceRegistration = repository.findByDeviceId(deviceId);
			if (deviceRegistration == null) {
				logger.warn("Device {} sent a pause request, but we don't know the device",
						StringUtils.cutDown(deviceId, 16));
				session.close();
				return;
			}
			Long crysilId = deviceRegistration.getCrysilId();
			logger.debug("Pausing registration for crysilId {}, deviceId {}", crysilId,
					StringUtils.cutDown(deviceId, 16));
			deviceRegistration.setActive(false);
			repository.save(deviceRegistration);
			session.sendMessage(new TextMessage(JsonUtils.toJson(new SimpleMessage(Constants.MSG_PAUSE, crysilId
					.toString()))));
			session.close(CloseStatus.NORMAL);
		} else if (msg.getHeader().equalsIgnoreCase(Constants.MSG_RESUME)) {
			logger.debug("Handling resume request from device {}", StringUtils.cutDown(deviceId, 16));
			DeviceRegistration deviceRegistration = repository.findByDeviceId(deviceId);
			if (deviceRegistration == null) {
				logger.warn("Device {} sent a resume request, but we don't know the device",
						StringUtils.cutDown(deviceId, 16));
				session.close();
				return;
			}
			Long crysilId = deviceRegistration.getCrysilId();
			logger.debug("Resuming registration for crysilId {}, deviceId {}", crysilId,
					StringUtils.cutDown(deviceId, 16));
			deviceRegistration.setActive(true);
			repository.save(deviceRegistration);
			session.sendMessage(new TextMessage(JsonUtils.toJson(new SimpleMessage(Constants.MSG_RESUME, crysilId
					.toString()))));
			session.close(CloseStatus.NORMAL);
		} else {
			logger.warn("Unknown message from device {}, closing!", StringUtils.cutDown(deviceId, 16));
			session.close();
			return;
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.error("Transport error", exception);
		session.close(CloseStatus.SERVER_ERROR);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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

		if (!session.getAttributes().containsKey(Constants.PARAM_GCM)) {
			logger.error("No GCM id in established WebSocket session, closing!");
			session.close();
			return;
		}
		String gcmId = session.getAttributes().get(Constants.PARAM_GCM).toString();
		logger.debug("Opening WebSocket for GCM id {}", StringUtils.cutDown(gcmId, 16));

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		if (!session.getAttributes().containsKey(Constants.PARAM_GCM)) {
			return;
		}
		String gcmId = session.getAttributes().get(Constants.PARAM_GCM).toString();
		logger.debug("Closing WebSocket for GCM id {}, status is {}", StringUtils.cutDown(gcmId, 16), status);
	}

}
