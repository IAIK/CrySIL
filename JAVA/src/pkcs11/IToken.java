package pkcs11;

import java.util.List;

import gui.Server;
import obj.CK_MECHANISM;
import objects.PKCS11Object;

public interface IToken {

	public abstract byte[] sign(byte[] data, PKCS11Object key, CK_MECHANISM mech);

	public abstract Boolean verify(byte[] data, byte[] signature,
			PKCS11Object key, CK_MECHANISM mech);

	public abstract byte[] encrypt(byte[] data, PKCS11Object key, CK_MECHANISM mech);

	public abstract byte[] decrypt(byte[] enc_data, PKCS11Object key,
			CK_MECHANISM mech) throws PKCS11Error;

	public abstract Server.ServerInfo getInfo();

	public abstract List<PKCS11Object> getObjects();
}
