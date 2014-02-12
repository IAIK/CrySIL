package pkcs11;
import java.util.ArrayList;

import objects.MechanismObject;

import proxys.CK_MECHANISM;
import proxys.CK_NOTIFY_CALLBACK;
import proxys.CK_BYTE_ARRAY;
import proxys.CK_ULONG_JPTR;
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
	private byte[] pApplication;
	private CK_NOTIFY_CALLBACK notify_callback;
	private Slot slot;
	private long flags;
	private long handle;
	
	private SignHelper signHelper;
	private FindObjectsHelper findObjectsHelper;

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
	public Slot getToken(){
		return slot;
	}
	public void initSign(CK_MECHANISM pMechanism, long hKey) throws PKCS11Error{
		if(signHelper != null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_ACTIVE);
		}
		signHelper = getToken().checkAndInitSign(hKey,pMechanism);
	}
	//byte[] pData, long ulDataLen, CK_BYTE_ARRAY pSignature, CK_ULONG_JPTR pulSignatureLen
	public void sign(byte[] pData) throws PKCS11Error{
		if(signHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		signHelper.addData(pData);
	}
	public byte[] getsignedData() throws PKCS11Error{
		if(signHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		if(signHelper.cData == null){
			signHelper.cData = getToken().sign(signHelper.getData(), signHelper.getKey(), signHelper.getMechanism());
		}
		return signHelper.cData;
	}
	public void initFind(FindObjectsHelper f) throws PKCS11Error{
		if(findObjectsHelper != null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_ACTIVE);
		}
		findObjectsHelper = f;
	}
	
	public void verify(){
		
	}
	
}
