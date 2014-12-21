package pkcs11;

import obj.CK_ATTRIBUTE_TYPE;
import obj.CK_MECHANISM;
import obj.CK_MECHANISM_INFO;
import obj.CK_MECHANISM_TYPE;
import obj.CK_OBJECT_TYPE;
import obj.CK_RETURN_TYPE;
import objects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import configuration.L;
import configuration.Server;

/**
 * A Slot is a ServerInfo
 * 
 */
public class Slot {

	private IToken token;
	private HashMap<Long, CK_MECHANISM_INFO> mechanisms = new HashMap<>();

	private long slotID;
	private boolean roToken = false;
//	static final public long MAX_SESSIONS_PER_SLOT = 100000000000l;
    static final public long MAX_SESSIONS_PER_SLOT = 100000l;

	private ArrayList<Session> sessionList = new ArrayList<Session>();
	private Session.USER_TYPE utype = Session.USER_TYPE.PUBLIC;

	public ObjectManager objectManager = new ObjectManager();

	// private String pin;

	public Slot(long slotid, Server.ServerInfo server) throws PKCS11Error {
		slotID = slotid;
		token = new Token(server);
		loadMechanisms();
		List<PKCS11Object> objs = token.getObjects();
		if (objs == null) {
			return;
		}
		for (PKCS11Object o : objs) {
			objectManager.addObject(o);
		}
		// generate PIN
	}

	public Session.USER_TYPE getUserType() {
		return utype;
	}

	protected boolean isAnySessionRO() {
		for (Session s : sessionList) {
			if (!s.isRW()) {
				return true;
			}
		}
		return false;
	}

	public IToken getToken() {
		return token;
	}

	// Handle = slotIndex*MAX_SESSIONS_PER_SLOT+sessionIndex
	// sessionIndex = Handle%MAX_SESSIONS_PER_SLOT
	// slotIndex = Handle/MAX_SESSIONS_PER_SLOT
	protected long getNewSessionID() throws PKCS11Error {
		if (sessionList.size() == 0) {
			return 1;
		}
		long id = 1;
		for (Session s : sessionList) {
			if (s.getID() != id) {
				return id;
			}
			id++;
		}
		if (id < MAX_SESSIONS_PER_SLOT) {
			return id;
		}
		throw new PKCS11Error(CK_RETURN_TYPE.CKR_SESSION_COUNT);
	}

	public long newSession(Session.ACCESS_TYPE atype) throws PKCS11Error {
		if (atype == Session.ACCESS_TYPE.RO && utype == Session.USER_TYPE.SO) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_SESSION_READ_WRITE_SO_EXISTS);
		}
		if (roToken && atype == Session.ACCESS_TYPE.RW) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_TOKEN_WRITE_PROTECTED);
		}
		long id = getNewSessionID();
		long handle = slotID * MAX_SESSIONS_PER_SLOT + id;
		sessionList.add(new Session(this, handle, atype));
		return handle;
	}

	public void delSession(long sessionid) throws PKCS11Error {
		Iterator<Session> it = sessionList.iterator();
		while (it.hasNext()) {
			if (it.next().getID() == sessionid) {
				it.remove();
				return;
			}
		}
		throw new PKCS11Error(CK_RETURN_TYPE.CKR_SESSION_HANDLE_INVALID);
	}

	public void delAllSessions() {
		sessionList.clear();
	}

	public Session getSessionByID(long sessionID) throws PKCS11Error {
        L.log("looking for session: "+sessionID +" id from first: "+sessionList.get(0).getID(), 3);
		for (Session s : sessionList) {
			if (s.getID() == sessionID) {
				return s;
        }
		}
        L.log("session with id "+ sessionID + " not found",3);
		throw new PKCS11Error(CK_RETURN_TYPE.CKR_SESSION_HANDLE_INVALID);
	}
	public int getSessionCount(){
		return sessionList.size();
	}

	public void login(Session.USER_TYPE new_utype) throws PKCS11Error {
		if (new_utype == Session.USER_TYPE.PUBLIC) {
			return;
		}
		if (utype != Session.USER_TYPE.PUBLIC) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_USER_ALREADY_LOGGED_IN);
		}
		if (new_utype == Session.USER_TYPE.SO && isAnySessionRO()) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_SESSION_READ_ONLY_EXISTS);
		}

		utype = new_utype;
	}

	public void logout() {
		utype = Session.USER_TYPE.PUBLIC;
	}

	public long getID() {
		return slotID;
	}

	public ArrayList<Session> getSessionList() {
		return sessionList;
	}

	public Server.ServerInfo getTokenInfo() {
		return token.getInfo();
	}

	public Util.Capabilities getCapabilities() {
		return new Util.Capabilities().decrypt().encrypt().sign().verify();
	}

	/*** crypto functions ***/
	public static class CryptoOperationParams {
		public PKCS11Object key;
		public CK_MECHANISM mechanism;

		public CryptoOperationParams(CK_MECHANISM mech, PKCS11Object key) {
			this.key = key;
			this.mechanism = mech;
		}
	}

	public CryptoOperationParams checkAndInit(long hKey, CK_MECHANISM mechanism,
			String operation) throws PKCS11Error {
		PKCS11Object key = objectManager.getObject(hKey);
		CK_MECHANISM_INFO mech_info = getMechanismInfo(mechanism.getMechanism());
		// OBJECT_CLASS cl =
		// key.getAttribute(CK_ATTRIBUTE_TYPE.CKA_CLASS).copyToSwigEnum(OBJECT_CLASS.class);
		Object cl = key.getAttribute(CK_ATTRIBUTE_TYPE.CKA_CLASS).getpValue();

		switch (operation) {
		case "sign":
			if (!CK_OBJECT_TYPE.CKO_PRIVATE_KEY.equals(cl)
					&& !CK_OBJECT_TYPE.CKO_SECRET_KEY.equals(cl)) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_TYPE_INCONSISTENT);
			}
			if (!mech_info.getCKF_SIGN()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_MECHANISM_INVALID);
			}
			if (!(boolean) key.getAttribute(CK_ATTRIBUTE_TYPE.CKA_SIGN)
					.getpValue()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
		case "verify":
			if (!CK_OBJECT_TYPE.CKO_PUBLIC_KEY.equals(cl)
					&& !CK_OBJECT_TYPE.CKO_SECRET_KEY.equals(cl))
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_TYPE_INCONSISTENT);
			if (!mech_info.getCKF_VERIFY()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_MECHANISM_INVALID);
			}
			if (!(boolean) key.getAttribute(CK_ATTRIBUTE_TYPE.CKA_VERIFY)
					.getpValue()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
		case "decrypt":
			if (!CK_OBJECT_TYPE.CKO_PRIVATE_KEY.equals(cl)
					&& !CK_OBJECT_TYPE.CKO_SECRET_KEY.equals(cl))
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_TYPE_INCONSISTENT);
			if (!mech_info.getCKF_DECRYPT()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_MECHANISM_INVALID);
			}
			if (!(boolean) key.getAttribute(CK_ATTRIBUTE_TYPE.CKA_DECRYPT)
					.getpValue()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
		case "encrypt":
			if (!CK_OBJECT_TYPE.CKO_PUBLIC_KEY.equals(cl)
					&& !CK_OBJECT_TYPE.CKO_SECRET_KEY.equals(cl))
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_TYPE_INCONSISTENT);
			if (!mech_info.getCKF_ENCRYPT()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_MECHANISM_INVALID);
			}
			if (!(boolean) key.getAttribute(CK_ATTRIBUTE_TYPE.CKA_ENCRYPT)
					.getpValue()) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_FUNCTION_NOT_PERMITTED);
			}
			break;
		default:
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_FUNCTION_NOT_SUPPORTED);
		}

		return new CryptoOperationParams(mechanism, key);
	}

	/*** Mechanism management ***/
	public Long[] getMechanisms() {
		return mechanisms.keySet().toArray(new Long[0]);
	}

//	public void getMechanismInfo(long type, CK_MECHANISM_INFO info)
//			throws PKCS11Error {
//		CK_MECHANISM_INFO local_info = mechanisms.get(type);
//		if (local_info == null) {
//			throw new PKCS11Error(CK_RETURN_TYPE.CKR_MECHANISM_INVALID);
//		}
////		local_info.writeInto(info);
//	}

	public CK_MECHANISM_INFO getMechanismInfo(long type)
			throws PKCS11Error {
		CK_MECHANISM_INFO local_info = mechanisms.get(type);
		if (local_info == null) {
            L.log("mechanisminfo == null" + type, 2);
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_MECHANISM_INVALID);
		}
		return local_info;
	}

	public void loadMechanisms() {
		// Note: right place to ask server for server-depended mechanisms
        CK_MECHANISM_INFO info =  new CK_MECHANISM_INFO(0, Long.MAX_VALUE, 0);
        info.setCKF_HW();info.setCKF_SIGN();info.setCKF_VERIFY();info.setCKF_WRAP();info.setCKF_UNWRAP();info.setCKF_DECRYPT();
		mechanisms.put(CK_MECHANISM_TYPE.CKM_RSA_PKCS, info);

        info =  new CK_MECHANISM_INFO(0, Long.MAX_VALUE, 0);
        info.setCKF_HW();info.setCKF_ENCRYPT();info.setCKF_DECRYPT();
		mechanisms.put(CK_MECHANISM_TYPE.CKM_RSA_PKCS_OAEP, info);

        info =  new CK_MECHANISM_INFO(0, Long.MAX_VALUE, 0);
        info.setCKF_HW();info.setCKF_SIGN();info.setCKF_VERIFY();
		mechanisms.put(CK_MECHANISM_TYPE.CKM_SHA1_RSA_PKCS, info);
	
        info =  new CK_MECHANISM_INFO(0, Long.MAX_VALUE, 0);
        info.setCKF_HW();info.setCKF_SIGN();info.setCKF_VERIFY();
		mechanisms.put(CK_MECHANISM_TYPE.CKM_SHA224_RSA_PKCS, info);

        info =  new CK_MECHANISM_INFO(0, Long.MAX_VALUE, 0);
        info.setCKF_HW();info.setCKF_SIGN();info.setCKF_VERIFY();
		mechanisms.put(CK_MECHANISM_TYPE.CKM_SHA256_RSA_PKCS, info);

        info =  new CK_MECHANISM_INFO(0, Long.MAX_VALUE, 0);
        info.setCKF_HW();info.setCKF_SIGN();info.setCKF_VERIFY();
		mechanisms.put(CK_MECHANISM_TYPE.CKM_SHA512_RSA_PKCS, info);

	}
}
