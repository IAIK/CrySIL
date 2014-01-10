package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.iaikjce.IaikJceActor;

public class IaikJceCommandDecrypt extends IaikJceCommandCryptoOperation {


    public IaikJceCommandDecrypt(IaikJceActor actor) {
        super(actor);
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.decrypt;
    }

    @Override
    protected byte[] handleCryptoOperation(byte[] data, String keyId, String subKeyId, String userId, String algorithm) {
        return cryptoProvider.decrypt(data,keyId,subKeyId,userId,algorithm);
    }
}
