package at.iaik.skytrust.element.actors.apdu;

import iaik.utils.Base64Exception;
import iaik.utils.Util;
import at.iaik.skytrust.element.actors.common.BasicCommand;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPacket;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPayload;

public class APDUCommandTransmit extends BasicCommand {
    protected APDUActor _smartCardProvider;

    public APDUCommandTransmit(APDUActor smartCardProvider) {
        _smartCardProvider = smartCardProvider;
    }

    @Override
    protected void handleCommand(CMDPacket cmdPacket, CMDPacket responseCmdPacket) {
        CMDPayload responsePayload = responseCmdPacket.getPayload();
        responsePayload.setCode(400);

        if (cmdPacket.getPayload().getLoad() != null) {
            byte[] resp = null;

            try {
                byte[] load = Util.fromBase64String(cmdPacket.getPayload().getLoad());
                resp = _smartCardProvider.transmit(load);
            } catch (Base64Exception e) {
                System.err.println("Error while Base64 decoding!");
                return;
            }

            if (resp != null) {
                responsePayload.setCode(200);
                responsePayload.setLoad(cleanResponseLoad(Util.toBase64String(resp)));
            }
        }
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.transmitAPDU;
    }
}