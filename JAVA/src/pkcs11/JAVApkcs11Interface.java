package pkcs11;

import gui.Server.ServerInfo;
import obj.*;
import objects.PKCS11Object;

import java.util.ArrayList;
import java.util.Iterator;

public class JAVApkcs11Interface {
	static {
		System.load("/usr/lib/libpkcs11_java_wrap.so");
	}

	private static ResourceManager getRM() throws PKCS11Error {
		ResourceManager _instance = ResourceManager.getInstance(appID);
		if (_instance == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_GENERAL_ERROR);
		}
		return _instance;
	}

	private static String appID;

	/*
	 * whatever you do, keep that thing running! always! start with C_Initailize
	 * kill with C_Finalize! use it to do maintenance and other stuff, change
	 * the sleep interval if you like but keep it running!
	 */
	private static MaintenanceThread maintenanceThread = null;

	// Tick from MaintenanceThread
	public static void tick() {
		try {
			getRM().updateSlotList();
		} catch (PKCS11Error e) {
			e.printStackTrace();
		}

	}

	public static long C_Initialize() {
		System.err.println("starting maintenance Thread!");
		if (maintenanceThread == null) {
			maintenanceThread = new MaintenanceThread();
		}
		maintenanceThread.start();
		appID = "newRandomID";
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_Finalize() {
		maintenanceThread.stopMaintenanceThread();
		maintenanceThread = null;
		// now we can die gracefully
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_GetSlotList(boolean tokenPresent, long[] pSlotList,
			CK_ULONG_PTR pulCount) {

		System.err.println("\nC_GetSlotList..start:");
		try {
			ArrayList<Slot> slotlist = null;
			slotlist = getRM().getSlotList();

			if (pSlotList == null) {
				pulCount.setValue(slotlist.size());
				return CK_RETURN_TYPE.CKR_OK;
			} else if (pSlotList.length == 0) {
				pulCount.setValue(0);
				System.out
						.println("C_GetSlotList no Slots: " + slotlist.size());
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
			System.err
					.println("\n slotlist..exception..............................................");
			return e.getCode();
		}
	}

	public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
		if (pInfo == null) {
			return -1;
		}
		pInfo.setSlotDescription("IAIK Skytrust");
		Slot slot;
		try {
			slot = getRM().getSlotByID(slotID);
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
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

	public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
		System.err
				.println("\nC_GetTokenInfo..start..............................................");
		try {
			Slot slot = getRM().getSlotByID(slotID);

			ServerInfo s = slot.getTokenInfo();
			pInfo.setLabel(Util.fixStringLen(s.getName(), 32));// 32 char
			pInfo.setManufacturerID(Util.fixStringLen("IAIK", 32));
			pInfo.setModel(Util.fixStringLen("", 32));// 32
			pInfo.setSerialNumber(Util.fixStringLen("0.1", 32));

			pInfo.setLabel("tokenlabel");
			pInfo.setManufacturerID("IAIK");
			pInfo.setModel("Token 43");
			pInfo.setSerialNumber("0.1");

			pInfo.setCKF_WRITE_PROTECTED();
			pInfo.setCKF_PROTECTED_AUTHENTICATION_PATH();
			pInfo.setCKF_TOKEN_INITIALIZED();
			// flags = Util.setFlag(flags, slot.getCapabilities().getAsFlags());
			// //does this belong here?

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
			System.err.println("\n tokeninfo..exception");
			return e.getCode();
		}

	}

	public static long C_GetMechanismList(long slotID, long[] pMechanismList,
			CK_ULONG_PTR pulCount) {
		System.err.println("\nJAVA: C_GetMechanismList:");
		try {
			Slot slot = getRM().getSlotByID(slotID);
			int mech_count = slot.getMechanisms().length;

			if (pMechanismList == null) {
				System.err
						.println("\nC_GetMechanismList.........ende0  ...........");
				pulCount.setValue(mech_count);
				return CK_RETURN_TYPE.CKR_OK;
			} else if (pulCount.getValue() < mech_count) {
				System.err
						.println("\nC_GetMechanismList.........buffer  ...........");
				pulCount.setValue(mech_count);
				return CK_RETURN_TYPE.CKR_BUFFER_TOO_SMALL;
			} else {
				pulCount.setValue(mech_count);
				Long[] mechanisms = slot.getMechanisms();
				for (int i = 0; i < mech_count; i++) {
					// pMechanismList.setitem(i, mechanisms[i].swigValue());
					pMechanismList[i] = mechanisms[i];
				}
				System.err
						.println("\nC_GetMechanismList.........ende  ...........");
				return CK_RETURN_TYPE.CKR_OK;
			}
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	public static long C_GetMechanismInfo(long slotID, long type,
			CK_MECHANISM_INFO pInfo) {
		try {
			System.err.println("java: C_GetMechanismInfo........start");
			Slot slot = getRM().getSlotByID(slotID);
			pInfo = slot.getMechanismInfo(type);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	public static long C_OpenSession(long slotID, long flags,
			CK_ULONG_PTR phSession) {
		System.err.println("C_OpenSession ............start............."
				+ slotID);
		try {

			if (!Util.isFlagSet(flags, CK_FLAGS.CKF_SERIAL_SESSION)) {
				System.err
						.println("C_OpenSession ............error.............");
				return CK_RETURN_TYPE.CKR_GENERAL_ERROR; // CKR_PARALLEL_NOT_SUPPORTED
			}
			Session.ACCESS_TYPE atype = Session.ACCESS_TYPE.RO;
			if (Util.isFlagSet(flags, CK_FLAGS.CKF_RW_SESSION)) {
				atype = Session.ACCESS_TYPE.RW;
			}
			phSession.setValue(getRM().newSession(slotID, atype));
			System.err.println("C_OpenSession ............end.............");
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			System.err.println("C_OpenSession ............error1.............");
			e.printStackTrace();
			return e.getCode();
		}
	}

	public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
		System.err.println("\njava: getSessionInfo start................."
				+ pInfo);
		try {
			// Decrypt: not implemented
			Session session = getRM().getSessionByHandle(hSession);
			pInfo.setSlotID(session.getSlot().getID());
			pInfo.setCKF_SERIAL_SESSION();
			if (session.isRW()) {
				pInfo.setCKF_RW_SESSION();
			}

			pInfo.setState(session.getSessionState());
			System.err.println("leaving getSessionInfo ordinary...");

			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	public static long C_Login(long hSession, long userType, String pPin) {
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

	public static long C_Logout(long hSession) {
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_CloseSession(long hSession) {
		try {
			getRM().delSession(hSession);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	public static long C_CloseAllSessions(long slotID) {
		try {
			getRM().delAllSessionsToSlot(slotID);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	public static long C_GetAttributeValue(long hSession, long hObject,
			CK_ATTRIBUTE[] pTemplate, long ulCount) {
		try {
			Long res = CK_RETURN_TYPE.CKR_OK;
			if (pTemplate.length != ulCount) {
				throw new PKCS11Error(CK_RETURN_TYPE.CKR_ARGUMENTS_BAD);
			}

			Session session = getRM().getSessionByHandle(hSession);
			PKCS11Object obj = session.getSlot().objectManager
					.getObject(hObject);

			CK_ATTRIBUTE src;
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
					attr.setUlValueLen(src.getUlValueLen());
					res = CK_RETURN_TYPE.CKR_BUFFER_TOO_SMALL;
				} else {
					attr.setpValue(src.getpValue());
					attr.setUlValueLen(src.getUlValueLen());
				}
			}
			return res;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	public static long C_SetAttributeValue(long hSession, long hObject,
			CK_ATTRIBUTE[] pTemplate, long ulCount) {
		try {
			// checkNullPtr(pTemplate);
			System.err.print("\nC_SetAttributeValue....Object: " + hObject
					+ "....");
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

	public static long C_CreateObject(long hSession, CK_ATTRIBUTE[] pTemplate,
			long ulCount, CK_ULONG_PTR phObject) {
		System.err.print("\nC_CreateObject....");
		try {
			Session session = getRM().getSessionByHandle(hSession);
			long handle = session.getSlot().objectManager
					.createObject(pTemplate);
			phObject.setValue(handle);
			System.err.print("Object: " + handle + "....");
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}

		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_DecryptInit(long hSession, CK_MECHANISM pMechanism,
			long hKey) {
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.decryptInit(pMechanism, hKey);
			System.err
					.println("DecryptInit: alles okay... init fertig... returning ok :)");
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	public static long C_Decrypt(long hSession, byte[] pEncryptedData,
			long ulEnryptedDataLen, byte[] pData, CK_ULONG_PTR pulDataLen) {

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

	public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart,
			long ulEncryptedPartLen, byte[] pPart, CK_ULONG_PTR pulPartLen) {
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
				System.err
						.println("\n slotlist..ende..............................................");
				return CK_RETURN_TYPE.CKR_OK;
			}
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	public static long C_DestroyObject(long hSession, long hObject) {
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.getSlot().objectManager.deleteObject(hObject);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
	}

	public static long C_FindObjectsInit(long hSession,
			CK_ATTRIBUTE[] pTemplate, long ulCount) {
		System.err.println("\nthis is java calling FindobjectsInit "
				+ pTemplate);
		System.err.println("hSession: " + hSession);
		System.err.println("ulCount: " + ulCount);
		if (pTemplate != null)
			for (int i = 0; i < pTemplate.length; i++) {
				System.err.println("pTemplate[" + i + "]" + pTemplate[i]);
				System.err.println("pTmeplate[" + i + "].type: "
						+ pTemplate[i].getType());
				System.err.println("pTmeplate[" + i + "].length: "
						+ pTemplate[i].getUlValueLen());
				System.err.println("pTmeplate[" + i + "].pValue: "
						+ pTemplate[i].getpValue());
				if (pTemplate[i].getpValue() instanceof Long)
					System.err.println("is a Long");
				if (pTemplate[i].getpValue() instanceof Byte[])
					System.err.println("is a Byte[]");
				if (pTemplate[i].getpValue() instanceof Boolean)
					System.err.println("is a Boolean");
			}

		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.find(pTemplate);

		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_FindObjects(long hSession, long[] phObject,
			long ulMaxObjectCount, CK_ULONG_PTR pulObjectCount) {
		System.err.println("\nthis is java calling Findobjects");
		try {
			Session session = getRM().getSessionByHandle(hSession);

			ArrayList<Long> handles = session.findGetData();
			System.err.println("handles.size(): " + handles.size());

			Iterator<Long> it = handles.iterator();
			int size = 0;
			for (; size < ulMaxObjectCount && it.hasNext(); size++) {
				phObject[size] = it.next();
				it.remove();
			}
			pulObjectCount.setValue(size);

		} catch (PKCS11Error e) {
			e.printStackTrace();
			System.err.println("findobjects....error1");
			return e.getCode();
		}
		System.err.println("found objects.................."
				+ pulObjectCount.getValue());
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_FindObjectsFinal(long hSession) {
		System.err.println("\nthis is java calling FindobjectsFinal");
		try {
			Session session = getRM().getSessionByHandle(hSession);
			session.findFinal();
		} catch (PKCS11Error e) {
			e.printStackTrace();
			return e.getCode();
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_GenerateRandom(long hSession, byte[] RandomData,
			long ulRandomLen) {
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_GetInfo(CK_INFO pInfo) {
		System.err.println("\n java is calling, C_GetINFO");
		if (pInfo == null) {
			return CK_RETURN_TYPE.CKR_ARGUMENTS_BAD;
		}
		pInfo.setManufacturerID("weeehaaaa");
		pInfo.setLibraryDescription("tadaaa");
		pInfo.getCryptokiVersion().setMajor((byte) 0x02);
		pInfo.getCryptokiVersion().setMinor((byte) 0x14);

		pInfo.getLibraryVersion().setMajor((byte) 1);
		pInfo.getLibraryVersion().setMinor((byte) 0);

		pInfo.setLibraryDescription(Util.fixStringLen(
				"skytrust pkcs11 library", 32));

		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen,
			String pNewPin, long ulNewLen) {
		return CK_RETURN_TYPE.CKR_OK;
	}

	public static long C_SignInit(long hSession, CK_MECHANISM pMechanism,
			long hKey) {

		try {
			System.err.print("\nC_SignInit....");
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

	public static long C_SignUpdate(long hSession, byte[] pPart, long ulPartLen) {
		try {
			System.err.print("\nC_Sign....");
			Session session = getRM().getSessionByHandle(hSession);
			session.signAddData(pPart);
			return CK_RETURN_TYPE.CKR_OK;
		} catch (PKCS11Error e) {
			return e.getCode();
		}
	}

	public static long C_SignFinal(long hSession, byte[] pSignature,
			CK_ULONG_PTR pulSignatureLen) {
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

	public static long C_Sign(long hSession, byte[] pData, long ulDataLen,
			byte[] pSignature, CK_ULONG_PTR pulSignatureLen) {
		System.err.print("\nC_Sign....");

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
	 * 
	 */
	public static long C_UnwrapKey(long hSession, CK_MECHANISM pMechanism,
			long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen,
			CK_ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_PTR phKey) {
		System.out.println("UnwrapKey: ");
		System.out.println("hSession: " + hSession);
		System.out.println("pMechanism: " + pMechanism);
		System.out.println("hUnwrappingKey: " + hUnwrappingKey);
		System.out.println("pWrappedKey: " + pWrappedKey.length);
		System.out.println("wrappedKeyLen: " + ulWrappedKeyLen);
		System.out.println("pTemplate: " + pTemplate);
		System.out.println("ulAttribute: " + ulAttributeCount);
		System.out.println("phKey: " + phKey);
		System.out
				.println("************************************************************");
		Session session;
		try {
			session = getRM().getSessionByHandle(hSession);
			PKCS11Object obj = session.getSlot().objectManager
					.getObject(hUnwrappingKey);
			if (obj == null) {
				System.out.println("ERROR: unwrappingKey not found!");
			}
			session.decryptInit(pMechanism, hUnwrappingKey);
			if (pMechanism == null) {
				System.out.println("ERROR: mechanism not found!");
			}
			session.decrypt(pWrappedKey);
			if (pWrappedKey == null) {
				System.out.println("ERROR: pWrappedKey is null");
			}
			byte[] unwrappedKey = session.decryptGetData();
			session.decryptFinal();
			CK_ATTRIBUTE[] pTemplate2 = new CK_ATTRIBUTE[pTemplate.length + 1];
			for (int i = 0; i < pTemplate.length; i++) {
				pTemplate2[i] = pTemplate[i];
			}
			pTemplate2[pTemplate2.length - 1] = new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_VALUE, unwrappedKey, unwrappedKey.length);
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
				if (objectHandle.length != 1) {
					System.err
							.println("ERROR: UnwrapKey duplicate Entries found!");
				}
				hKey = objectHandle[0];
			}
			C_FindObjectsFinal(hSession);
			System.out.println("Unwrapped Key with handle " + hKey);
			phKey.setValue(hKey);

		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
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
	 * 
	 */
	public static long C_WrapKey(long hSession, CK_MECHANISM pMechanism,
			long hWrappingKey, long hKey, byte[] pWrappedKey,
			CK_ULONG_PTR pulWrappedKeyLen) {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getCode();
		}
		return CK_RETURN_TYPE.CKR_OK;
	}

}
