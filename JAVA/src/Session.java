
import java.util.ArrayList;

import proxys.CK_NOTIFY_CALLBACK;
//import proxys.CK_VOID_PTR;
import proxys.RETURN_TYPE;
import proxys.SESSION_STATE;


public class Session {
	public enum ACCESS_TYPE {
		RO,RW
	}
	public enum USER_TYPE {
		PUBLIC,USER,SO
	}
//	private USER_TYPE utype;
	private ACCESS_TYPE atype;
//	private CK_VOID_PTR pApplication; //;alksdjf;alksjf;ladks
	private CK_NOTIFY_CALLBACK notify_callback;
	private Slot slot;
	private long flags;
	private long handle;
	
	public SignHelper signHelper;

	public Session(Slot slot,long handle,ACCESS_TYPE atype){
	//	this.flags = flags;
		this.atype = atype;
		this.slot = slot;
		this.handle = handle;
	}
	public boolean isRW(){
		return (atype==ACCESS_TYPE.RW)?true:false;
	}
	//handle = SlotID + SessionID
	public long getHandle(){
		return handle;
	}
	//ID = SessionID (Slot local)
	protected long getID(){
		return handle%Slot.MAX_SESSIONS_PER_SLOT;
	}
	public Slot getSlot(){
		return slot;
	}
}
