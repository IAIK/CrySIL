package pkcs11;

import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import gui.Server;

import java.util.List;


public interface IServerSession {

	public Server.ServerInfo getInfo();

	public List<SKey> getKeyList() throws PKCS11Error;

	public byte[] sign(byte[] pData, SKey key,SkyTrustAlgorithm mech) throws PKCS11Error;

	public boolean verify(byte[] data, byte[] signature, SKey key,SkyTrustAlgorithm mech) throws PKCS11Error;

	public byte[] encrypt(byte[] plaindata, SKey key,SkyTrustAlgorithm mech) throws PKCS11Error;
	
	public byte[] decrypt(byte[] encdata, SKey key,SkyTrustAlgorithm mech) throws PKCS11Error;

	public boolean isAutheticated();
}
