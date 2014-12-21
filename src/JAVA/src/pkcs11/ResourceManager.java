package pkcs11;

import gui.DataVaultSingleton;
import gui.Server;
import obj.CK_RETURN_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/*
 * one per Application 
 * manges the sessionlist and slotlist 
 * and maybe other such things
 * */
public class ResourceManager {

	private static ResourceManager _instance;
	private ArrayList<Slot> slotList = new ArrayList<Slot>();;
	static final public long MAX_SLOT = 1000;


	public static ResourceManager getInstance() {
		if (_instance == null) {
			try {
				_instance = new ResourceManager();
				// _instance.updateSlotList();
			} catch (PKCS11Error e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _instance;
	}

	private ResourceManager() throws PKCS11Error {
		// updateSlotList();
	}

	public long newSession(long slotid, Session.ACCESS_TYPE atype)
			throws PKCS11Error {
		Slot slot = getSlotByID(slotid);
		return slot.newSession(atype);
	}

	public void delSession(long handle) throws PKCS11Error {
		long slot = handle / Slot.MAX_SESSIONS_PER_SLOT;
		long sessionid = handle % Slot.MAX_SESSIONS_PER_SLOT;
		getSlotByID(slot).delSession(sessionid);
	}

	public void delAllSessionsToSlot(long slotid) throws PKCS11Error {
		getSlotByID(slotid).delAllSessions();
	}

	public Session getSessionByHandle(long handle) throws PKCS11Error {
		// sessionIndex = Handle%MAX_SESSIONS_PER_SLOT
		// slotIndex = Handle/MAX_SESSIONS_PER_SLOT
		long slotid = handle / Slot.MAX_SESSIONS_PER_SLOT;
		long session = handle % Slot.MAX_SESSIONS_PER_SLOT;
		return getSlotByID(slotid).getSessionByID(session);
	}

	public Slot getSlotBySessionHandle(long handle) throws PKCS11Error {
		long slotid = handle / Slot.MAX_SESSIONS_PER_SLOT;
		return getSlotByID(slotid);
	}

	public Slot getSlotByID(long slotid) throws PKCS11Error {
		if (slotid > Integer.MAX_VALUE || slotid > MAX_SLOT) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_SLOT_ID_INVALID);

		}
		Slot r = slotList.get((int) (slotid - 1));
		if (r == null) {
            System.out.println("some really" +
                    "strange things happened...");

            throw new PKCS11Error(CK_RETURN_TYPE.CKR_SLOT_ID_INVALID);
		}
		return r;
	}

	protected long newSlotID() throws PKCS11Error {
		long id = 1;
		if (slotList.size() == 0) {
			return id;
		}
		class SlotComparator implements Comparator<Slot> {
			@Override
			public int compare(Slot arg0, Slot arg1) {
				if (arg0.getID() < arg1.getID()) {
					return -1;
				} else if (arg0.getID() == arg1.getID()) {
					return 0;
				} else {
					return 1;
				}
			}
		}
		Collections.sort(slotList, new SlotComparator());
		for (Slot s : slotList) {
			if (s.getID() != id) {
				break;
			}
			id = id + 1;
		}
		if (id >= MAX_SLOT) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_GENERAL_ERROR);
		}
		return id;
	}

	public void delSlot(long slotid) {
		Iterator<Slot> it = slotList.iterator();
		for (Slot s = null; it.hasNext(); s = it.next()) {
			if (s.getID() == slotid) {
				it.remove();
				return;
			}
		}
	}

	public ArrayList<Slot> getSlotList() {
		return slotList;
	}

	public void updateSlotList() throws PKCS11Error {
		ArrayList<Server.ServerInfo> info_list = DataVaultSingleton
				.getInstance().getServerInfoList();
		if (info_list.size() > MAX_SLOT) {
			while (info_list.remove(MAX_SLOT))
				;
		}
		for (Slot s : slotList) {
			info_list.remove(s.getTokenInfo());
		}
		for (Server.ServerInfo info : info_list) {
			if (info != null)
				slotList.add(new Slot(newSlotID(), info));
		}
	}
}
