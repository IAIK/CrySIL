package at.iaik.skytrust.element.actors.smcc.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.smcc.SmartCardProvider;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPacket;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPayload;
import iaik.utils.Base64Exception;
import iaik.utils.Util;

public class SMCCCommandEncrypt extends BasicSMCCCommand {

  public SMCCCommandEncrypt(SmartCardProvider smartCardProvider) {
   _smartCardProvider = smartCardProvider;
  }

    @Override
    protected void handleCommand(CMDPacket cmdPacket, CMDPacket responseCmdPacket) {
        CMDPayload responsePayload = responseCmdPacket.getPayload();

        byte[] encrypted = null;
        if (cmdPacket.getPayload().getLoad() != null) {
            System.out.println("Payloadset");
            try {
                encrypted = _smartCardProvider.encrypt(Util.fromBase64String(cmdPacket.getPayload().getLoad()),
                        cmdPacket.getPayload().getKey().getId(),
                        cmdPacket.getPayload().getAlgorithm().getName());

            } catch (Base64Exception e) {
                System.err.println("Error while decode Load!");
            }
            if(encrypted != null){
                responsePayload.setCode(200);
                responsePayload.setLoad(Util.toBase64String(encrypted));
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
        return CmdType.encrypt;
    }

}
