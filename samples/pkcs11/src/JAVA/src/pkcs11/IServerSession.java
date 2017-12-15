package pkcs11;

import java.util.List;

import common.CrySilAlgorithm;
import configuration.Server;
import objects.MKey;


public interface IServerSession {

	public Server.ServerInfo getInfo();

	public List<MKey> getKeyList() throws PKCS11Error;

	public byte[] sign(byte[] pData, MKey key, CrySilAlgorithm mech) throws PKCS11Error;

	public boolean verify(byte[] data, byte[] signature, MKey key, CrySilAlgorithm mech) throws PKCS11Error;

	public byte[] encrypt(byte[] plaindata, MKey key, CrySilAlgorithm mech) throws PKCS11Error;
	
	public byte[] decrypt(byte[] encdata, MKey key, CrySilAlgorithm mech) throws PKCS11Error;

	public boolean isAutheticated();
}
