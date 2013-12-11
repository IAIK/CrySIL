
import proxys.CK_BYTE_ARRAY;
import proxys.CK_INFO;
import proxys.CK_MECHANISM;
import proxys.CK_MECHANISM_INFO;
import proxys.CK_SESSION_INFO;
import proxys.CK_SLOT_INFO;
import proxys.CK_TOKEN_INFO;
import proxys.CK_ULONG_ARRAY;
import proxys.pkcs11Constants;
import proxys.CK_VOID_PTR;
import proxys.CK_ULONG_PTR;
import proxys.CK_ATTRIBUTE;
import proxys.CK_NOTIFY_CALLBACK;

public class pkcs11Interface implements pkcs11Constants {
	
  public static long C_OpenSession(long slotID, long flags, CK_VOID_PTR pApplication, CK_NOTIFY_CALLBACK Notify, CK_ULONG_PTR phSession) {
	  return CKR_OK;
  }

  public static long C_CloseSession(long hSession) {
	  return CKR_OK;
  }

  public static long C_CloseAllSessions(long slotID) {
	  return CKR_OK;
  }

  public static long C_Initialize(CK_VOID_PTR pInitArgs) {
	  return CKR_OK;
  }

  public static long C_Login(long hSession, long userType, String pPin, long ulPinLen) {
	  return CKR_OK;
  }

  public static long C_Logout(long hSession) {
	  return CKR_OK;
  }

  public static long C_SetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[]  pTemplate, long ulCount) {
	  return CKR_OK;
  }

  public static long C_CreateObject(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount, CK_ULONG_PTR phObject) {
	  return CKR_OK;
  }

  public static long C_DecryptInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
	  return CKR_OK;
  }

  public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart, long ulEncryptedPartLen, CK_BYTE_ARRAY pPart, CK_ULONG_PTR pulPartLen) {
	  return CKR_OK;
  }

  public static long C_DestroyObject(long hSession, long hObject) {
	  return CKR_OK;
  }

  public static long C_Finalize(CK_VOID_PTR pReserved) {
	  return CKR_OK;
  }

  public static long C_FindObjects(long hSession, CK_ULONG_PTR phObject, long ulMaxObjectCount, CK_ULONG_PTR pulObjectCount) {
	  return CKR_OK;
  }

  public static long C_FindObjectsFinal(long hSession) {
	  return CKR_OK;
  }

  public static long C_FindObjectsInit(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount) {
	  return CKR_OK;
  }

  public static long C_GenerateRandom(long hSession, byte[] RandomData, long ulRandomLen) {
	  return CKR_OK;
  }

  public static long C_GetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[] pTemplate, long ulCount) {
	  return CKR_OK;
  }

  public static long C_GetInfo(CK_INFO pInfo) {
	  return CKR_OK;
  }

  public static long C_GetMechanismInfo(long slotID, long type, CK_MECHANISM_INFO pInfo) {
	  return CKR_OK;
  }

  public static long C_GetMechanismList(long slotID, CK_ULONG_ARRAY pMechanismList, CK_ULONG_PTR pulCount) {
	  return CKR_OK;
  }

  public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
	  return CKR_OK;
  }

  public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
	  return CKR_OK;
  }

  public static long C_GetSlotList(short tokenPresent, CK_ULONG_ARRAY pSlotList, CK_ULONG_PTR pulCount) {
	  return CKR_OK;
  }

  public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
	  return CKR_OK;
  }


  public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
	  return CKR_OK;
  }

  public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen, String pNewPin, long ulNewLen) {
	  return CKR_OK;
  }

  public static long C_Sign(long hSession, byte[] pData, long ulDataLen, CK_BYTE_ARRAY pSignature, CK_ULONG_PTR pulSignatureLen) {
	  return CKR_OK;
  }

  public static long C_SignInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
	  return CKR_OK;
  }

  public static long C_UnwrapKey(long hSession, CK_MECHANISM pMechanism, long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen, CK_ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_PTR phKey) {
	  return CKR_OK;
  }

  public static long C_WrapKey(long hSession, CK_MECHANISM pMechanism, long hWrappingKey, long hKey, CK_BYTE_ARRAY pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen) {
	  return CKR_OK;
  }

}
