package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.iaikjce.IaikJceActor;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyCertificate;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyHandle;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyMetaInformation;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.status.SPayloadStatus;
import at.iaik.skytrust.keystorage.rest.client.moveaway.JCEKeyAndCertificate;
import iaik.utils.Util;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;

public class IaikJceCommandDiscoverKeys extends IaikJceCommandBasic {

    public IaikJceCommandDiscoverKeys(IaikJceActor actor) {
        super(actor);
    }

    @Override
    protected void handleCommand(SRequest requestCmdPacket,
                                 SResponse responseCmdPacket) {

        String sessionId = requestCmdPacket.getHeader().getSessionId();
        String userId = sessionManager.getSession(sessionId).getUser().getUserId();

        SPayloadDiscoverKeysRequest discoverKeysRequest = (SPayloadDiscoverKeysRequest) requestCmdPacket.getPayload();
        String representation = discoverKeysRequest.getRepresentation();

        List<SKey> keys = new ArrayList();
        List<JCEKeyAndCertificate> keyAndCertificates = restKeyStorage.getKeys(userId);
        for (JCEKeyAndCertificate keyAndCertificate:keyAndCertificates) {
            SKey key = null;

            if ("certificate".equals(representation)) {
                key = new SKeyCertificate();
                String encodedCertificate = null;
                try {
                    encodedCertificate = Util.toBase64String(keyAndCertificate.getX509Certificate().getEncoded());
                    encodedCertificate = cleanResponseLoad(encodedCertificate);
                    ((SKeyCertificate) key).setEncodedCertificate(encodedCertificate);

                    List<String> locations = keyAndCertificate.getKeyStorageLocations();
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
                    ((SKeyCertificate) key).setMetaInformation(metaInformationList);
                } catch (CertificateEncodingException e) {
                    logger.error("Could not encode certificate", e);
                }
            } else if ("handle".equals(representation)) {
                key = new SKeyHandle();
                List<String> locations = keyAndCertificate.getKeyStorageLocations();
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
                ((SKeyHandle)key).setMetaInformation(metaInformationList);

            }
            if (key!=null) {
                key.setId(keyAndCertificate.getSkyTrustKeyId());
                key.setSubId(keyAndCertificate.getSkyTrustSubKeyId());
                keys.add(key);
            }
        }
        if (keys.size()==0) {
            SPayloadStatus status = new SPayloadStatus();
            status.setCode(400);
            responseCmdPacket.setPayload(status);
        } else {
            SPayloadDiscoverKeysResponse discoverKeysResponse = new SPayloadDiscoverKeysResponse();
            discoverKeysResponse.setKey(keys);
            responseCmdPacket.setPayload(discoverKeysResponse);
        }

    }

    @Override
    public CmdType getCommandType () {
        return CmdType.discoverKeys;
    }
}
