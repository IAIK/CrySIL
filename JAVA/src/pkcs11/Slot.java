package pkcs11;

import gui.Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import proxys.MECHANISM_TYPE;
import proxys.RETURN_TYPE;




/**
 * A Slot is a ServerInfo
 * 
 */
public class Slot{
	
	private ServerSession serversession;
	private ArrayList<Long> mechanisms = new ArrayList<Long>();
	private long slotID;
	private boolean roToken = false;
	static final public long MAX_SESSIONS_PER_SLOT = 100000000000l;
	private ArrayList<Session> sessionList = new ArrayList<Session>();
	private Session.USER_TYPE utype = Session.USER_TYPE.PUBLIC;
	
	private ObjectStorage storage;
	
	public ServerSession getServersession() {
		return serversession;
	}
	//private String pin;
	
	public Slot(long slotid, Server.ServerInfo server){
		slotID = slotid;
		serversession = new ServerSession(server);
		//generate PIN
	}
	protected Session.USER_TYPE getAllSessionUserType(){
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
			if(s.getID() == sessionid){
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
	public void setID(long id){
		slotID = id;
	}
	public ArrayList<Session> getSessionList(){
		return sessionList; 
	}
	public Server.ServerInfo getServerInfo(){
		return serversession.getInfo();
	}



	public ArrayList<Long> getMechanisms(){
		
		
		mechanisms.add(new Long(MECHANISM_TYPE.RSA_PKCS_KEY_PAIR_GEN.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES_KEY_GEN.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES3_KEY_GEN.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.RSA_PKCS.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.RSA_X_509.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.MD5_RSA_PKCS.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SHA1_RSA_PKCS.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES_ECB.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES_CBC.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES_CBC_PAD.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES3_ECB.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES3_CBC.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.DES3_CBC_PAD.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SHA_1.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SHA_1_HMAC.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SHA_1_HMAC_GENERAL.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.MD5.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.MD5_HMAC.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.MD5_HMAC_GENERAL.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SSL3_PRE_MASTER_KEY_GEN.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SSL3_MASTER_KEY_DERIVE.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SSL3_KEY_AND_MAC_DERIVE.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SSL3_MD5_MAC.swigValue()));
		mechanisms.add(new Long(MECHANISM_TYPE.SSL3_SHA1_MAC.swigValue()));
		
		
		
		mechanisms.add(new Long(MECHANISM_TYPE.RSA_PKCS.swigValue()));

		
		
		
//		RSAES_RAW("RSAES-RAW"),
//	    RSAES_PKCS1_V1_5("RSAES-PKCS1-v1_5"),
//	    RSA_OAEP("RSA-OAEP"),
//	    RSASSA_PKCS1_V1_5_SHA_1("RSASSA-PKCS1-v1_5-SHA-1"),
//	    RSASSA_PKCS1_V1_5_SHA_224("RSASSA-PKCS1-v1_5-SHA-224"),
//	    RSASSA_PKCS1_V1_5_SHA_256("RSASSA-PKCS1-v1_5-SHA-256"),
//	    RSASSA_PKCS1_V1_5_SHA_512("RSASSA-PKCS1-v1_5-SHA-512"),
//
//	    RSA_PSS("RSA-PSS");


		return mechanisms;
	}

}



