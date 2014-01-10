package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.iaikjce.IaikJceActor;

public class IaikJceCommandSign extends IaikJceCommandCryptoOperation {


    public IaikJceCommandSign(IaikJceActor actor) {
        super(actor);
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.sign;
    }

    @Override
    protected byte[] handleCryptoOperation(byte[] data, String keyId, String subKeyId, String userId, String algorithm) {
        return cryptoProvider.sign(data, keyId, subKeyId, userId, algorithm);
    }
}
