package pkcs11;

import gui.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import objects.MECHANISM;
import objects.MECHANISM.MechanismInfo;
import objects.ObjectManager;
import objects.PKCS11Object;

import proxys.ATTRIBUTE_TYPE;
import proxys.CK_MECHANISM_INFO;
import proxys.MECHANISM_TYPES;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;




/**
 * A Slot is a ServerInfo
 * 
 */
public class Slot{
	
	private IServerSession serversession;
	private HashMap<MECHANISM_TYPES,MECHANISM.MechanismInfo> mechanisms = new HashMap<>();
	
	private long slotID;
	private boolean roToken = false;
	static final public long MAX_SESSIONS_PER_SLOT = 100000000000l;
	private ArrayList<Session> sessionList = new ArrayList<Session>();
	private Session.USER_TYPE utype = Session.USER_TYPE.PUBLIC;
	
	public ObjectManager objectManager = new ObjectManager();
	
	//private String pin;
	
	public Slot(long slotid, Server.ServerInfo server){
		slotID = slotid;
		serversession = new ServerSession(server);
		loadMechanisms();
		//generate PIN
	}
	public Session.USER_TYPE getUserType(){
		return utype;
	}
	protected boolean isAnySessionRO(){
		for(Session s : sessionList){
			if(!s.isRW()){
				return true;
			}
		}
		return false;
	}
	public IServerSession getServersession() {
		return serversession;
	}

	//Handle = slotIndex*MAX_SESSIONS_PER_SLOT+sessionIndex
	//sessionIndex = Handle%MAX_SESSIONS_PER_SLOT
	//slotIndex = Handle/MAX_SESSIONS_PER_SLOT
	protected long getNewSessionID() throws PKCS11Error{
		long id=1;
		for(Session s:sessionList){
			if(s.getID() != id){
				if(id < MAX_SESSIONS_PER_SLOT){
					return id;
				}
			}
			id++;
		}
		if(sessionList.size()==0){
			return 1;
		}
		throw new PKCS11Error(RETURN_TYPE.SESSION_COUNT);
	}
	public long newSession(Session.ACCESS_TYPE atype) throws PKCS11Error{
		if(atype == Session.ACCESS_TYPE.RO && utype == Session.USER_TYPE.SO){
			throw new PKCS11Error(RETURN_TYPE.SESSION_READ_WRITE_SO_EXISTS);
		}
		if(roToken && atype == Session.ACCESS_TYPE.RW){
			throw new PKCS11Error(RETURN_TYPE.TOKEN_WRITE_PROTECTED);
		}
		long id = getNewSessionID();
		long handle = slotID*MAX_SESSIONS_PER_SLOT + id;
		sessionList.add(new Session(this,handle ,atype));
		return handle;
	}
	public void delSession(long sessionid) throws PKCS11Error{
		Iterator<Session> it = sessionList.iterator();
		for(Session s = null;it.hasNext();s = it.next()){
			if(s!=null && s.getID() == sessionid){
				it.remove();
				return;
			}
		}
		throw new PKCS11Error(RETURN_TYPE.SESSION_HANDLE_INVALID);	
	}
	public void delAllSessions(){
		sessionList.clear();
	}
	public Session getSessionByID(long sessionID) throws PKCS11Error{
		for(Session s:sessionList){
			if(s.getID() == sessionID){
				return s;
			}
		}
		throw new PKCS11Error(RETURN_TYPE.SESSION_HANDLE_INVALID);
	}
	public void login(Session.USER_TYPE new_utype) throws PKCS11Error{
		if(new_utype == Session.USER_TYPE.PUBLIC){
			return;
		}
		if(utype != Session.USER_TYPE.PUBLIC){
			throw new PKCS11Error(RETURN_TYPE.USER_ALREADY_LOGGED_IN);
		}
		if(new_utype == Session.USER_TYPE.SO && isAnySessionRO()){
			throw new PKCS11Error(RETURN_TYPE.SESSION_READ_ONLY_EXISTS);
		}
		
		utype = new_utype;
	}
	public void logout(){
		utype = Session.USER_TYPE.PUBLIC;
	}

	public long getID(){
		return slotID;
	}

	public ArrayList<Session> getSessionList(){
		return sessionList; 
	}
	public Server.ServerInfo getServerInfo(){
		return serversession.getInfo();
	}
	public Util.Capabilities getCapabilities(){
		return new Util.Capabilities().decrypt().encrypt().sign().verify();
	}

/*** crypto functions ***/	
	public static class CryptoOperationParams{
		public PKCS11Object key;
		public MECHANISM mechanism;
		public CryptoOperationParams(MECHANISM mech, PKCS11Object key){
			this.key = key;
			this.mechanism = mech;
		}
	}
	
	public CryptoOperationParams checkAndInit(long hKey,MECHANISM mechanism,String operation) throws PKCS11Error{
		PKCS11Object key = objectManager.getObject(hKey);
		MechanismInfo mech_info = getMechanismInfo(mechanism.getType());
		OBJECT_CLASS cl = key.getAttribute(ATTRIBUTE_TYPE.CLASS).copyToSwigEnum(OBJECT_CLASS.class);
		
		switch(operation){
		case "sign":
			if(!OBJECT_CLASS.PRIVATE_KEY.equals(cl) && !OBJECT_CLASS.SECRET_KEY.equals(cl)){
				throw new PKCS11Error(RETURN_TYPE.KEY_TYPE_INCONSISTENT);
			}
			if(!mech_info.isSign()){
				throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
			}
			if(!key.getAttribute(ATTRIBUTE_TYPE.SIGN).copyToBoolean()){
				throw new PKCS11Error(RETURN_TYPE.KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
		case "verify":
			if(!OBJECT_CLASS.PUBLIC_KEY.equals(cl) && !OBJECT_CLASS.SECRET_KEY.equals(cl))
				throw new PKCS11Error(RETURN_TYPE.KEY_TYPE_INCONSISTENT);
			if(!mech_info.isVerify()){
				throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
			}
			if(!key.getAttribute(ATTRIBUTE_TYPE.VERIFY).copyToBoolean()){
				throw new PKCS11Error(RETURN_TYPE.KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
		case "decrypt":
			if(!OBJECT_CLASS.PRIVATE_KEY.equals(cl) && !OBJECT_CLASS.SECRET_KEY.equals(cl))
				throw new PKCS11Error(RETURN_TYPE.KEY_TYPE_INCONSISTENT);
			if(!mech_info.isDecrypt()){
				throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
			}
			if(!key.getAttribute(ATTRIBUTE_TYPE.DECRYPT).copyToBoolean()){
				throw new PKCS11Error(RETURN_TYPE.KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
		case "encrypt":
			if(!OBJECT_CLASS.PUBLIC_KEY.equals(cl) && !OBJECT_CLASS.SECRET_KEY.equals(cl))
				throw new PKCS11Error(RETURN_TYPE.KEY_TYPE_INCONSISTENT);
			if(!mech_info.isEncrypt()){
				throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
			}
			if(!key.getAttribute(ATTRIBUTE_TYPE.ENCRYPT).copyToBoolean()){
				throw new PKCS11Error(RETURN_TYPE.KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
			default:
				throw new PKCS11Error(RETURN_TYPE.FUNCTION_NOT_SUPPORTED);
		}
		
		return new CryptoOperationParams(mechanism,key);
	}
// 			Session calls IServerSession directly	
///*** Crypto Functions ***/ 
//	public byte[] sign(byte[] data,CryptoOperationParams p) throws PKCS11Error{
//		return serversession.sign(data, PKCS11SkyTrustMapper.mapKey(p.key), PKCS11SkyTrustMapper.mapMechanism(p.mechanism));
//	}
//	public byte[] decrypt(byte[] encdata,CryptoOperationParams p) throws PKCS11Error{
//		// TODO Auto-generated method stub
//		return null;
//	}
//	public byte[] encrypt(byte[] data,CryptoOperationParams p) throws PKCS11Error{
//		// TODO Auto-generated method stub
//		return null;
//	}
//	public boolean verify(byte[] signature, byte[] data,CryptoOperationParams params) throws PKCS11Error {
//		// TODO Auto-generated method stub
//		return false;
//	}

/*** Mechanism management ***/
	public MECHANISM_TYPES[] getMechanisms(){
		return mechanisms.keySet().toArray(new MECHANISM_TYPES[0]);
	}
	public void getMechanismInfo(MECHANISM_TYPES type,CK_MECHANISM_INFO info) throws PKCS11Error{
		MECHANISM.MechanismInfo local_info = mechanisms.get(type);
		if(local_info == null){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
		}
		local_info.writeInto(info);
	}
	public MECHANISM.MechanismInfo getMechanismInfo(MECHANISM_TYPES type) throws PKCS11Error{
		MECHANISM.MechanismInfo local_info = mechanisms.get(type);
		if(local_info == null){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
		}
		return local_info;
	}
	public void loadMechanisms(){	
		//Note: right place to ask server for server-depended mechanisms
		mechanisms.put(MECHANISM_TYPES.RSA_PKCS,(MechanismInfo) new MechanismInfo().hw().sign().verify().wrap().unwrap());
		mechanisms.put(MECHANISM_TYPES.RSA_PKCS_OAEP,(MechanismInfo) new MechanismInfo().hw().encrypt().decrypt());
		mechanisms.put(MECHANISM_TYPES.SHA1_RSA_PKCS,(MechanismInfo) new MechanismInfo().hw().sign().verify());
		mechanisms.put(MECHANISM_TYPES.SHA224_RSA_PKCS,(MechanismInfo) new MechanismInfo().hw().sign().verify());
		mechanisms.put(MECHANISM_TYPES.SHA256_RSA_PKCS,(MechanismInfo) new MechanismInfo().hw().sign().verify());
		mechanisms.put(MECHANISM_TYPES.SHA512_RSA_PKCS,(MechanismInfo) new MechanismInfo().hw().sign().verify());

		
//		RSAES_RAW("RSAES-RAW"),
//	    RSAES_PKCS1_V1_5("RSAES-PKCS1-v1_5"),
//	    RSA_OAEP("RSA-OAEP"),
//	    RSASSA_PKCS1_V1_5_SHA_1("RSASSA-PKCS1-v1_5-SHA-1"),
//	    RSASSA_PKCS1_V1_5_SHA_224("RSASSA-PKCS1-v1_5-SHA-224"),
//	    RSASSA_PKCS1_V1_5_SHA_256("RSASSA-PKCS1-v1_5-SHA-256"),
//	    RSASSA_PKCS1_V1_5_SHA_512("RSASSA-PKCS1-v1_5-SHA-512"),
//	    RSA_PSS("RSA-PSS");
	}

}



