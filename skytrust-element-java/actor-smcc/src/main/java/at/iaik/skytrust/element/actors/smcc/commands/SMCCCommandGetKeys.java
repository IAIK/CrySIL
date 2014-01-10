package at.iaik.skytrust.element.actors.smcc.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.smcc.SmartCardProvider;
import at.iaik.skytrust.element.authentication.KeyBean;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPacket;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPayload;

import java.util.List;

public class SMCCCommandGetKeys extends BasicSMCCCommand {

  public SMCCCommandGetKeys(SmartCardProvider smartCardProvider) {
    _smartCardProvider = smartCardProvider;
  }

    @Override
    protected void handleCommand(CMDPacket cmdPacket, CMDPacket responseCmdPacket) {
        CMDPayload responsePayload = responseCmdPacket.getPayload();
        List<KeyBean> keyInfo = _smartCardProvider.getAvailableKeySlots();
        if(keyInfo != null){
            for(KeyBean key : keyInfo){
                System.out.println("Key: " + key.toString());

            }
            responsePayload.setCode(200);
        }else{
            responsePayload.setCode(400);
        }
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.discoverKeys;
    }


}
