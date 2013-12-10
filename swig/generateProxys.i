%module pkcs11
%include "typemaps.i"
%include "cpointer.i"
%include "carrays.i"

/*alter default proxy classes for public access to cPtr*/
%typemap(javabody) SWIGTYPE, SWIGTYPE *, SWIGTYPE [], SWIGTYPE (CLASS::*) %{
  private long swigCPtr;
  protected boolean swigCMemOwn;
  
  public $javaclassname(long cPtr, boolean cMemoryOwn) {
    swigCPtr = cPtr;
    swigCMemOwn = cMemoryOwn;
  }

  public static long getCPtr($javaclassname obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }
%}

%apply char * { CK_CHAR [ANY]}

%{

/* Includes the header in the wrapper code */
#include "pkcs11t_processed.h"
/* #include "pkcs11f_funcdecl.h"	*/
/* #include "pkcs11f_funcpointer.h" */
%}



/* Parse the header file to generate wrappers */
%include "pkcs11t_processed.h"

%pointer_class(unsigned long int,CK_ULONG_PTR)
%pointer_class(void,CK_VOID_PTR)

	 CK_RV C_GetFunctionList(CK_FUNCTION_LIST_PTR_PTR ppFunctionList){

	}

	 CK_RV C_CloseAllSessions(CK_SLOT_ID slotID)
	{
	

	}
	 CK_RV C_CloseSession(CK_SESSION_HANDLE hSession)
	{
	

	}

	 CK_RV C_CreateObject(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phObject)
	{
	

	}
	 CK_RV C_DecryptInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
	{
	

	}
	 CK_RV C_DecryptUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
	{
	

	}
	 CK_RV C_DestroyObject(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject)
	{
	

	}
	 CK_RV C_Finalize(CK_VOID_PTR pReserved)
	{

	}
	 CK_RV C_FindObjects(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE_PTR phObject, CK_ULONG ulMaxObjectCount, CK_ULONG_PTR pulObjectCount)
	{
	

	}
	 CK_RV C_FindObjectsFinal(CK_SESSION_HANDLE hSession)
	{
	

	}
	 CK_RV C_FindObjectsInit(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
	{
	

	}
	 CK_RV C_GenerateRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR RandomData, CK_ULONG ulRandomLen)
	{
	

	}
	 CK_RV C_GetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
	{

	}
	 CK_RV C_GetInfo(CK_INFO_PTR pInfo)
	{
	}


	 CK_RV C_GetMechanismInfo(CK_SLOT_ID slotID, CK_MECHANISM_TYPE type, CK_MECHANISM_INFO_PTR pInfo)
	{
	

	}
	 CK_RV C_GetMechanismList(CK_SLOT_ID slotID, CK_MECHANISM_TYPE_PTR pMechanismList, CK_ULONG_PTR pulCount)
	{
	

	}
	 CK_RV C_GetSessionInfo(CK_SESSION_HANDLE hSession, CK_SESSION_INFO_PTR pInfo)
	{
	

	}
	 CK_RV C_GetSlotInfo(CK_SLOT_ID slotID, CK_SLOT_INFO_PTR pInfo)
	{
	

	}
	 CK_RV C_GetSlotList(CK_BBOOL tokenPresent, CK_SLOT_ID_PTR pSlotList, CK_ULONG_PTR pulCount)
	{
	

	}
	 CK_RV C_GetTokenInfo(CK_SLOT_ID slotID, CK_TOKEN_INFO_PTR pInfo)
	{
	
	}
	 CK_RV C_Initialize(CK_VOID_PTR pInitArgs)
	{
	

	}
	 CK_RV C_Login(CK_SESSION_HANDLE hSession, CK_USER_TYPE userType, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
	{
	

	}
	 CK_RV C_Logout(CK_SESSION_HANDLE hSession)
	{
	
}
	 CK_RV C_OpenSession(CK_SLOT_ID slotID, CK_FLAGS flags, CK_VOID_PTR pApplication, CK_NOTIFY Notify, CK_SESSION_HANDLE_PTR phSession)
	{
	

	}
	 CK_RV C_SeedRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSeed, CK_ULONG ulSeedLen)
	{
	

	}
	 CK_RV C_SetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
	{
	

	}
	 CK_RV C_SetPIN(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pOldPin, CK_ULONG ulOldLen, CK_CHAR_PTR pNewPin, CK_ULONG ulNewLen)
	{
	

	}
	 CK_RV C_Sign(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
	{
	

	}
	 CK_RV C_SignInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
	{
	

	}
	 CK_RV C_UnwrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hUnwrappingKey, CK_BYTE_PTR pWrappedKey, CK_ULONG ulWrappedKeyLen, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
	{
	

	}
	 CK_RV C_WrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hWrappingKey, CK_OBJECT_HANDLE hKey, CK_BYTE_PTR pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen)
	{
	

	}
	

