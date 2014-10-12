package pkcs11;

import obj.CK_ATTRIBUTE;
import obj.CK_MECHANISM;
import obj.CK_RETURN_TYPE;
import obj.CK_SESSION_STATE;

import java.util.ArrayList;

//import proxys.CK_VOID_PTR;

public class Session {
	public enum ACCESS_TYPE {
		RO, RW
	}

	public enum USER_TYPE {
		PUBLIC, USER, SO
	}

	// private USER_TYPE utype;
	private ACCESS_TYPE atype;
	private byte[] pApplication;
	private Slot slot;
	private long flags;
	private long handle;

	public CryptoHelper signHelper;
	private CryptoHelper verifyHelper;
	private CryptoHelper decryptHelper;
	private CryptoHelper encryptHelper;
	public FindObjectsHelper findObjectsHelper;

	public Session(Slot slot, long handle, ACCESS_TYPE atype) {
		// this.flags = flags;
		this.atype = atype;
		this.slot = slot;
		this.handle = handle;
	}

	public boolean isRW() {
		return (atype == ACCESS_TYPE.RW) ? true : false;
	}

	public Long getSessionState() {
		if (isRW()) {
			if (getSlot().getUserType() == USER_TYPE.USER) {
				return CK_SESSION_STATE.CKS_RW_USER_FUNCTIONS;
			} else if (getSlot().getUserType() == USER_TYPE.SO) {
				return CK_SESSION_STATE.CKS_RW_SO_FUNCTIONS;
			} else {
				return CK_SESSION_STATE.CKS_RW_PUBLIC_SESSION;
			}
		} else {
			if (getSlot().getUserType() == USER_TYPE.USER) {
				return CK_SESSION_STATE.CKS_RO_USER_FUNCTIONS;
			} else {
				return CK_SESSION_STATE.CKS_RO_PUBLIC_SESSION;
			}
		}
	}

	// handle = SlotID + SessionID
	public long getHandle() {
		return handle;
	}

	// ID = SessionID (Slot local)
	protected long getID() {
		return handle % Slot.MAX_SESSIONS_PER_SLOT;
	}

	public Slot getSlot() {
		return slot;
	}

	public void signInit(CK_MECHANISM pMechanism, long hKey) throws PKCS11Error {
		if (signHelper != null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_ACTIVE);
		}
		signHelper = new CryptoHelper(getSlot().checkAndInit(hKey, pMechanism,
				"sign"));
	}

	public void signAddData(byte[] pData) throws PKCS11Error {
		if (signHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		signHelper.addData(pData);
	}

	public void signSetData(byte[] pData) throws PKCS11Error {
		if (signHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		if (!signHelper.hasProcessedData()) {
			signHelper.setData(pData);
		}
	}

	public byte[] sign() throws PKCS11Error {
		if (signHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		if (!signHelper.hasProcessedData()) {
			byte[] signed_data = getSlot().getToken().sign(
					signHelper.getData(), signHelper.getKey(),
					signHelper.getMechanism());
			if (signed_data == null) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_DEVICE_ERROR);
			}
			signHelper.setProcessedData(signed_data);
		}
		return signHelper.getProcessedData();
	}

	public void signFinal() throws PKCS11Error {
		if (signHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		signHelper = null;
	}

	public void verifyInit(CK_MECHANISM pMechanism, long hKey)
			throws PKCS11Error {
		if (verifyHelper != null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_ACTIVE);
		}
		verifyHelper = new CryptoHelper(getSlot().checkAndInit(hKey,
				pMechanism, "verify"));
	}

	public void verifyAddData(byte[] pData) throws PKCS11Error {
		if (verifyHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		verifyHelper.addData(pData);
	}

	public boolean verify(byte[] signature) throws PKCS11Error {
		if (verifyHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		Boolean result = getSlot().getToken().verify(verifyHelper.getData(),
				signature, verifyHelper.getKey(), verifyHelper.getMechanism());
		if (result == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_DEVICE_ERROR);
		}
		return result;
	}

	public void verifyFinal() throws PKCS11Error {
		if (verifyHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		verifyHelper = null;
	}

	public void decryptInit(CK_MECHANISM pMechanism, long hKey)
			throws PKCS11Error {
		if (decryptHelper != null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_ACTIVE);
		}
		decryptHelper = new CryptoHelper(getSlot().checkAndInit(hKey,
				pMechanism, "decrypt"));
	}

	public void decrypt(byte[] encdata) throws PKCS11Error {
		if (decryptHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		System.out.println("*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X");
		System.out.println("dcH getKey: "+decryptHelper.getKey());
		System.out.println("dcH getMech: "+decryptHelper.getMechanism());
		System.out.println("dcH encdata: "+encdata.length);
		System.out.println("*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X*X");
		
		byte[] plain_data = getSlot().getToken().decrypt(encdata, decryptHelper.getKey(), decryptHelper.getMechanism());
		//throw something?
		decryptHelper.setProcessedData(plain_data);
	}

	public byte[] decryptGetData() throws PKCS11Error {
		if (decryptHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		return decryptHelper.getProcessedData();
	}

	public void decryptFinal() throws PKCS11Error {
		if (decryptHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		decryptHelper = null;
	}

	public void encryptInit(CK_MECHANISM pMechanism, long hKey)
			throws PKCS11Error {
		if (encryptHelper != null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_ACTIVE);
		}
		encryptHelper = new CryptoHelper(getSlot().checkAndInit(hKey,
				pMechanism, "decrypt"));
	}

	public void encrypt(byte[] data) throws PKCS11Error {
		if (encryptHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		byte[] encdata = getSlot().getToken().encrypt(data,
				encryptHelper.getKey(), encryptHelper.getMechanism());
		if (encdata == null) {
			encryptHelper = null;
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_DEVICE_ERROR);
		}
		encryptHelper.setProcessedData(encdata);
	}

	public byte[] encryptGetData() throws PKCS11Error {
		if (encryptHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		return encryptHelper.getProcessedData();
	}

	public void encryptFinal() throws PKCS11Error {
		if (encryptHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		encryptHelper = null;
	}

	public void find(CK_ATTRIBUTE[] attr) throws PKCS11Error {
		if (findObjectsHelper != null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_ACTIVE);
		}
		ArrayList<Long> found_objs = getSlot().objectManager.findObjects(attr);
		findObjectsHelper = new FindObjectsHelper(found_objs);
	}

	public ArrayList<Long> findGetData() throws PKCS11Error {
		if (findObjectsHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		return findObjectsHelper.foundObjects;
	}

	public void findFinal() throws PKCS11Error {
		if (findObjectsHelper == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_OPERATION_NOT_INITIALIZED);
		}
		findObjectsHelper = null;
	}
}
