package pkcs11;

import java.util.List;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import gui.Server;
import objects.MECHANISM;
import objects.ObjectManager;
import objects.PKCS11Object;

public interface IToken {

	public abstract byte[] sign(byte[] data, PKCS11Object key, MECHANISM mech);

	public abstract Boolean verify(byte[] data, byte[] signature,
			PKCS11Object key, MECHANISM mech);

	public abstract byte[] encrypt(byte[] data, PKCS11Object key, MECHANISM mech);

	public abstract byte[] decrypt(byte[] enc_data, PKCS11Object key,
			MECHANISM mech);

	public abstract Server.ServerInfo getInfo();

	public abstract List<SKey> getObjects();
}