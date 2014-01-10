package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.iaikjce.IaikJceActor;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyCertificate;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyMetaInformation;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadGetKeyRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadGetKeyResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.status.SPayloadStatus;
import at.iaik.skytrust.keystorage.rest.client.moveaway.JCEKeyAndCertificate;
import iaik.utils.Util;
import iaik.x509.X509Certificate;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;

public class IaikJceCommandGetKey extends IaikJceCommandBasic {

    public IaikJceCommandGetKey(IaikJceActor actor) {
        super(actor);
    }

    @Override
    protected void handleCommand(SRequest skyTrustRequest, SResponse skyTrustResponse) {
        SPayloadRequest requestPayload = skyTrustRequest.getPayload();
        String sessionId = skyTrustResponse.getHeader().getSessionId();
        String userId = sessionManager.getSession(sessionId).getUser().getUserId();

        if (requestPayload instanceof SPayloadGetKeyRequest) {
            SPayloadGetKeyRequest getKeyRequest = (SPayloadGetKeyRequest) requestPayload;
            String representation = getKeyRequest.getRepresentation();
            SKey keyHandle = (SKey) getKeyRequest.getKey();

            String keyId = getKeyId(keyHandle.getId());
            String subKeyId = keyHandle.getSubId();

            //TODO subKeyId for storage!!
            JCEKeyAndCertificate storedKey = restKeyStorage.getKey(userId,keyId,subKeyId);


            SPayloadGetKeyResponse responsePayload = new SPayloadGetKeyResponse();
            if ("certificate".equals(representation)) {
                X509Certificate certificate = storedKey.getX509Certificate();
                if (certificate != null) {
                    String encodedCertificate = null;
                    try {
                        encodedCertificate = Util.toBase64String(certificate.getEncoded());
                        encodedCertificate = cleanResponseLoad(encodedCertificate);

                        List<String> locations = storedKey.getKeyStorageLocations();
                        String locationString = "";
                        for (int i=0;i<locations.size();i++) {
                            locationString+=locations.get(i);
                            if (i<(locations.size()-1)) {
                                locationString+=",";
                            }
                        }

                        List<SKeyMetaInformation> metaInformationList = new ArrayList();
                        SKeyMetaInformation metaInformation = new SKeyMetaInformation();
                        metaInformation.setGenericMetaInformation(locationString);
                        metaInformationList.add(metaInformation);

                        SKeyCertificate certificateRep = new SKeyCertificate();
                        certificateRep.setEncodedCertificate(encodedCertificate);
                        certificateRep.setRepresentation("certificate");
                        certificateRep.setId(keyHandle.getId());
                        certificateRep.setSubId(storedKey.getSkyTrustSubKeyId());
                        certificateRep.setMetaInformation(metaInformationList);
                        responsePayload.setKey(certificateRep);

                        skyTrustResponse.setPayload(responsePayload);
                        return;
                    } catch (CertificateEncodingException e) {
                        logger.error("Could not encode certificate", e);
                    }
                }
            }
        }
        SPayloadStatus status = new SPayloadStatus();
        status.setCode(400);
        skyTrustResponse.setPayload(status);
        return;
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.getKey;
    }

}
