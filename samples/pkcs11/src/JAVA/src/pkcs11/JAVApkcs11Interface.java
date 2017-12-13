package pkcs11;

import obj.CK_ATTRIBUTE;
import obj.CK_ATTRIBUTE_TYPE;
import obj.CK_FLAGS;
import obj.CK_INFO;
import obj.CK_MECHANISM;
import obj.CK_MECHANISM_INFO;
import obj.CK_RETURN_TYPE;
import obj.CK_SESSION_INFO;
import obj.CK_SLOT_INFO;
import obj.CK_TOKEN_INFO;
import obj.CK_ULONG_PTR;
import obj.CK_USER_TYPE;
import obj.CK_VERSION;
import objects.*;

import java.util.ArrayList;
import java.util.Iterator;


import configuration.L;
import configuration.Server.ServerInfo;

/**
 * 
 * This is the main entry point to our PKCS#11 library.
 * 
 */
public class JAVApkcs11Interface {

	private static ResourceManager getRM() throws PKCS11Error {
		ResourceManager _instance = ResourceManager.getInstance();
		if (_instance == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_GENERAL_ERROR);
		}
		return _instance;
	}

	private static MaintenanceThread maintenanceThread = null;

	// Tick from MaintenanceThread
	public static void tick() {

	}

	/**
	 * C_Initialize initializes the Cryptoki library. This function needs to be
	 * called befor other C_xxx functions.
	 * 
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_Initialize() {
		L.log("function called: C_Initialize", 1);

		if (maintenanceThread == null) {
			maintenanceThread = new MaintenanceThread();
		}
		maintenanceThread.start();
		try {
				getRM().updateSlotList();
		} catch (PKCS11Error e) {
			e.printStackTrace();
		}
		L.log("leaving C_Initialize", 1);
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * 
	 * C_Finalize is called to indicate that an application is finished with the
	 * Cryptoki library. It should be the last Cryptoki call made by an
	 * application.
	 * 
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_Finalize() {
		L.log("function called: C_Finalize", 1);
		maintenanceThread.stopMaintenanceThread();
		maintenanceThread = null;
		// now we can die gracefully
		return CK_RETURN_TYPE.CKR_OK;

	}

	/**
	 * 
	 * C_GetSlotList is used to obtain a list of slots in the system.
	 * 
	 * @param tokenPresent
	 *            indicates whether the list obtained includes only those slots
	 *            with a token present (CK_TRUE), or all slots (CK_FALSE);
	 * @param pSlotList
	 *            receives the Slot IDs
	 * @param pulCount
	 *            (non-NULL) receives the number of slots
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_GetSlotList(boolean tokenPresent, long[] pSlotList,
			CK_ULONG_PTR pulCount) {
		L.log("function called: C_GetSlotList", 1);

		try {
			ArrayList<Slot> slotlist = null;
			slotlist = getRM().getSlotList();

			if (pSlotList == null) {
				pulCount.setValue(slotlist.size());
				return CK_RETURN_TYPE.CKR_OK;
			} else if (pSlotList.length == 0) {
				pulCount.setValue(0);
				return CK_RETURN_TYPE.CKR_OK;
			} else if (pulCount.getValue() < slotlist.size()) {
				pulCount.setValue(slotlist.size());
				return CK_RETURN_TYPE.CKR_BUFFER_TOO_SMALL;
			} else {
				pulCount.setValue(slotlist.size());
				Iterator<Slot> it = slotlist.iterator();
				for (int i = 0; it.hasNext(); i++) {
					pSlotList[i] = it.next().getID();
				}
				return CK_RETURN_TYPE.CKR_OK;
			}
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * C_GetSlotInfo obtains information about a particular slot in the system.
	 * 
	 * @param slotID
	 *            is the ID of the slot;
	 * @param pInfo
	 *            receives the slot information.
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */

	public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
		L.log("function called: C_GetSlotInfo", 1);

		if (pInfo == null) {
			return -1;
		}
		pInfo.setSlotDescription("IAIK Skytrust");
		Slot slot;
		try {
			slot = getRM().getSlotByID(slotID);
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
		pInfo.setManufacturerID(slot.getTokenInfo().getName() + " " + slotID);
		pInfo.setCKF_HW_SLOT();
		pInfo.setCKF_REMOVABLE_DEVICE();
		pInfo.setCKF_TOKEN_PRESENT();
		pInfo.getHardwareVersion().setMajor((byte) 0x02);
		pInfo.getHardwareVersion().setMinor((byte) 0x14);
		pInfo.getFirmwareVersion().setMajor((byte) 0x02);
		pInfo.getFirmwareVersion().setMinor((byte) 0x14);
		return CK_RETURN_TYPE.CKR_OK;

		// Slot slot = null;
		// try {
		// checkNullPtr(pInfo);
		// slot = getRM().getSlotByID(slotID);
		// } catch (PKCS11Error e) {
		// return e.getCode();
		// }
		//
		// long flags = Util.initFlags;
		//
		// flags = Util.setFlag(flags, CKF_TOKEN_PRESENT);
		// flags = Util.setFlag(flags, CKF_HW_SLOT);
		// pInfo.setFlags(flags);
		//
		// pInfo.setManufacturerID("IAIK Skytrust                                                                                       ");
		// // 32
		// pInfo.setSlotDescription(slot.getTokenInfo().getName()
		// +
		// "                                                                                       ");
		// // 32
		//
		// return CK_RETURN_TYPE.CKR_OK;
		// return 0;
	}

	/**
	 * obtains information about a particular token in the system
	 * 
	 * @param slotID
	 *            is the ID of the token's slot
	 * @param pInfo
	 *            receives the token information
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
		L.log("function called: C_GetTokenInfo", 1);
		try {
			Slot slot = getRM().getSlotByID(slotID);

			ServerInfo s = slot.getTokenInfo();
			pInfo.setLabel(Util.fixStringLen(s.getName().replaceAll(":", "_"),
					32));// 32 char
			pInfo.setManufacturerID(Util.fixStringLen("IAIK", 32));
			pInfo.setModel(Util.fixStringLen("skytrust token", 32));// 32
			pInfo.setSerialNumber(Util.fixStringLen("0.1", 32));

			pInfo.setCKF_WRITE_PROTECTED();
			pInfo.setCKF_PROTECTED_AUTHENTICATION_PATH();
			pInfo.setCKF_TOKEN_INITIALIZED();

			pInfo.setUlRwSessionCount(slot.getSessionCount());
			pInfo.setUlSessionCount(slot.getSessionCount());
			pInfo.setUlMaxSessionCount(Long.MAX_VALUE);
			pInfo.setUlMaxRwSessionCount(Long.MAX_VALUE);

			pInfo.setUlFreePrivateMemory(0);
			pInfo.setUlFreePublicMemory(0);
			pInfo.setUlTotalPrivateMemory(0);
			pInfo.setUlTotalPublicMemory(0);

			pInfo.setUlMaxPinLen(0);
			pInfo.setUlMinPinLen(0);

			pInfo.getHardwareVersion().setMajor((byte) 0x01);
			pInfo.getHardwareVersion().setMinor((byte) 0x07);
			pInfo.getFirmwareVersion().setMajor((byte) 0x02);
			pInfo.getFirmwareVersion().setMinor((byte) 0x03);

			pInfo.setUtcTime("0000000000000000");

			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}

	}

	/**
	 * is used to obtain a list of mechanism types supported by a token
	 * 
	 * @param slotID
	 *            is the ID of the token’s slot
	 * @param pMechanismList
	 * @param pulCount
	 *            receives the number of mechanisms
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_GetMechanismList(long slotID, long[] pMechanismList,
			CK_ULONG_PTR pulCount) {
		L.log("function called: C_GetMechanismList", 1);
		try {
			Slot slot = getRM().getSlotByID(slotID);
			int mech_count = slot.getMechanisms().length;

			if (pMechanismList == null) {
				pulCount.setValue(mech_count);
				return CK_RETURN_TYPE.CKR_OK;
			} else if (pulCount.getValue() < mech_count) {
				pulCount.setValue(mech_count);
				return CK_RETURN_TYPE.CKR_BUFFER_TOO_SMALL;
			} else {
				pulCount.setValue(mech_count);
				Long[] mechanisms = slot.getMechanisms();
				for (int i = 0; i < mech_count; i++) {
					// pMechanismList.setitem(i, mechanisms[i].swigValue());
					pMechanismList[i] = mechanisms[i];
				}
				return CK_RETURN_TYPE.CKR_OK;
			}
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	/**
	 * obtains information about a particular mechanism possibly supported by a
	 * token
	 * 
	 * @param slotID
	 *            is the ID of the token’s slot
	 * @param type
	 *            is the type of mechanism
	 * 
	 * @param pInfo
	 *            receives the mechanism information
	 * 
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_GetMechanismInfo(long slotID, long type,
			CK_MECHANISM_INFO pInfo) {
		L.log("function called: C_GetMechanismList", 1);
		try {
			Slot slot = getRM().getSlotByID(slotID);
			pInfo = slot.getMechanismInfo(type);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * opens a session between an application and a token in a particular slot
	 * 
	 * @param slotID
	 *            is the ID of the token's slot
	 * @param flags
	 *            indicates the type of session
	 * @param phSession
	 *            receives the handle for the new session
	 * 
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_OpenSession(long slotID, long flags,
			CK_ULONG_PTR phSession) {
		L.log("function called: C_GetMechanismList", 1);
		L.log("slotID: " + slotID, 2);
		L.log("flags: " + flags, 2);
		try {

			if (!Util.isFlagSet(flags, CK_FLAGS.CKF_SERIAL_SESSION)) {
				return CK_RETURN_TYPE.CKR_GENERAL_ERROR; // CKR_PARALLEL_NOT_SUPPORTED
			}
			Session.ACCESS_TYPE atype = Session.ACCESS_TYPE.RO;
			if (Util.isFlagSet(flags, CK_FLAGS.CKF_RW_SESSION)) {
				atype = Session.ACCESS_TYPE.RW;
			}
			phSession.setValue(getRM().newSession(slotID, atype));
			L.log("new SessionID:  " + phSession.getValue(), 5);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * obtains information about a session
	 * 
	 * @param hSession
	 *            is the session’s handle
	 * @param pInfo
	 *            receives the session information
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
		L.log("function called: C_GetSessionInfo", 1);
		L.log("hSession: " + hSession, 2);
		try {
			// Decrypt: not implemented
			Session session = getRM().getSessionByHandle(hSession);
			pInfo.setSlotID(session.getSlot().getID());
			pInfo.setCKF_SERIAL_SESSION();
			pInfo.setUlDeviceError(0L);
			if (session.isRW()) {
				pInfo.setCKF_RW_SESSION();
			}

			pInfo.setState(session.getSessionState());

			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	/**
	 * logs a user into a token
	 * 
	 * 
	 * @param hSession
	 *            is a session handle
	 * @param userType
	 *            is the user type
	 * @param pPin
	 *            is the user's pin
	 * @return A long value according to PKCS#11 standard.
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_Login(long hSession, long userType, String pPin) {
		L.log("function called: C_Login", 1);
		if (userType == CK_USER_TYPE.CKU_SO) {
			return CK_RETURN_TYPE.CKR_USER_TYPE_INVALID;
		}
		if (userType == CK_USER_TYPE.CKU_CONTEXT_SPECIFIC) {
			return CK_RETURN_TYPE.CKR_USER_TYPE_INVALID;
		}
		try {
			getRM().getSlotBySessionHandle(hSession).login(
					Session.USER_TYPE.USER);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * logs a user out from a token
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_Logout(long hSession) {
		L.log("function called: C_Logout", 1);
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * closes a session between an application and a token
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_CloseSession(long hSession) {
		L.log("function called: C_CloseSession", 1);
		try {
			getRM().delSession(hSession);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	/**
	 * closes all sessions an application has with a token
	 * 
	 * @param slotID
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_CloseAllSessions(long slotID) {
		L.log("function called: C_CloseAllSessions", 1);
		try {
			getRM().delAllSessionsToSlot(slotID);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	/**
	 * obtains the value of one or more attributes of an object
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param hObject
	 *            is the object's handle
	 * @param pTemplate
	 *            specifies which attribute values are to be obtained, and
	 *            receives the attribute values
	 * @param ulCount
	 *            is the number of attributes in the template
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_GetAttributeValue(long hSession, long hObject,
			CK_ATTRIBUTE[] pTemplate, long ulCount) {
		L.log("function called: C_GetAttributeValue", 1);
		try {
			Long res = CK_RETURN_TYPE.CKR_OK;
			if (pTemplate.length != ulCount) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_ARGUMENTS_BAD);
			}

			Session session = getRM().getSessionByHandle(hSession);
			PKCS11Object obj = session.getSlot().objectManager
					.getObject(hObject);

			CK_ATTRIBUTE src;
			L.log("J: object: " + hObject, 5);
			for (CK_ATTRIBUTE attr : pTemplate) {
				try {
					src = obj.getAttribute(attr.getType());
				} catch (PKCS11Error e) {
					if (e.getCode() == CK_RETURN_TYPE.CKR_ATTRIBUTE_TYPE_INVALID) {
						attr.setUlValueLen(-1);
						res = CK_RETURN_TYPE.CKR_ATTRIBUTE_TYPE_INVALID;
						continue;
					} else {
						e.printStackTrace();
						throw e;
					}
				}
				if (attr.getpValue() == null) {
					attr.setUlValueLen(src.getUlValueLen());
				} else if (attr.getUlValueLen() < src.getUlValueLen()) {
					L.log("src len:   " + src.getUlValueLen(), 5);
					L.log("attr len:  " + attr.getUlValueLen(), 5);

					attr.setUlValueLen(attr.getUlValueLen());
					res = CK_RETURN_TYPE.CKR_BUFFER_TOO_SMALL;
				} else {
					attr.setpValue(src.getpValue());
					attr.setUlValueLen(src.getUlValueLen());
					L.log("J: attribute: " + attr.toString(), 5);
				}
			}
			return res;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * modifies the value of one or more attributes of an object
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param hObject
	 *            is the object's handle
	 * @param pTemplate
	 *            specifies which attribute values are to be modified and their
	 *            new values
	 * @param ulCount
	 *            is the number of attributes in the template
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_SetAttributeValue(long hSession, long hObject,
			CK_ATTRIBUTE[] pTemplate, long ulCount) {
		L.log("function called: C_SetAttributeValue", 1);
		try {
			// checkNullPtr(pTemplate);
			Session session = getRM().getSessionByHandle(hSession);
			PKCS11Object obj = session.getSlot().objectManager
					.getObject(hObject);

			for (CK_ATTRIBUTE attr : pTemplate) {
				obj.setAttribute(attr.createClone());
			}
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * creates a new object
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pTemplate
	 *            the object’s template
	 * @param ulCount
	 *            is the number of attributes in the template
	 * @param phObject
	 *            receives the new object’s handle
	 * 
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_CreateObject(long hSession, CK_ATTRIBUTE[] pTemplate,
			long ulCount, CK_ULONG_PTR phObject) {
		L.log("function called: C_CreateObject", 1);
		try {
			Session session = getRM().getSessionByHandle(hSession);
			long handle = session.getSlot().objectManager
					.createObject(pTemplate);
			phObject.setValue(handle);
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}

		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * initializes a decryption operation
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pMechanism
	 *            is the decryption mechanism
	 * @param hKey
	 *            is the handle of the decryption key
	 * 
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_DecryptInit(long hSession, CK_MECHANISM pMechanism,
			long hKey) {
		L.log("function called: C_DecryptInit", 1);
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.decryptInit(pMechanism, hKey);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * 
	 * decrypts encrypted data in a single part
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pEncryptedData
	 *            is the encrypted data
	 * @param ulEnryptedDataLen
	 *            is the length of the encrypted data
	 * @param pData
	 *            receives the recovered data
	 * @param pulDataLen
	 *            receives the length of the recovered data
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_Decrypt(long hSession, byte[] pEncryptedData,
			long ulEnryptedDataLen, byte[] pData, CK_ULONG_PTR pulDataLen) {
		L.log("function called: C_Decrypt", 1);

		try {
			long val = C_DecryptUpdate(hSession, pEncryptedData,
					ulEnryptedDataLen, pData, pulDataLen);
			if (val != CK_RETURN_TYPE.CKR_OK) {
				return val;
			}
			Session session = getRM().getSessionByHandle(hSession);
			session.decryptFinal();

			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * continues a multiple-part decryption operation, processing another
	 * encrypted data part
	 * 
	 * @param hSession
	 *            is the session’s handle
	 * 
	 * @param pEncryptedPart
	 *            is the encrypted data part
	 * @param ulEncryptedPartLen
	 *            is the length of the encrypted data part
	 * @param pPart
	 *            receives the recovered data part
	 * @param pulPartLen
	 *            length of the recovered data part
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart,
			long ulEncryptedPartLen, byte[] pPart, CK_ULONG_PTR pulPartLen) {
		L.log("function called: C_DecryptUpdate", 1);
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.decrypt(pEncryptedPart);

			byte[] decryptedPart = session.decryptGetData();
			if (pPart == null) {
				pulPartLen.setValue(decryptedPart.length);
				return CK_RETURN_TYPE.CKR_OK;
			} else if (pulPartLen.getValue() < decryptedPart.length) {
				pulPartLen.setValue(decryptedPart.length);
				return CK_RETURN_TYPE.CKR_BUFFER_TOO_SMALL;
			} else {
				pulPartLen.setValue(decryptedPart.length);
				for (int i = 0; i < decryptedPart.length; i++) {
					pPart[i] = decryptedPart[i];
				}
				return CK_RETURN_TYPE.CKR_OK;
			}
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * destroys an object
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param hObject
	 *            is the object's handle
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_DestroyObject(long hSession, long hObject) {
		L.log("function called: C_DestroyObject", 1);
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.getSlot().objectManager.deleteObject(hObject);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * initializes a search for token and session objects that match a template
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pTemplate
	 *            is a search template that specifies the attribute values to
	 *            match
	 * 
	 * @param ulCount
	 *            is the number of attributes in the search template
	 * 
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_FindObjectsInit(long hSession,
			CK_ATTRIBUTE[] pTemplate, long ulCount) {
		L.log("function called: C_FindObjectsInit", 1);

		CK_ATTRIBUTE[] array;

		if (pTemplate == null) {
			array = new CK_ATTRIBUTE[0];
		} else {
			array = pTemplate.clone();
		}
		for (CK_ATTRIBUTE tmp : array) {

			L.log("attribute: " + tmp.toString(), 5);

		}
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.find(array);

		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * continues a search for token and session objects that match a template,
	 * obtaining additional object handles
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param phObject
	 *            location that receives the list (array) of additional object
	 *            handles
	 * 
	 * @param ulMaxObjectCount
	 *            is the maximum number of object handles to be returned
	 * 
	 * @param pulObjectCount
	 *            receives the actual number of object handles returned
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_FindObjects(long hSession, long[] phObject,
			long ulMaxObjectCount, CK_ULONG_PTR pulObjectCount) {
		L.log("function called: C_FindObjects", 1);
		try {
			Session session = getRM().getSessionByHandle(hSession);

			ArrayList<Long> handles = session.findGetData();

			Iterator<Long> it = handles.iterator();
			int size = 0;
			for (; size < ulMaxObjectCount && it.hasNext(); size++) {
				phObject[size] = it.next();
				it.remove();
			}
			pulObjectCount.setValue(size);

		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * terminates a search for token and session objects
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_FindObjectsFinal(long hSession) {
		L.log("function called: C_FindObjectsFinal", 1);
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.findFinal();
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * generates random or pseudo-random data
	 * 
	 * @deprecated not implemented right now!
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param RandomData
	 *            receives the random data
	 * @param ulRandomLen
	 *            is the length in bytes of the random or pseudo-random data to
	 *            be generated
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	@Deprecated
	public static long C_GenerateRandom(long hSession, byte[] RandomData,
			long ulRandomLen) {
		L.log("function called: C_GenerateRandom | Be careful, Not implemented right now!",
				0);
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * returns general information about Cryptoki
	 * 
	 * @param pInfo
	 *            receives the information
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_GetInfo(CK_INFO pInfo) {
		L.log("function called: C_GetInfo", 1);
		if (pInfo == null) {
			return CK_RETURN_TYPE.CKR_ARGUMENTS_BAD;
		}
		if (pInfo.getLibraryVersion() == null) {
			pInfo.setLibraryVersion(new CK_VERSION((byte) 0x00, (byte) 0x00));
		}
		if (pInfo.getCryptokiVersion() == null) {
			pInfo.setCryptokiVersion(new CK_VERSION((byte) 0x00, (byte) 0x00));
		}
		pInfo.setManufacturerID("TUG IAIK");
		pInfo.setLibraryDescription("library description");
		pInfo.getCryptokiVersion().setMajor((byte) 0x02);
		pInfo.getCryptokiVersion().setMinor((byte) 0x14);

		pInfo.getLibraryVersion().setMajor((byte) 1);
		pInfo.getLibraryVersion().setMinor((byte) 0);

		pInfo.setLibraryDescription(Util.fixStringLen(
				"skytrust pkcs11 library", 32));

		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * mixes additional seed material into the token’s random number generator
	 * 
	 * @deprecated, not implemented right now
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pSeed
	 *            is the seed material
	 * @param ulSeedLen
	 *            is the length of the seed material
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	@Deprecated
	public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
		L.log("function called: C_SeedRandom | Be careful, Not implemented right now!",
				0);
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * modifies the PIN of the user that is currently logged in, or the CKU_USER
	 * PIN if the session is not logged in
	 * 
	 * @deprecated, doesn't touch the pin at all by now.
	 * 
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pOldPin
	 *            is the old pin
	 * @param ulOldLen
	 *            is the length of the old pin
	 * @param pNewPin
	 *            is the new pin
	 * @param ulNewLen
	 *            is the length of the new pin
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */

	@Deprecated
	public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen,
			String pNewPin, long ulNewLen) {
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * initializes a signature operation, where the signature is an appendix to
	 * the data
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pMechanism
	 *            is the signature mechanism
	 * @param hKey
	 *            is the handle of the signature key
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_SignInit(long hSession, CK_MECHANISM pMechanism,
			long hKey) {
		L.log("function called: C_SignInit", 1);

		try {
			if (pMechanism == null) {
				return CK_RETURN_TYPE.CKR_ARGUMENTS_BAD;
			}
			Session session = getRM().getSessionByHandle(hSession);
			session.signInit(pMechanism, hKey);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * continues a multiple-part signature operation, processing another data
	 * part
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pPart
	 *            points to the data part
	 * @param ulPartLen
	 *            is the length of the data part
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_SignUpdate(long hSession, byte[] pPart, long ulPartLen) {
		try {
			L.log("function called: C_SignUpdate", 1);
			Session session = getRM().getSessionByHandle(hSession);
			session.signAddData(pPart);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	/**
	 * finishes a multiple-part signature operation, returning the signature
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pSignature
	 *            receives the signature
	 * @param pulSignatureLen
	 *            receives the length of the signature
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_SignFinal(long hSession, byte[] pSignature,
			CK_ULONG_PTR pulSignatureLen) {
		L.log("function called: C_SignFinal", 1);
		Session session;
		try {
			session = getRM().getSessionByHandle(hSession);
			if (pSignature == null) {
				pulSignatureLen.setValue(session.sign().length);
				return CK_RETURN_TYPE.CKR_OK;
			} else if (pulSignatureLen.getValue() < session.sign().length) {
				pulSignatureLen.setValue(session.sign().length);
				return CK_RETURN_TYPE.CKR_BUFFER_TOO_SMALL;
			} else {
				byte[] signed = session.sign();
				pulSignatureLen.setValue(signed.length);
				for (int i = 0; i < signed.length; i++) {
					pSignature[i] = signed[i];
				}
				session.signFinal();
				return CK_RETURN_TYPE.CKR_OK;
			}
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	/**
	 * signs data in a single part, where the signature is an appendix to the
	 * data
	 * 
	 * @param hSession
	 *            is the session's handle
	 * @param pData
	 *            is the data
	 * @param ulDataLen
	 *            is the length of the data
	 * @param pSignature
	 *            receives the signature
	 * @param pulSignatureLen
	 *            receives the length of the signature
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 */
	public static long C_Sign(long hSession, byte[] pData, long ulDataLen,
			byte[] pSignature, CK_ULONG_PTR pulSignatureLen) {
		L.log("function called: C_Sign", 1);
		L.log("Data to sign: \r\n" + new String(pData), 2);

		long val = C_SignUpdate(hSession, pData, ulDataLen);
		if (val != CK_RETURN_TYPE.CKR_OK) {
			return val;
		}
		val = C_SignFinal(hSession, pSignature, pulSignatureLen);
		if (val != CK_RETURN_TYPE.CKR_OK) {
			return val;
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

	/**
	 * unwraps (i.e. decrypts) a wrapped key, creating a new private key or
	 * secret key object
	 * 
	 * @param hSession
	 *            current session's handle
	 * @param pMechanism
	 *            wrapping mechanism
	 * @param hUnwrappingKey
	 *            the unwrappingKey
	 * @param pWrappedKey
	 *            the wrapped key
	 * @param ulWrappedKeyLen
	 *            length of the wrapped key
	 * @param pTemplate
	 *            template for the new key
	 * @param ulAttributeCount
	 *            number of attributes
	 * @param phKey
	 *            pointer to the key
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 * 
	 */
	public static long C_UnwrapKey(long hSession, CK_MECHANISM pMechanism,
			long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen,
			CK_ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_PTR phKey) {
		L.log("function called: C_UnwrapKey", 1);
		Session session;
		try {
			session = getRM().getSessionByHandle(hSession);
	//		PKCS11Object obj = session.getSlot().objectManager .getObject(hUnwrappingKey);
			session.decryptInit(pMechanism, hUnwrappingKey);
			session.decrypt(pWrappedKey);
			byte[] unwrappedKey = session.decryptGetData();
			session.decryptFinal();
			CK_ATTRIBUTE[] pTemplate2 = new CK_ATTRIBUTE[pTemplate.length + 1];
			for (int i = 0; i < pTemplate.length; i++) {
				pTemplate2[i] = pTemplate[i];
			}
			pTemplate2[pTemplate2.length - 1] = new CK_ATTRIBUTE(
					CK_ATTRIBUTE_TYPE.CKA_VALUE, unwrappedKey,
					unwrappedKey.length);
			C_FindObjectsInit(hSession, pTemplate2, 1);
			CK_ULONG_PTR foundObjectCount = new CK_ULONG_PTR(0);
			C_FindObjects(hSession, null, 3, foundObjectCount);
			long hKey = 0;
			if (foundObjectCount.getValue() == 0) {
				hKey = session.getSlot().objectManager.newObject(unwrappedKey,
						pTemplate2);
			} else {
				long[] objectHandle = new long[(int) foundObjectCount
						.getValue()];
				C_FindObjects(hSession, null, 3, foundObjectCount);
				hKey = objectHandle[0];
			}
			C_FindObjectsFinal(hSession);
			phKey.setValue(hKey);

		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}

		return CK_RETURN_TYPE.CKR_OK;

	}

	/**
	 * wraps (i.e., encrypts) a private or secret
	 * 
	 * @param hSession
	 *            current session's handle
	 * @param pMechanism
	 *            wrapping mechanism
	 * @param hWrappingKey
	 *            handle of the wrapping-key
	 * @param hKey
	 *            handle of the key to be wrapped
	 * @param pWrappedKey
	 *            buffer for the wrapped key
	 * @param pulWrappedKeyLen
	 *            pointer to the length of the wrapped key
	 * @return A long value according to PKCS#11 standard
	 * @see PKCS#11 v2.20 standard: <br>
	 *      ftp://ftp.rsasecurity.com/pub/pkcs/pkcs-11/v2-20/pkcs-11v2-20.pdf
	 * 
	 */
	public static long C_WrapKey(long hSession, CK_MECHANISM pMechanism,
			long hWrappingKey, long hKey, byte[] pWrappedKey,
			CK_ULONG_PTR pulWrappedKeyLen) {

		L.log("function called: C_WrapKey", 1);
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session = getRM().getSessionByHandle(hSession);
			PKCS11Object obj = session.getSlot().objectManager
					.getObject(hWrappingKey);

			session.encryptInit(pMechanism, hKey);
			session.encrypt(obj.getData());
			pWrappedKey = session.encryptGetData();
			session.decryptFinal();
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

}
