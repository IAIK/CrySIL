package at.iaik.skytrust.element.actors.common;

import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;

public abstract class BasicCommand {


    public SResponse handle(SRequest skyTrustRequest) {
        //create response cmd packet
        SResponse skyTrustResponse = new SResponse();
        skyTrustResponse.setHeader(new SkyTrustHeader());
        skyTrustResponse.getHeader().setProtocolVersion("0.1");
        skyTrustResponse.getHeader().setCommandId(skyTrustRequest.getHeader().getCommandId());
        skyTrustResponse.getHeader().setSessionId(skyTrustRequest.getHeader().getSessionId());

        //handle respective command
        handleCommand(skyTrustRequest, skyTrustResponse);

        //return result
        return skyTrustResponse;
    }

    public String cleanResponseLoad(String response) {
        return response.replace("\r", "").replace("\n", "");
    }

    protected abstract void handleCommand(SRequest skyTrustRequest, SResponse skyTrustResponse);

    public abstract CmdType getCommandType();

}
