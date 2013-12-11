
import proxys.pkcs11Constants;

public class pkcs11Interface implements pkcs11Constants {


  public static long C_OpenSession(long slotID, long flags, CK_VOID_PTR pApplication, CK_NOTIFY_CALLBACK Notify, CK_SESSION_HANDLE_PTR phSession) {
  }

  public static long C_CloseSession(long hSession) {
  }

  public static long C_CloseAllSessions(long slotID) {
  }

  public static long C_Initialize(CK_VOID_PTR pInitArgs) {
  }

  public static long C_Login(long hSession, long userType, String pPin, long ulPinLen) {
  }

  public static long C_Logout(long hSession) {
  }


  public static long C_SetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[]  pTemplate, long ulCount) {
  }

  public static long C_CreateObject(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount, CK_ULONG_PTR phObject) {
  }

  public static long C_DecryptInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
  }

  public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart, long ulEncryptedPartLen, CK_BYTE_ARRAY pPart, CK_ULONG_PTR pulPartLen) {
  }

  public static long C_DestroyObject(long hSession, long hObject) {
  }

  public static long C_Finalize(CK_VOID_PTR pReserved) {
  }

  public static long C_FindObjects(long hSession, CK_ULONG_PTR phObject, long ulMaxObjectCount, CK_ULONG_PTR pulObjectCount) {
  }

  public static long C_FindObjectsFinal(long hSession) {
  }

  public static long C_FindObjectsInit(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount) {
  }

  public static long C_GenerateRandom(long hSession, byte[] RandomData, long ulRandomLen) {
  }

  public static long C_GetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[] pTemplate, long ulCount) {
  }

  public static long C_GetInfo(CK_INFO pInfo) {
  }

  public static long C_GetMechanismInfo(long slotID, long type, CK_MECHANISM_INFO pInfo) {
  }

  public static long C_GetMechanismList(long slotID, CK_ULONG_ARRAY pMechanismList, CK_ULONG_PTR pulCount) {
  }

  public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
  }

  public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
  }

  public static long C_GetSlotList(short tokenPresent, CK_ULONG_ARRAY pSlotList, CK_ULONG_PTR pulCount) {
  }

  public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
  }


  public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
  }

  public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen, String pNewPin, long ulNewLen) {
  }

  public static long C_Sign(long hSession, byte[] pData, long ulDataLen, CK_BYTE_ARRAY pSignature, CK_ULONG_PTR pulSignatureLen) {
  }

  public static long C_SignInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
  }

  public static long C_UnwrapKey(long hSession, CK_MECHANISM pMechanism, long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen, CK_ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_PTR phKey) {
  }

  public static long C_WrapKey(long hSession, CK_MECHANISM pMechanism, long hWrappingKey, long hKey, CK_BYTE_ARRAY pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen) {
  }

}
