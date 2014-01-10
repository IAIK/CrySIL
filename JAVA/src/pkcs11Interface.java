
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.org.apache.bcel.internal.generic.RET;

import proxys.CK_BYTE_ARRAY;
import proxys.CK_INFO;
import proxys.CK_MECHANISM;
import proxys.CK_MECHANISM_INFO;
import proxys.CK_NOTIFY_CALLBACK;
import proxys.CK_SESSION_INFO;
import proxys.CK_SLOT_INFO;
import proxys.CK_TOKEN_INFO;
import proxys.CK_ULONG_ARRAY;
import proxys.RETURN_TYPE;
import proxys.pkcs11Constants;
import proxys.CK_VOID_PTR;
import proxys.CK_ULONG_PTR;
import proxys.CK_ATTRIBUTE;
import proxys.CK_NOTIFY_CALLBACK;

public class pkcs11Interface implements pkcs11Constants {

	private static ResourceManager rm = null;
	private static ResourceManager getRM() throws PKCS11Error{
		if(rm == null){
			throw new PKCS11Error(RETURN_TYPE.GENERAL_ERROR);
		}
		return rm;
	}
	
	public static long C_Initialize(CK_VOID_PTR  pInitArgs){
		String appID = "newRandomID";
		if(rm != null){
			RETURN_TYPE.FUNCTION_FAILED.swigValue();
		}
		
		rm = new ResourceManager(appID);
		
		return RETURN_TYPE.OK.swigValue();
	}
  public static long C_OpenSession(long slotID, long flags, CK_VOID_PTR pApplication, CK_NOTIFY_CALLBACK Notify, CK_ULONG_PTR phSession) {
	  /* v0.1 */
	  //public session erstellen
	  /* v0.2 */	  
	  //public session erstellen
	  //in proxy gui: 	create Eintrag f. application
	  //				show to which user-server pair the application wants to connect
	  //				create one time pass
	  
	  
	//	  #define CKF_EXCLUSIVE_SESSION   0x00000001  /* session is exclusive */
	//	  #define CKF_RW_SESSION          0x00000002  /* session is read/write */
	//	  #define CKF_SERIAL_SESSION      0x00000004  /* doesn't support parallel */
	//	  /* CKF_INSERTION_CALLBACK is new for v2.0.  If it is set in the
	//	   * flags supplied to a C_OpenSession call, then instead of actually
	//	   * opening a session, the call is a request to get a callback when
	//	   * the token is inserted. */
	//	  #define CKF_INSERTION_CALLBACK  0x00000008  /* app. gets insertion notice */

	  if(! Util.isFlagSet(flags, CKF_SERIAL_SESSION)){
		 return RETURN_TYPE.GENERAL_ERROR.swigValue(); //CKR_PARALLEL_NOT_SUPPORTED
	  }
//	  Util.isFlagSet(flags, CKF_INSERTION_CALLBACK);
//	  Util.isFlagSet(flags, CKF_EXCLUSIVE_SESSION);
	  Session.ACCESS_TYPE atype = Session.ACCESS_TYPE.RO;
	  if(Util.isFlagSet(flags, CKF_RW_SESSION)){
		  atype = Session.ACCESS_TYPE.RW;
	  }
	  try {
		  phSession.assign(getRM().newSession(slotID, atype));
	  } catch (PKCS11Error e) {
		  return e.getCode();
	  } 
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
	  //if user is auth to skytrust -> Token is present
		  
	  Slot s = getRM().getSlotByID(slotID);
	  long flags = Util.initFlags;
	  if(s.isTokenPresent()){
		  Util.setFlag(flags, CKF_TOKEN_PRESENT);
	  }
	  pInfo.setFlags(flags);
	  pInfo.setManufacturerID("IAIK Skytrust");
	  pInfo.setSlotDescription(slot.getServerName());
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetSlotList(short tokenPresent, CK_ULONG_ARRAY pSlotList, CK_ULONG_PTR pulCount) {
	  try {
		ArrayList<Slot> slotlist = null;
		slotlist = getRM().getSlotList();			
		if(tokenPresent == 1) {
			Iterator<Slot> it = slotlist.iterator();
			while(it.hasNext()){
				if(!it.next().isTokenPresent()){
					it.remove();
				}
			}
		}
		int buffersize = (int) pulCount.value();
		
		pulCount.assign(slotlist.size());
		if(pSlotList.getCPtr() != 0){
			if(buffersize < slotlist.size()){
				return RETURN_TYPE.BUFFER_TOO_SMALL.swigValue();
			}else{
				pulCount.assign(slotlist.size());
				Iterator<Slot> it = slotlist.iterator();
				for(int i=0;it.hasNext();i++){
					pSlotList.setitem(i, it.next().getID());
				}
			}
		}
	} catch (PKCS11Error e) {
		e.printStackTrace();
		return e.getCode();
	}  
	return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
	  return RETURN_TYPE.OK.swigValue();
  }
  public static long C_Login(long hSession, long userType, String pPin, long ulPinLen) {
	  /* v0.1 */
	  //pPin = user:password for skytrust server
	  /* v0.2 */
	  //pPin = one time password showed in in the skytrust gui 
	  //in proxy: 	if correct change to user session 
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_Logout(long hSession) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_CloseSession(long hSession) {
	  return RETURN_TYPE.OK.swigValue();
  }
  
  public static long C_CloseAllSessions(long slotID) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[]  pTemplate, long ulCount) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_CreateObject(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount, CK_ULONG_PTR phObject) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_DecryptInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart, long ulEncryptedPartLen, CK_BYTE_ARRAY pPart, CK_ULONG_PTR pulPartLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_DestroyObject(long hSession, long hObject) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_Finalize(CK_VOID_PTR pReserved) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjects(long hSession, CK_ULONG_PTR phObject, long ulMaxObjectCount, CK_ULONG_PTR pulObjectCount) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjectsFinal(long hSession) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjectsInit(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GenerateRandom(long hSession, byte[] RandomData, long ulRandomLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[] pTemplate, long ulCount) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetInfo(CK_INFO pInfo) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetMechanismInfo(long slotID, long type, CK_MECHANISM_INFO pInfo) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetMechanismList(long slotID, CK_ULONG_ARRAY pMechanismList, CK_ULONG_PTR pulCount) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen, String pNewPin, long ulNewLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_Sign(long hSession, byte[] pData, long ulDataLen, CK_BYTE_ARRAY pSignature, CK_ULONG_PTR pulSignatureLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SignInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_UnwrapKey(long hSession, CK_MECHANISM pMechanism, long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen, CK_ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_PTR phKey) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_WrapKey(long hSession, CK_MECHANISM pMechanism, long hWrappingKey, long hKey, CK_BYTE_ARRAY pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

}
