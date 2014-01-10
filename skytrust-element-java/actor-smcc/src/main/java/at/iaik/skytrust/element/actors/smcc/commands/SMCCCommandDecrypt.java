package at.iaik.skytrust.element.actors.smcc.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.smcc.SmartCardProvider;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPacket;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPayload;
import iaik.utils.Base64Exception;
import iaik.utils.Util;

public class SMCCCommandDecrypt extends BasicSMCCCommand {

    public SMCCCommandDecrypt(SmartCardProvider apduCitizenCard) {
        _smartCardProvider = apduCitizenCard;
    }

    @Override
    protected void handleCommand(CMDPacket requestCmdPacket, CMDPacket responseCmdPacket) {
        CMDPayload responsePayload = responseCmdPacket.getPayload();

        byte[] decrypted = null;
        if(requestCmdPacket.getPayload().getLoad() != null){
            try {
                decrypted = _smartCardProvider.decrypt(Util.fromBase64String(requestCmdPacket.getPayload().getLoad()),requestCmdPacket.getPayload().getKey().getId(),requestCmdPacket.getPayload().getAlgorithm().getName());
//                decrypted = _iaikjce.decrypt(Util.fromBase64String(decrypt.getPayload().getLoad()),
//                        decrypt.getPayload().getKey().getId(),
//                        decrypt.getPayload().getAlgorithm().getName());
            } catch (Base64Exception e) {
                System.err.println("Error while decode Load!");
            }
            if(decrypted != null){
                responsePayload.setCode(200);
                responsePayload.setLoad(Util.toBase64String(decrypted));
            }
            else {
                responsePayload.setCode(400);
            }
        } else{
            responsePayload.setCode(400);
        }
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.decrypt;
    }



}
