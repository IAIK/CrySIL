%module pkcs11
%javaconst(1);
%include "typemaps.i"
%include "cpointer.i"
%include "carrays.i"
%include "enums.swg"
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
  public static long getCPtr($javaclassname obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }
%}



%define %mypointer(TYPE, NAME)
%{
typedef TYPE NAME;
%}
typedef struct {
} NAME;

%extend NAME {
void assign(TYPE value) {
  *self = value;
}
TYPE value() {
  return *self;
}
TYPE value() {
  return *self;
}
}
%types(NAME = TYPE);
%enddef

typedef struct {
  CK_NOTIFY func;
} CK_NOTIFY_CALLBACK;

%extend CK_NOTIFY_CALLBACK {
CK_RV call(CK_SESSION_HANDLE para1,CK_NOTIFICATION para2,CK_VOID_PTR para3){
  return func(para1,para2,para3);
}
}


%typemap(jni) char "jbyte"
%typemap(jtype) char "byte"
%typemap(jstype) char "byte"
%mypointer(char,CK_VOID_PTR)
%include "java.swg"           //reset old typemaps

//%typemap(jni) CK_VOID_PTR "jlong"
//%typemap(jtype) CK_VOID_PTR "long"
%typemap(jstype) CK_VOID_PTR "CK_VOID_PTR"
%typemap(javain) CK_VOID_PTR "CK_VOID_PTR.getCPtr($javainput)"

%typemap(jstype) CK_OBJECT_HANDLE_PTR "CK_ULONG_PTR"
%typemap(javain) CK_OBJECT_HANDLE_PTR "CK_ULONG_PTR.getCPtr($javainput)"



%{
/* Includes the header in the wrapper code */
#include "pkcs11t_processed.h"
#include "CKA_enum.h"
#include "CKC_enum.h"
#include "CKS_enum.h"
#include "CKM_enum.h"
/* #include "pkcs11f_funcdecl.h"	*/
/* #include "pkcs11f_funcpointer.h" */
%}

/* Parse the header file to generate wrappers */
%include "pkcs11t_processed.h"
%include "CKA_enum.h"
%include "CKC_enum.h"
%include "CKS_enum.h"
%include "CKM_enum.h"

%apply char * { CK_CHAR_PTR }



%define %myarray(TYPE,NAME)
%{
typedef TYPE NAME;
%}
typedef struct NAME {
  CK_ULONG len;
} NAME;
%extend NAME {
NAME(CK_ULONG nelements) {
  len = nelements;
  
}
~NAME() {
}
TYPE getitem(int index) {
  if(index < len)
    return self[index];
  return 0;
}
void setitem(int index, TYPE value) {
  if(index < len)
    self[index] = value;
}
};
%types(NAME = TYPE);
%enddef

%myarray(CK_BYTE,CK_BYTE_ARRAY)
%myarray(CK_ULONG,CK_ULONG_ARRAY)
%pointer_class(unsigned long int,CK_ULONG_PTR)

%pointer_class(CK_SESSION_HANDLE,CK_SESSION_HANDLE_PTR)


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
	

