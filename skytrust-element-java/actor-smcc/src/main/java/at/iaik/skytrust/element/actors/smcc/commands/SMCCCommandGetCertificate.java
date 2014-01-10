package at.iaik.skytrust.element.actors.smcc.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.smcc.SmartCardProvider;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPacket;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPayload;
import iaik.utils.Util;
import iaik.x509.X509Certificate;

import java.security.cert.CertificateEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 5/22/13
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class SMCCCommandGetCertificate extends BasicSMCCCommand {

    public SMCCCommandGetCertificate(SmartCardProvider apduCitizenCard) {
        _smartCardProvider = apduCitizenCard;
    }

    @Override
    protected void handleCommand(CMDPacket requestCmdPacket, CMDPacket responseCmdPacket) {
        CMDPayload responsePayload = responseCmdPacket.getPayload();
        if(requestCmdPacket.getPayload().getKey().getId() != null) {
            X509Certificate cert = _smartCardProvider.getCertificate(requestCmdPacket.getPayload().getKey());
            //X509Certificate cert = null;
            if(cert != null){
                responsePayload.setCode(200);
                try {
                    responsePayload.setLoad(Util.toBase64String(cert.getEncoded()));
                } catch (CertificateEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else {
                responsePayload.setCode(400);
            }
        } else {
            responsePayload.setCode(400);
        }
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.getCertificate;
    }

}
