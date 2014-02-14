package pkcs11;

import gui.Server;
import java.io.IOException;
import java.util.List;

import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;


public interface IServerSession {

	public Server.ServerInfo getInfo();

	public List<SKey> getKeyList();

	public byte[] sign(byte[] pData, SKey key,SkyTrustAlgorithm mech) throws IOException, PKCS11Error;


	public void encrypt();
	
	public void decrypt();

	public boolean isAutheticated();

	boolean verify(byte[] data, byte[] signature, SKey key,SkyTrustAlgorithm mech);
}
