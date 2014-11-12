#ifndef __Pkcs11Config_H
#define __Pkcs11Config_h


#define Tutorial_VERSION_MAJOR @Tutorial_VERSION_MAJOR@
#define Tutorial_VERSION_MINOR @Tutorial_VERSION_MINOR@

#ifndef WIN32
#include <pthread.h>
#define CK_PTR *
#define CK_DEFINE_FUNCTION(returnType, name)  returnType name
#define CK_DECLARE_FUNCTION(returnType, name) returnType name
#define CK_DECLARE_FUNCTION_POINTER(returnType, name) returnType (* name)
#define CK_CALLBACK_FUNCTION(returnType, name) returnType (* name)
#ifndef NULL_PTR
#define NULL_PTR 0
#include "pkcs11.h"

#endif

#else
#include <windows.h>
#include <process.h>

// PKCS#11 related stuff
#pragma pack(push, cryptoki, 1)


#define CK_IMPORT_SPEC __declspec(dllimport)
#define CRYPTOKI_EXPORTS
#ifdef CRYPTOKI_EXPORTS
#define CK_EXPORT_SPEC __declspec(dllexport)
#else
#define CK_EXPORT_SPEC CK_IMPORT_SPEC
#endif

#define CK_CALL_SPEC __cdecl

#define CK_PTR *
#define CK_DEFINE_FUNCTION(returnType, name) returnType CK_EXPORT_SPEC CK_CALL_SPEC name
#define CK_DECLARE_FUNCTION(returnType, name) returnType CK_EXPORT_SPEC CK_CALL_SPEC name
#define CK_DECLARE_FUNCTION_POINTER(returnType, name) returnType CK_IMPORT_SPEC (CK_CALL_SPEC CK_PTR name)
#define CK_CALLBACK_FUNCTION(returnType, name) returnType (CK_CALL_SPEC CK_PTR name)

#ifndef NULL_PTR
#define NULL_PTR 0
#endif

#include "pkcs11.h"

#pragma pack(pop, cryptoki)

// Platform dependend type for dynamically loaded library handle
typedef HINSTANCE DLHANDLE;
// Platform dependend function that loads dynamic library
#define DLOPEN(lib) LoadLibraryA((lib))
// Platform dependend function that gets function pointer from dynamic library
#define DLSYM(lib, func) GetProcAddress((lib), (func))
// Platform dependend function that unloads dynamic library
#define DLCLOSE FreeLibrary

// Platform dependend function for case insensitive string comparison
#define STRCASECMP _stricmp
// Platform dependend function for string duplication
#define STRDUP _strdup






#endif
#endif
