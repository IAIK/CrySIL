import proxys.CK_NOTIFY_CALLBACK;
import proxys.CK_VOID_PTR;
import proxys.RETURN_TYPE;
import proxys.SESSION_STATE;


public class Session {
	private SESSION_STATE state;
	private CK_VOID_PTR pApplication;
	private CK_NOTIFY_CALLBACK notify_callback;
	private long slotID;
	private long flags;
	private long handle;
	
	private static long new_handle = 0;
	
	private long getNewHandle(){
		return ++new_handle; //TODO overflow?
	}

	protected Session(long slotID,long flags){
		this.flags = flags;
		this.slotID = slotID;
		handle = getNewHandle();
		state = SESSION_STATE.RO_PUBLIC_SESSION;
	}
	public long getHandle(){
		return handle;
	}
	public RETURN_TYPE login(){
		return RETURN_TYPE.OK;
	}
	public RETURN_TYPE logout(){
		return RETURN_TYPE.OK;
	}
}
