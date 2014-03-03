package pkcs11;

import gui.Server;
import gui.Server.ServerInfo;

import java.util.ArrayList;
import java.util.List;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

import objects.MECHANISM;
import objects.PKCS11Object;

public class Token implements IToken {
	private ServerSession server;
	
	public Token(Server.ServerInfo server_info){
		server = new ServerSession(server_info);
	}

	@Override
	public byte[] sign(byte[] data, PKCS11Object key, MECHANISM mech){
		try {
			return server.sign(data, PKCS11SkyTrustMapper.mapKey(key), PKCS11SkyTrustMapper.mapMechanism(mech));
		} catch (PKCS11Error e) {
			return null;
		}
	}

	@Override
	public Boolean verify(byte[] data,byte[] signature, PKCS11Object key, MECHANISM mech){
		//TODO local verify
		return null;
	}

	@Override
	public byte[] encrypt(byte[] data, PKCS11Object key, MECHANISM mech){
		try {
			return server.encrypt(data, PKCS11SkyTrustMapper.mapKey(key), PKCS11SkyTrustMapper.mapMechanism(mech));
		} catch (PKCS11Error e) {
			return null;
		}
	}

	@Override
	public byte[] decrypt(byte[] enc_data, PKCS11Object key, MECHANISM mech){
		try {
			return server.decrypt(enc_data, PKCS11SkyTrustMapper.mapKey(key), PKCS11SkyTrustMapper.mapMechanism(mech));
		} catch (PKCS11Error e) {
			return null;
		}
	}
	
	@Override
	public List<PKCS11Object> getObjects(){
		try {
			List<SKey> list = server.getKeyList();
			ArrayList<PKCS11Object> objlist = new ArrayList<PKCS11Object>();
			PKCS11Object o;
			for(SKey key : list){
				o = PKCS11SkyTrustMapper.mapToCert(key);
				if(o != null) objlist.add(o);
				o = PKCS11SkyTrustMapper.mapToPrivate(key);
				if(o != null) objlist.add(o);
				o = PKCS11SkyTrustMapper.mapToPub(key);
				if(o != null) objlist.add(o);
			}
			return objlist;
		} catch (PKCS11Error e) {
			return null;
		}
	}
	@Override
	public ServerInfo getInfo() {
		return server.getInfo();
	}
}
