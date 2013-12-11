import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.CK_INFO;
import proxys.CK_MECHANISM;
import proxys.CK_MECHANISM_INFO;
import proxys.CK_NOTIFY_CALLBACK;
import proxys.CK_SESSION_HANDLE_PTR;
import proxys.CK_SESSION_INFO;
import proxys.CK_SLOT_INFO;
import proxys.CK_TOKEN_INFO;
import proxys.CK_ULONG_ARRAY;
import proxys.CK_ULONG_PTR;
import proxys.CK_VOID_PTR;
import proxys.pkcs11Constants;

import proxys.pkcs11Constants;

public class pkcs11Interface implements pkcs11Constants {


  public static long C_OpenSession(long slotID, long flags, CK_VOID_PTR pApplication, CK_NOTIFY_CALLBACK Notify, CK_SESSION_HANDLE_PTR phSession) {
  return 0; 
  }

  public static long C_CloseSession(long hSession) {
  return 0; 
  }

  public static long C_CloseAllSessions(long slotID) {
  return 0; 
  }

  public static long C_Initialize(CK_VOID_PTR pInitArgs) {
  return 0; 
  }

  public static long C_Login(long hSession, long userType, String pPin, long ulPinLen) {
  return 0; 
  }

  public static long C_Logout(long hSession) {
  return 0; 
  }


  public static long C_SetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[]  pTemplate, long ulCount) {
  return 0; 
  }

  public static long C_CreateObject(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount, CK_ULONG_PTR phObject) {
  return 0; 
  }

  public static long C_DecryptInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
  return 0; 
  }

  public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart, long ulEncryptedPartLen, CK_BYTE_ARRAY pPart, CK_ULONG_PTR pulPartLen) {
  return 0; 
  }

  public static long C_DestroyObject(long hSession, long hObject) {
  return 0; 
  }

  public static long C_Finalize(CK_VOID_PTR pReserved) {
  return 0; 
  }

  public static long C_FindObjects(long hSession, CK_ULONG_PTR phObject, long ulMaxObjectCount, CK_ULONG_PTR pulObjectCount) {
  return 0; 
  }

  public static long C_FindObjectsFinal(long hSession) {
  return 0; 
  }

  public static long C_FindObjectsInit(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount) {
  return 0; 
  }

  public static long C_GenerateRandom(long hSession, byte[] RandomData, long ulRandomLen) {
  return 0; 
  }

  public static long C_GetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[] pTemplate, long ulCount) {
  return 0; 
  }

  public static long C_GetInfo(CK_INFO pInfo) {
	  System.out.println("madness... java function called ..." + pInfo);
  return 0; 
  }

  public static long C_GetMechanismInfo(long slotID, long type, CK_MECHANISM_INFO pInfo) {
  return 0; 
  }

  public static long C_GetMechanismList(long slotID, CK_ULONG_ARRAY pMechanismList, CK_ULONG_PTR pulCount) {
  return 0; 
  }

  public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
  return 0; 
  }

  public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
  return 0; 
  }

  public static long C_GetSlotList(short tokenPresent, CK_ULONG_ARRAY pSlotList, CK_ULONG_PTR pulCount) {
  return 0; 
  }

  public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
  return 0; 
  }


  public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
  return 0; 
  }

  public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen, String pNewPin, long ulNewLen) {
  return 0; 
  }

  public static long C_Sign(long hSession, byte[] pData, long ulDataLen, CK_BYTE_ARRAY pSignature, CK_ULONG_PTR pulSignatureLen) {
  return 0; 
  }

  public static long C_SignInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
  return 0; 
  }

  public static long C_UnwrapKey(long hSession, CK_MECHANISM pMechanism, long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen, CK_ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_PTR phKey) {
  return 0; 
  }

  public static long C_WrapKey(long hSession, CK_MECHANISM pMechanism, long hWrappingKey, long hKey, CK_BYTE_ARRAY pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen) {
  return 0; 
  }

}


