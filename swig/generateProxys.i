%module pkcs11
%{
#include "pkcs11.h"

typedef struct {
  CK_NOTIFY func;
} CK_NOTIFY_CALLBACK;
%}
%javaconst(1);
%include "typemaps.i"
%include "cpointer.i"
%include "carrays.i"

%typemap(javainterfaces, notderived="1") SWIGTYPE  "StructBase"
%typemap(javainterfaces, notderived="1") enum SWIGTYPE "EnumBase"

/*alter default proxy classes for public access to cPtr*/
%typemap(javabody) SWIGTYPE, SWIGTYPE *, SWIGTYPE [], SWIGTYPE (CLASS::*) %{
  private long swigCPtr;
  protected boolean swigCMemOwn;
  
  public $javaclassname(long cPtr, boolean cMemoryOwn) {
    swigCPtr = cPtr;
    swigCMemOwn = cMemoryOwn;
  }
  public long getCPtr() {
    return swigCPtr;
  }
  public boolean isNullPtr() {
    return (swigCPtr == 0L);
  }
  public static long getCPtr($javaclassname obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }
%}


/* Parse the header file to generate wrappers */

%apply char[] { CK_CHAR_PTR }
%apply char[] { CK_UTF8CHAR[ANY],CK_CHAR[ANY] }
%typemap(javaimports) SWIGTYPE %{
	import pkcs11.Util;
	import objects.StructBase;
%}
%typemap(javaimports) enum SWIGTYPE %{
	import objects.EnumBase;
%}
%typemap(javain)  CK_UTF8CHAR[ANY],CK_CHAR[ANY] %{ /*JAVAIN*/ Util.fixStringLen($javainput,$1_dim0)
%}
%typemap(memberin)  CK_UTF8CHAR[ANY],CK_CHAR[ANY] { //MEMBERIN 
  if ($input) {
  	memmove($1,$input,$1_dim0);
  }
}

//%typemap(jstype) CK_UTF8CHAR[ANY],CK_CHAR[ANY] %{ /*jstype*/ String %}
//%typemap(jtype) CK_UTF8CHAR[ANY],CK_CHAR[ANY] %{ /*jtype*/ jstring %}
//%typemap(jni) CK_UTF8CHAR[ANY],CK_CHAR[ANY] %{ /*jni*/ jstring %}


%apply unsigned long int {CK_ATTRIBUTE_TYPE, CK_MECHANISM_TYPE}

%pointer_class(unsigned long int,CK_ULONG_JPTR)

%array_class(unsigned long int,CK_ULONG_ARRAY)
%array_class(CK_CHAR,CK_BYTE_ARRAY)

%include "pkcs11t_processed.h"
%include "CKA_enum.h"
%include "CKC_enum.h"
%include "CKS_enum.h"
%include "CKK_enum.h"
%include "CKM_enum.h"
%include "CKR_enum.h"
%include "CKO_enum.h"


typedef struct {
  CK_NOTIFY func;
} CK_NOTIFY_CALLBACK;

%extend CK_NOTIFY_CALLBACK {
CK_RV call(CK_SESSION_HANDLE para1,CK_NOTIFICATION para2,CK_VOID_PTR para3){
  return self->func(para1,para2,para3);
}
}


CK_RV C_CloseAllSessions(CK_SLOT_ID slotID);

CK_RV C_CloseSession(CK_SESSION_HANDLE hSession);

CK_RV C_Finalize(CK_VOID_PTR pReserved);

CK_RV C_OpenSession(CK_SLOT_ID slotID, CK_FLAGS flags, CK_VOID_PTR pApplication, CK_NOTIFY Notify, CK_SESSION_HANDLE_PTR phSession);

CK_RV C_GetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount);
	 
	 CK_RV C_GetInfo(CK_INFO_PTR pInfo);

	 CK_RV C_Initialize(CK_VOID_PTR pInitArgs);
	 
	 CK_RV C_Login(CK_SESSION_HANDLE hSession, CK_USER_TYPE userType, CK_CHAR_PTR pPin, CK_ULONG ulPinLen);
	 
	 CK_RV C_Logout(CK_SESSION_HANDLE hSession);
	 
	 CK_RV C_SetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
	{
	

	}
	 CK_RV C_SetPIN(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pOldPin, CK_ULONG ulOldLen, CK_CHAR_PTR pNewPin, CK_ULONG ulNewLen)
	{
	

	}
	 CK_RV C_GetSessionInfo(CK_SESSION_HANDLE hSession, CK_SESSION_INFO_PTR pInfo)
	{
	}
		
	 CK_RV C_GetSlotInfo(CK_SLOT_ID slotID, CK_SLOT_INFO_PTR pInfo);

	 CK_RV C_GetSlotList(CK_BBOOL tokenPresent, CK_SLOT_ID_PTR pSlotList, CK_ULONG_PTR pulCount);

	 CK_RV C_GetTokenInfo(CK_SLOT_ID slotID, CK_TOKEN_INFO_PTR pInfo);

	 CK_RV C_GetMechanismInfo(CK_SLOT_ID slotID, CK_MECHANISM_TYPE type, CK_MECHANISM_INFO_PTR pInfo)
	{
	

	}
	 CK_RV C_GetMechanismList(CK_SLOT_ID slotID, CK_MECHANISM_TYPE_PTR pMechanismList, CK_ULONG_PTR pulCount)
	{
	

	}
CK_RV C_FindObjects(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE_PTR phObject, CK_ULONG ulMaxObjectCount, CK_ULONG_PTR pulObjectCount);

	 CK_RV C_FindObjectsFinal(CK_SESSION_HANDLE hSession)
	{
	

	}
	 CK_RV C_FindObjectsInit(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
	{
	

	}
		 CK_RV C_CreateObject(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phObject)
	{
	

	}

	 CK_RV C_DestroyObject(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject)
	{
	

	}
	
	
	
	 CK_RV C_GenerateRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR RandomData, CK_ULONG ulRandomLen)
	{
	

	}
	 CK_RV C_SeedRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSeed, CK_ULONG ulSeedLen)
	{
	

	}
	 CK_RV C_SignInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
	{
	}
CK_RV C_Sign(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
	{
	}
CK_RV C_DecryptInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
	{
	}
	
	
	 CK_RV C_DecryptUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
	{
	

	}
	 CK_RV C_UnwrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hUnwrappingKey, CK_BYTE_PTR pWrappedKey, CK_ULONG ulWrappedKeyLen, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
	{
	

	}
	 CK_RV C_WrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hWrappingKey, CK_OBJECT_HANDLE hKey, CK_BYTE_PTR pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen)
	{
	

	}
	

