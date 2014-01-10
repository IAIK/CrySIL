package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.iaikjce.IaikJceActor;

public class IaikJceCommandEncrypt extends IaikJceCommandCryptoOperation {


    public IaikJceCommandEncrypt(IaikJceActor actor) {
        super(actor);
    }

    @Override
    public CmdType getCommandType() {
        return CmdType.encrypt;
    }

    @Override
    protected byte[] handleCryptoOperation(byte[] data, String keyId, String subKeyId, String userId, String algorithm) {
        return cryptoProvider.encrypt(data, keyId, subKeyId, userId, algorithm);
    }
}
