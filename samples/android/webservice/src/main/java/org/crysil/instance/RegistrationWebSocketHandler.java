package org.crysil.instance;

import java.security.cert.X509Certificate;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.crysil.communications.json.JsonUtils;
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
 * Handles registration of an CrySIL Android server: It gets assigned a new CrySIL ID, it answers with a certificate
 * that we will sign.
 * 
 * @see ManagementWebSocketHandler
 */
@Controller
public class RegistrationWebSocketHandler extends TextWebSocketHandler {

	private static Logger logger = LoggerFactory.getLogger(RegistrationWebSocketHandler.class);

	@Autowired
	private AppConfiguration config;

	@Autowired
	private DeviceRepository repository;

	@Autowired
	private CertificateUtil certificateUtil;

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		if (!session.getAttributes().containsKey(Constants.PARAM_GCM)) {
			logger.error("We have received an unwanted message for session " + session.getId());
			session.close();
			return;
		}

		String deviceId = session.getAttributes().get(Constants.PARAM_GCM).toString();
		logger.debug("Got message '{}'", StringUtils.cutDown(message.getPayload(), 32));
		SimpleMessage msg = JsonUtils.fromJson(message.getPayload(), SimpleMessage.class);

		if (msg.getHeader().equalsIgnoreCase(Constants.MSG_REGISTER)) {
			String msgDeviceId = msg.getPayload();
			DeviceRegistration deviceRegistration = repository.findByDeviceId(msgDeviceId);
			if (deviceRegistration == null) {
				deviceRegistration = new DeviceRegistration(msgDeviceId);
				repository.save(deviceRegistration);
			}
			Long crysilId = deviceRegistration.getCrysilId();
			logger.debug("Answering start from device {} with crysilId {}", StringUtils.cutDown(deviceId, 16), crysilId);
			session.sendMessage(new TextMessage(JsonUtils.toJson(new SimpleMessage(Constants.MSG_REGISTER, Long
					.toString(crysilId)))));
		} else if (msg.getHeader().equalsIgnoreCase(Constants.MSG_CSR)) {
			logger.debug("Handling certificate signing request from device {}", StringUtils.cutDown(deviceId, 16));
			DeviceRegistration deviceRegistration = repository.findByDeviceId(deviceId);
			if (deviceRegistration == null) {
				logger.warn("Device {} sent a CSR, but we don't know the device", StringUtils.cutDown(deviceId, 16));
				session.close();
				return;
			}
			Long crysilId = deviceRegistration.getCrysilId();
			byte[] csrBytes = Base64.decodeBase64(msg.getPayload());
			try {
				PKCS10CertificationRequest csr = new PKCS10CertificationRequest(csrBytes);
				X509Certificate signedCertificate = certificateUtil.signCertificate(csr, crysilId);
				deviceRegistration.setActive(true);
				repository.save(deviceRegistration);
				logger.debug("Signed the certificate for crysilId {}", crysilId);
				byte[] signedEncoded = signedCertificate.getEncoded();
				String signedBase64 = Base64.encodeBase64String(signedEncoded);
				session.sendMessage(new TextMessage(JsonUtils
						.toJson(new SimpleMessage(Constants.MSG_CSR, signedBase64))));
				session.close(CloseStatus.NORMAL);
			} catch (Exception e) {
				logger.error("Could not sign the CSR", e);
				repository.delete(deviceRegistration);
				session.close();
				return;
			}
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
