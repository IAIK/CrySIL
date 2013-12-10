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

