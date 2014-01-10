import gui.Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import proxys.RETURN_TYPE;




/**
 * A Slot is a ServerInfo
 * 
 */
public class Slot{
	private ServerSession serversession;
	private long slotID;
	private boolean roToken = false;
	static final public long MAX_SESSIONS_PER_SLOT = 100000000000l;
	private ArrayList<Session> sessionList = null; 
	private Session.USER_TYPE utype = Session.USER_TYPE.PUBLIC;
	
	//private String pin;
	
	public Slot(long slotid, Server.ServerInfo server){
		server = null;
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
	/**
	 * A Token is Present if the user is authenticated to the Skytrust Server
	 */
	public boolean isTokenPresent(){
		return (server == null)?false:server.isAutheticated();
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
	public String getServerName(){
		return "nameParsedfromServerURL";
	}
}



