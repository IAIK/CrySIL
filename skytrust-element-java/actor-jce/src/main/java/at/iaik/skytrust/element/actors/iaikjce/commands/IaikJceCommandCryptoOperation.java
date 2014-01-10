package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.iaikjce.IaikJceActor;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SCryptoParams;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadCryptoOperationRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadWithLoadResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.status.SPayloadStatus;
import iaik.utils.Base64Exception;
import iaik.utils.Util;

public abstract class IaikJceCommandCryptoOperation extends IaikJceCommandBasic {

    public IaikJceCommandCryptoOperation(IaikJceActor actor) {
        super(actor);
    }


    @Override
    protected void handleCommand(SRequest skyTrustRequest, SResponse skyTrustResponse) {
        SPayloadRequest requestPayload = skyTrustRequest.getPayload();
        String sessionId = skyTrustResponse.getHeader().getSessionId();
        String userId = sessionManager.getSession(sessionId).getUser().getUserId();
        if (requestPayload instanceof SPayloadCryptoOperationRequest) {
            SPayloadWithLoadResponse responsePayload = new SPayloadWithLoadResponse();
            skyTrustResponse.setPayload(responsePayload);

            SPayloadCryptoOperationRequest payloadCryptoOperationRequest = (SPayloadCryptoOperationRequest) requestPayload;
            SCryptoParams cryptoParameters = payloadCryptoOperationRequest.getCryptoParams();
            SKey key = cryptoParameters.getKey();
            String keyId = getKeyId(key.getId());
            String subKeyId = key.getSubId();

            byte[] response = null;
            try {
                String base64load = payloadCryptoOperationRequest.getLoad();
                byte[] decoded = null;
                if (base64load != null) {
                    decoded = Util.fromBase64String(payloadCryptoOperationRequest.getLoad());
                }
                String algorithm = payloadCryptoOperationRequest.getCryptoParams().getAlgorithm();
                response = handleCryptoOperation(decoded, keyId, subKeyId, userId, algorithm);
                if (response != null) {
                    responsePayload.setLoad(cleanResponseLoad(Util.toBase64String(response)));
                    return;
                }
            } catch (Base64Exception e) {
                logger.error("Base64Encoding error");
            }
        }
        SPayloadStatus status = new SPayloadStatus();
        status.setCode(400);
        skyTrustResponse.setPayload(status);

    }

    abstract protected byte[] handleCryptoOperation(byte[] data, String keyId, String subKeyId, String userId, String algorithm);
}