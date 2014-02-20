package pkcs11;
import java.util.ArrayList;

import objects.ATTRIBUTE;
import objects.MECHANISM;

import proxys.CK_ATTRIBUTE;
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
	
	private CryptoHelper signHelper;
	private CryptoHelper verifyHelper;
	private CryptoHelper decryptHelper;
	private CryptoHelper encryptHelper;
	public FindObjectsHelper findObjectsHelper;

	public Session(Slot slot,long handle,ACCESS_TYPE atype){
	//	this.flags = flags;
		this.atype = atype;
		this.slot = slot;
		this.handle = handle;
	}
	public boolean isRW(){
		return (atype==ACCESS_TYPE.RW)?true:false;
	}
	public SESSION_STATE getSessionState(){
		if(isRW()){
			if(getSlot().getUserType() == USER_TYPE.USER){
				return SESSION_STATE.RW_USER_FUNCTIONS;
			}else if(getSlot().getUserType() == USER_TYPE.SO){
				return SESSION_STATE.RW_SO_FUNCTIONS;				
			}else{
				return SESSION_STATE.RW_PUBLIC_SESSION;				
			}
		}else{
			if(getSlot().getUserType() == USER_TYPE.USER){
				return SESSION_STATE.RO_USER_FUNCTIONS;
			}else{
				return SESSION_STATE.RO_PUBLIC_SESSION;
			}
		}
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
	public void signInit(MECHANISM pMechanism, long hKey) throws PKCS11Error{
		if(signHelper != null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_ACTIVE);
		}
		signHelper = new CryptoHelper(getToken().checkAndInit(hKey,pMechanism,"sign"));
	}
	public void signAddData(byte[] pData) throws PKCS11Error{
		if(signHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		signHelper.addData(pData);
	}
	public byte[] sign() throws PKCS11Error{
		if(signHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		try{
			if(!signHelper.hasProcessedData()){
				byte[] signed_data = getToken().getServersession().sign(
						signHelper.getData(), 
						PKCS11SkyTrustMapper.mapKey(signHelper.getKey()), 
						PKCS11SkyTrustMapper.mapMechanism(signHelper.getMechanism()));
				signHelper.setProcessedData(signed_data);
			}
			return signHelper.getProcessedData();
		}catch(PKCS11Error e){
			signHelper = null;
			throw e;
		}
	}
	public void signFinal() throws PKCS11Error{
		if(signHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		signHelper = null;
	}
	
	public void verifyInit(MECHANISM pMechanism, long hKey) throws PKCS11Error{
		if(verifyHelper!= null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_ACTIVE);
		}
		verifyHelper = new CryptoHelper(getToken().checkAndInit(hKey,pMechanism,"verify"));
	}
	public void verifyAddData(byte[] pData) throws PKCS11Error{
		if(verifyHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		verifyHelper.addData(pData);
	}
	public boolean verify(byte[] signature) throws PKCS11Error{
		if(verifyHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		try{
			return getToken().getServersession().verify(verifyHelper.getData(),signature,
					PKCS11SkyTrustMapper.mapKey(verifyHelper.getKey()), 
					PKCS11SkyTrustMapper.mapMechanism(verifyHelper.getMechanism()));
		}catch(PKCS11Error e){
			verifyHelper = null; //Operation is canceld if any error happens
			throw e;
		}
	}
	public void verifyFinal() throws PKCS11Error{
		if(verifyHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		verifyHelper = null;
	}
	
	public void decryptInit(MECHANISM pMechanism, long hKey) throws PKCS11Error{
		if(decryptHelper != null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_ACTIVE);
		}
		decryptHelper = new CryptoHelper(getToken().checkAndInit(hKey,pMechanism,"decrypt"));
	}
	public void decrypt(byte[] encdata) throws PKCS11Error{
		if(decryptHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		try{
			byte[] plain_data = getToken().getServersession().decrypt(encdata,
			PKCS11SkyTrustMapper.mapKey(decryptHelper.getKey()), 
			PKCS11SkyTrustMapper.mapMechanism(decryptHelper.getMechanism()));
			
			decryptHelper.setProcessedData(plain_data);
		}catch(PKCS11Error e){
			decryptHelper = null;
			throw e;
		}
	}
	public byte[] decryptGetData() throws PKCS11Error{
		if(decryptHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		return decryptHelper.getProcessedData();
	}
	public void decryptFinal() throws PKCS11Error{
		if(decryptHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		decryptHelper = null;
	}

	public void encryptInit(MECHANISM pMechanism, long hKey) throws PKCS11Error{
		if(encryptHelper != null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_ACTIVE);
		}
		decryptHelper = new CryptoHelper(getToken().checkAndInit(hKey,pMechanism,"decrypt"));
	}
	public void encrypt(byte[] data) throws PKCS11Error{
		if(encryptHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		try{
			byte[] encdata = getToken().getServersession().encrypt(data,
					PKCS11SkyTrustMapper.mapKey(encryptHelper.getKey()), 
					PKCS11SkyTrustMapper.mapMechanism(encryptHelper.getMechanism()));
			encryptHelper.setProcessedData(encdata);
		}catch(PKCS11Error e){
			encryptHelper = null;
			throw e;
		}
	}
	public byte[] encryptGetData() throws PKCS11Error{
		if(encryptHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		return encryptHelper.getProcessedData();
	}
	public void encryptFinal() throws PKCS11Error{
		if(encryptHelper == null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		encryptHelper = null;
	}
	
	
	public void initFind(ATTRIBUTE[] attr) throws PKCS11Error{
		if(findObjectsHelper != null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_ACTIVE);
		}
		findObjectsHelper = new FindObjectsHelper(attr);
	}
}
