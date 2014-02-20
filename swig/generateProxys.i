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
%extend CK_BYTE_ARRAY {
	size_t size;
}
 %{ 
 size_t CK_BYTE_ARRAY_size_get(){ 
    return sizeof(void*); 
 } 
 void CK_BYTE_ARRAY_size_set(CK_BYTE_ARRAY* obj,size_t s){ return; } 
 %} 
 
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


