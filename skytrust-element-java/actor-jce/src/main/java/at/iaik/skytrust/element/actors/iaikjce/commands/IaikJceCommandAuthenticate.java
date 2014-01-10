package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.common.BasicCommand;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/24/13
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class IaikJceCommandAuthenticate extends BasicCommand {

    @Override
    protected void handleCommand(SRequest skyTrustRequest, SResponse skyTrustResponse) {
       //do nothing, handled by actor...
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.authenticate;
    }
}
