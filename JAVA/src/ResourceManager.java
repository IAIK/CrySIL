import gui.Credential;
import java.util.ArrayList;
import proxys.RETURN_TYPE;



/*
 * one per Application 
 * manges the sessionlist and slotlist 
 * and maybe other such things
 * */
public class ResourceManager {

	private ArrayList<Slot> slotList = null;
	static final public long MAX_SLOT = 1000;
	
	public long newSession(long slotid,Session.ACCESS_TYPE atype) throws PKCS11Error{
		Slot slot = getSlotByID(slotid);
		return slot.newSession(atype);
	}
	public Session getSessionByHandle(long handle) throws PKCS11Error{
		//sessionIndex = Handle%MAX_SESSIONS_PER_SLOT
		//slotIndex = Handle/MAX_SESSIONS_PER_SLOT
		long slot = handle / Slot.MAX_SESSIONS_PER_SLOT;
		long session = handle % Slot.MAX_SESSIONS_PER_SLOT;
		return getSlotByID(slot).getSessionByID(session);
	}

	public Slot getSlotByID(long slotid) throws PKCS11Error{
		if(slotid > Integer.MAX_VALUE || slotid > MAX_SLOT){
			throw new PKCS11Error(RETURN_TYPE.SLOT_ID_INVALID);
		}
		Slot r = slotList.get((int) slotid-1);
		if(r == null){
			throw new PKCS11Error(RETURN_TYPE.SLOT_ID_INVALID);
		}
		return r;
	}
	protected long newSlotID(){
		long id=1;
		for(Slot s:slotList){
			if(s.getID() != id){
				if(id < MAX_SLOT){
					return id;
				}
			}
			id++;
		}
		throw new ;
	}
	public long newSlot(Server s,Credential user){
		long id = newSlotID();
		slotList.add(new Slot(user,id,s));
		return id;
	}
}
