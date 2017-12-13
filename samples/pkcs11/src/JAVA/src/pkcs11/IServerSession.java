package pkcs11;

import at.iaik.skytrust.common.SkyTrustAlgorithm;

import java.util.List;

import configuration.Server;

import objects.MKey;


public interface IServerSession {

	public Server.ServerInfo getInfo();

	public List<MKey> getKeyList() throws PKCS11Error;

	public byte[] sign(byte[] pData, MKey key,SkyTrustAlgorithm mech) throws PKCS11Error;

	public boolean verify(byte[] data, byte[] signature, MKey key,SkyTrustAlgorithm mech) throws PKCS11Error;

	public byte[] encrypt(byte[] plaindata, MKey key,SkyTrustAlgorithm mech) throws PKCS11Error;
	
	public byte[] decrypt(byte[] encdata, MKey key,SkyTrustAlgorithm mech) throws PKCS11Error;

	public boolean isAutheticated();
}
