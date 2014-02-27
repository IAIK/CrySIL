#include"pkcs11.h"
#include"jvm.h"
#include"unistd.h"
#include<string.h>
#include"stdio.h"

static CK_FUNCTION_LIST pkcs11_functions =  { {2, 20},
 &C_Initialize,
 &C_Finalize,
 &C_GetInfo,
 &C_GetFunctionList,
 &C_GetSlotList,
 &C_GetSlotInfo,
 &C_GetTokenInfo,
 &C_GetMechanismList,
 &C_GetMechanismInfo,
 &C_InitToken,
 &C_InitPIN,
 &C_SetPIN,
 &C_OpenSession,
 &C_CloseSession,
 &C_CloseAllSessions,
 &C_GetSessionInfo,
 &C_GetOperationState,
 &C_SetOperationState,
 &C_Login,
 &C_Logout,
 &C_CreateObject,
 &C_CopyObject,
 &C_DestroyObject,
 &C_GetObjectSize,
 &C_GetAttributeValue,
 &C_SetAttributeValue,
 &C_FindObjectsInit,
 &C_FindObjects,
 &C_FindObjectsFinal,
 &C_EncryptInit,
 &C_Encrypt,
 &C_EncryptUpdate,
 &C_EncryptFinal,
 &C_DecryptInit,
 &C_Decrypt,
 &C_DecryptUpdate,
 &C_DecryptFinal,
 &C_DigestInit,
 &C_Digest,
 &C_DigestUpdate,
 &C_DigestKey,
 &C_DigestFinal,
 &C_SignInit,
 &C_Sign,
 &C_SignUpdate,
 &C_SignFinal,
 &C_SignRecoverInit,
 &C_SignRecover,
 &C_VerifyInit,
 &C_Verify,
 &C_VerifyUpdate,
 &C_VerifyFinal,
 &C_VerifyRecoverInit,
 &C_VerifyRecover,
 &C_DigestEncryptUpdate,
 &C_DecryptDigestUpdate,
 &C_SignEncryptUpdate,
 &C_DecryptVerifyUpdate,
 &C_GenerateKey,
 &C_GenerateKeyPair,
 &C_WrapKey,
 &C_UnwrapKey,
 &C_DeriveKey,
 &C_SeedRandom,
 &C_GenerateRandom,
 &C_GetFunctionStatus,
 &C_CancelFunction,
 &C_WaitForSlotEvent }; 

CK_RV C_GetFunctionList(CK_FUNCTION_LIST_PTR_PTR ppFunctionList){ *ppFunctionList=&pkcs11_functions; return CKR_OK; }

CK_RV C_CloseAllSessions(CK_SLOT_ID slotID)
{ /*printf("\nC: called: C_CloseAllSessions    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_CloseAllSessionsJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_CloseAllSessions", "(J)J");
        
        if(C_CloseAllSessionsJava !=0)
        {

 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_CloseAllSessionsJava, slotID);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_CloseSession(CK_SESSION_HANDLE hSession)
{ /*printf("\nC: called: C_CloseSession    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_CloseSessionJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_CloseSession", "(J)J");
        
        if(C_CloseSessionJava !=0)
        {

 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_CloseSessionJava, hSession);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_CreateObject(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phObject)
{ /*printf("\nC: called: C_CreateObject    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_CreateObjectJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_CreateObject", "(J[Lobjects/ATTRIBUTE;JLproxys/CK_ULONG_JPTR;)J");
        
        if(C_CreateObjectJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls1 = (*(dings->env))->FindClass(dings->env, "objects/ATTRIBUTE"); //
jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
jobject obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pTemplate, JNI_FALSE);
 (*(dings->env))->ExceptionDescribe(dings->env);




array = (*(dings->env))->NewObjectArray(dings->env,size,cls1, obj1);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pTemplate+i, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);


if (pTemplate+i == NULL){
 obj1 = NULL; 

}
 (*(dings->env))->SetObjectArrayElement(dings->env, array,i, obj1);
 (*(dings->env))->ExceptionDescribe(dings->env);

}

}else{
 array = NULL;
}jobject obj3;
 if(phObject != NULL) { jclass cls3 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor3 = (*(dings->env))->GetMethodID(dings->env, cls3, "<init>", "(JZ)V");
                                obj3=(*(dings->env))->NewObject(dings->env, cls3, constructor3, phObject, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_CreateObjectJava, hSession, array, ulCount, obj3);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_DecryptInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ /*printf("\nC: called: C_DecryptInit    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_DecryptInitJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_DecryptInit", "(JLobjects/MECHANISM;J)J");
        
        if(C_DecryptInitJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_DecryptInitJava, hSession, obj1, hKey);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_DecryptUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{ /*printf("\nC: called: C_DecryptUpdate    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_DecryptUpdateJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_DecryptUpdate", "(J[BJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_DecryptUpdateJava !=0)
        {
jintArray result;
result = (*(dings->env))->NewIntArray(dings->env, ulEncryptedPartLen);

 (*(dings->env))->ExceptionDescribe(dings->env);
int j;
jint fill[ulEncryptedPartLen];
for (j = 0; j < ulEncryptedPartLen; j++) {
fill[j] =pEncryptedPart[j]; }
(*(dings->env))->SetIntArrayRegion(dings->env, result, 0, ulEncryptedPartLen, fill);
jobject obj3;
 if(pPart != NULL) { jclass cls3 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor3 = (*(dings->env))->GetMethodID(dings->env, cls3, "<init>", "(JZ)V");
                                obj3=(*(dings->env))->NewObject(dings->env, cls3, constructor3, pPart, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }jobject obj4;
 if(pulPartLen != NULL) { jclass cls4 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor4 = (*(dings->env))->GetMethodID(dings->env, cls4, "<init>", "(JZ)V");
                                obj4=(*(dings->env))->NewObject(dings->env, cls4, constructor4, pulPartLen, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_DecryptUpdateJava, hSession, result, ulEncryptedPartLen, obj3, obj4);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_DestroyObject(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject)
{ /*printf("\nC: called: C_DestroyObject    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_DestroyObjectJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_DestroyObject", "(JJ)J");
        
        if(C_DestroyObjectJava !=0)
        {

 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_DestroyObjectJava, hSession, hObject);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_Finalize(CK_VOID_PTR pReserved)
{ /*printf("\nC: called: C_Finalize    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_FinalizeJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_Finalize", "(Lproxys/CK_BYTE_ARRAY;)J");
        
        if(C_FinalizeJava !=0)
        {
jobject obj0;
 if(pReserved != NULL) { jclass cls0 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor0 = (*(dings->env))->GetMethodID(dings->env, cls0, "<init>", "(JZ)V");
                                obj0=(*(dings->env))->NewObject(dings->env, cls0, constructor0, pReserved, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj0==NULL){
                                }else{
				   }
} else{ obj0=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_FinalizeJava, obj0);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}
destroyVM();
return retVal;
}


CK_RV C_FindObjects(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE_PTR phObject, CK_ULONG ulMaxObjectCount, CK_ULONG_PTR pulObjectCount)
{ /*printf("\nC: called: C_FindObjects    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_FindObjectsJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_FindObjects", "(JLproxys/CK_ULONG_ARRAY;JLproxys/CK_ULONG_JPTR;)J");
        
        if(C_FindObjectsJava !=0)
        {
jobject obj1;
 if(phObject != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_ARRAY"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, phObject, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj3;
 if(pulObjectCount != NULL) { jclass cls3 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor3 = (*(dings->env))->GetMethodID(dings->env, cls3, "<init>", "(JZ)V");
                                obj3=(*(dings->env))->NewObject(dings->env, cls3, constructor3, pulObjectCount, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_FindObjectsJava, hSession, obj1, ulMaxObjectCount, obj3);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_FindObjectsFinal(CK_SESSION_HANDLE hSession)
{ /*printf("\nC: called: C_FindObjectsFinal    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_FindObjectsFinalJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_FindObjectsFinal", "(J)J");
        
        if(C_FindObjectsFinalJava !=0)
        {

 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_FindObjectsFinalJava, hSession);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_FindObjectsInit(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{ /*printf("\nC: called: C_FindObjectsInit    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_FindObjectsInitJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_FindObjectsInit", "(J[Lobjects/ATTRIBUTE;J)J");
        
        if(C_FindObjectsInitJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls1 = (*(dings->env))->FindClass(dings->env, "objects/ATTRIBUTE"); //
jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
jobject obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pTemplate, JNI_FALSE);
 (*(dings->env))->ExceptionDescribe(dings->env);




array = (*(dings->env))->NewObjectArray(dings->env,size,cls1, obj1);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pTemplate+i, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);


if (pTemplate+i == NULL){
 obj1 = NULL; 

}
 (*(dings->env))->SetObjectArrayElement(dings->env, array,i, obj1);
 (*(dings->env))->ExceptionDescribe(dings->env);

}

}else{
 array = NULL;
}
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_FindObjectsInitJava, hSession, array, ulCount);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GenerateRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR RandomData, CK_ULONG ulRandomLen)
{ /*printf("\nC: called: C_GenerateRandom    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GenerateRandomJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GenerateRandom", "(JLproxys/CK_BYTE_ARRAY;J)J");
        
        if(C_GenerateRandomJava !=0)
        {
jobject obj1;
 if(RandomData != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, RandomData, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GenerateRandomJava, hSession, obj1, ulRandomLen);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{ /*printf("\nC: called: C_GetAttributeValue    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetAttributeValueJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetAttributeValue", "(JJ[Lobjects/ATTRIBUTE;J)J");
        
        if(C_GetAttributeValueJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls2 = (*(dings->env))->FindClass(dings->env, "objects/ATTRIBUTE"); //
jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
jobject obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pTemplate, JNI_FALSE);
 (*(dings->env))->ExceptionDescribe(dings->env);




array = (*(dings->env))->NewObjectArray(dings->env,size,cls2, obj2);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pTemplate+i, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);


if (pTemplate+i == NULL){
 obj2 = NULL; 

}
 (*(dings->env))->SetObjectArrayElement(dings->env, array,i, obj2);
 (*(dings->env))->ExceptionDescribe(dings->env);

}

}else{
 array = NULL;
}
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetAttributeValueJava, hSession, hObject, array, ulCount);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetInfo(CK_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetInfo    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetInfoJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetInfo", "(Lproxys/CK_INFO;)J");
        
        if(C_GetInfoJava !=0)
        {
jobject obj0;
 if(pInfo != NULL) { jclass cls0 = (*(dings->env))->FindClass(dings->env, "proxys/CK_INFO"); //
         jmethodID constructor0 = (*(dings->env))->GetMethodID(dings->env, cls0, "<init>", "(JZ)V");
                                obj0=(*(dings->env))->NewObject(dings->env, cls0, constructor0, pInfo, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj0==NULL){
                                }else{
				   }
} else{ obj0=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetInfoJava, obj0);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetMechanismInfo(CK_SLOT_ID slotID, CK_MECHANISM_TYPE type, CK_MECHANISM_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetMechanismInfo    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetMechanismInfoJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetMechanismInfo", "(JJLproxys/CK_MECHANISM_INFO;)J");
        
        if(C_GetMechanismInfoJava !=0)
        {
jobject obj2;
 if(pInfo != NULL) { jclass cls2 = (*(dings->env))->FindClass(dings->env, "proxys/CK_MECHANISM_INFO"); //
         jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");
                                obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pInfo, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetMechanismInfoJava, slotID, type, obj2);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetMechanismList(CK_SLOT_ID slotID, CK_MECHANISM_TYPE_PTR pMechanismList, CK_ULONG_PTR pulCount)
{ /*printf("\nC: called: C_GetMechanismList    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetMechanismListJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetMechanismList", "(JLproxys/CK_ULONG_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_GetMechanismListJava !=0)
        {
jobject obj1;
 if(pMechanismList != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_ARRAY"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pMechanismList, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj2;
 if(pulCount != NULL) { jclass cls2 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");
                                obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pulCount, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetMechanismListJava, slotID, obj1, obj2);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetSessionInfo(CK_SESSION_HANDLE hSession, CK_SESSION_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetSessionInfo    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetSessionInfoJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetSessionInfo", "(JLproxys/CK_SESSION_INFO;)J");
        
        if(C_GetSessionInfoJava !=0)
        {
jobject obj1;
 if(pInfo != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_SESSION_INFO"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pInfo, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetSessionInfoJava, hSession, obj1);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetSlotInfo(CK_SLOT_ID slotID, CK_SLOT_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetSlotInfo    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetSlotInfoJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetSlotInfo", "(JLproxys/CK_SLOT_INFO;)J");
        
        if(C_GetSlotInfoJava !=0)
        {
jobject obj1;
 if(pInfo != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_SLOT_INFO"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pInfo, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetSlotInfoJava, slotID, obj1);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetSlotList(CK_BBOOL tokenPresent, CK_SLOT_ID_PTR pSlotList, CK_ULONG_PTR pulCount)
{ /*printf("\nC: called: C_GetSlotList    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetSlotListJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetSlotList", "(SLproxys/CK_ULONG_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_GetSlotListJava !=0)
        {
jobject obj1;
 if(pSlotList != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_ARRAY"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pSlotList, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj2;
 if(pulCount != NULL) { jclass cls2 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");
                                obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pulCount, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetSlotListJava, tokenPresent, obj1, obj2);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_GetTokenInfo(CK_SLOT_ID slotID, CK_TOKEN_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetTokenInfo    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_GetTokenInfoJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_GetTokenInfo", "(JLproxys/CK_TOKEN_INFO;)J");
        
        if(C_GetTokenInfoJava !=0)
        {
jobject obj1;
 if(pInfo != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_TOKEN_INFO"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pInfo, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_GetTokenInfoJava, slotID, obj1);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_Initialize(CK_VOID_PTR pInitArgs)
{ /*printf("\nC: called: C_Initialize    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_InitializeJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_Initialize", "(Lproxys/CK_BYTE_ARRAY;)J");
        
        if(C_InitializeJava !=0)
        {
jobject obj0;
 if(pInitArgs != NULL) { jclass cls0 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor0 = (*(dings->env))->GetMethodID(dings->env, cls0, "<init>", "(JZ)V");
                                obj0=(*(dings->env))->NewObject(dings->env, cls0, constructor0, pInitArgs, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj0==NULL){
                                }else{
				   }
} else{ obj0=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_InitializeJava, obj0);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_Login(CK_SESSION_HANDLE hSession, CK_USER_TYPE userType, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
{ /*printf("\nC: called: C_Login    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_LoginJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_Login", "(JJLjava/lang/String;J)J");
        
        if(C_LoginJava !=0)
        {
			jobject string2;
			string2 =(*(dings->env))->NewStringUTF(dings->env, pPin);
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_LoginJava, hSession, userType, string2, ulPinLen);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_Logout(CK_SESSION_HANDLE hSession)
{ /*printf("\nC: called: C_Logout    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_LogoutJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_Logout", "(J)J");
        
        if(C_LogoutJava !=0)
        {

 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_LogoutJava, hSession);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_OpenSession(CK_SLOT_ID slotID, CK_FLAGS flags, CK_VOID_PTR pApplication, CK_NOTIFY Notify, CK_SESSION_HANDLE_PTR phSession)
{ /*printf("\nC: called: C_OpenSession    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_OpenSessionJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_OpenSession", "(JJLproxys/CK_BYTE_ARRAY;Lproxys/CK_NOTIFY_CALLBACK;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_OpenSessionJava !=0)
        {
jobject obj2;
 if(pApplication != NULL) { jclass cls2 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");
                                obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pApplication, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }jobject obj3;
 if(Notify != NULL) { jclass cls3 = (*(dings->env))->FindClass(dings->env, "proxys/CK_NOTIFY_CALLBACK"); //
         jmethodID constructor3 = (*(dings->env))->GetMethodID(dings->env, cls3, "<init>", "(JZ)V");
                                obj3=(*(dings->env))->NewObject(dings->env, cls3, constructor3, Notify, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }jobject obj4;
 if(phSession != NULL) { jclass cls4 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor4 = (*(dings->env))->GetMethodID(dings->env, cls4, "<init>", "(JZ)V");
                                obj4=(*(dings->env))->NewObject(dings->env, cls4, constructor4, phSession, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_OpenSessionJava, slotID, flags, obj2, obj3, obj4);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_SeedRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSeed, CK_ULONG ulSeedLen)
{ /*printf("\nC: called: C_SeedRandom    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_SeedRandomJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_SeedRandom", "(JLjava/lang/String;J)J");
        
        if(C_SeedRandomJava !=0)
        {
			jobject string1;
			string1 =(*(dings->env))->NewStringUTF(dings->env, pSeed);
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_SeedRandomJava, hSession, string1, ulSeedLen);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_SetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{ /*printf("\nC: called: C_SetAttributeValue    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_SetAttributeValueJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_SetAttributeValue", "(JJ[Lobjects/ATTRIBUTE;J)J");
        
        if(C_SetAttributeValueJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls2 = (*(dings->env))->FindClass(dings->env, "objects/ATTRIBUTE"); //
jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
jobject obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pTemplate, JNI_FALSE);
 (*(dings->env))->ExceptionDescribe(dings->env);




array = (*(dings->env))->NewObjectArray(dings->env,size,cls2, obj2);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pTemplate+i, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);


if (pTemplate+i == NULL){
 obj2 = NULL; 

}
 (*(dings->env))->SetObjectArrayElement(dings->env, array,i, obj2);
 (*(dings->env))->ExceptionDescribe(dings->env);

}

}else{
 array = NULL;
}
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_SetAttributeValueJava, hSession, hObject, array, ulCount);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_SetPIN(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pOldPin, CK_ULONG ulOldLen, CK_CHAR_PTR pNewPin, CK_ULONG ulNewLen)
{ /*printf("\nC: called: C_SetPIN    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_SetPINJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_SetPIN", "(JLjava/lang/String;JLjava/lang/String;J)J");
        
        if(C_SetPINJava !=0)
        {
			jobject string1;
			string1 =(*(dings->env))->NewStringUTF(dings->env, pOldPin);			jobject string3;
			string3 =(*(dings->env))->NewStringUTF(dings->env, pNewPin);
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_SetPINJava, hSession, string1, ulOldLen, string3, ulNewLen);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_Sign(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{ /*printf("\nC: called: C_Sign    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_SignJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_Sign", "(J[BJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_SignJava !=0)
        {
jintArray result;
result = (*(dings->env))->NewIntArray(dings->env, ulDataLen);

 (*(dings->env))->ExceptionDescribe(dings->env);
int j;
jint fill[ulDataLen];
for (j = 0; j < ulDataLen; j++) {
fill[j] =pData[j]; }
(*(dings->env))->SetIntArrayRegion(dings->env, result, 0, ulDataLen, fill);
jobject obj3;
 if(pSignature != NULL) { jclass cls3 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor3 = (*(dings->env))->GetMethodID(dings->env, cls3, "<init>", "(JZ)V");
                                obj3=(*(dings->env))->NewObject(dings->env, cls3, constructor3, pSignature, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }jobject obj4;
 if(pulSignatureLen != NULL) { jclass cls4 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor4 = (*(dings->env))->GetMethodID(dings->env, cls4, "<init>", "(JZ)V");
                                obj4=(*(dings->env))->NewObject(dings->env, cls4, constructor4, pulSignatureLen, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_SignJava, hSession, result, ulDataLen, obj3, obj4);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_SignInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ /*printf("\nC: called: C_SignInit    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_SignInitJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_SignInit", "(JLobjects/MECHANISM;J)J");
        
        if(C_SignInitJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_SignInitJava, hSession, obj1, hKey);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_UnwrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hUnwrappingKey, CK_BYTE_PTR pWrappedKey, CK_ULONG ulWrappedKeyLen, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
{ /*printf("\nC: called: C_UnwrapKey    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_UnwrapKeyJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_UnwrapKey", "(JLobjects/MECHANISM;J[BJ[Lobjects/ATTRIBUTE;JLproxys/CK_ULONG_JPTR;)J");
        
        if(C_UnwrapKeyJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jintArray result;
result = (*(dings->env))->NewIntArray(dings->env, ulWrappedKeyLen);

 (*(dings->env))->ExceptionDescribe(dings->env);
int j;
jint fill[ulWrappedKeyLen];
for (j = 0; j < ulWrappedKeyLen; j++) {
fill[j] =pWrappedKey[j]; }
(*(dings->env))->SetIntArrayRegion(dings->env, result, 0, ulWrappedKeyLen, fill);
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulAttributeCount;
jclass cls5 = (*(dings->env))->FindClass(dings->env, "objects/ATTRIBUTE"); //
jmethodID constructor5 = (*(dings->env))->GetMethodID(dings->env, cls5, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
jobject obj5=(*(dings->env))->NewObject(dings->env, cls5, constructor5, pTemplate, JNI_FALSE);
 (*(dings->env))->ExceptionDescribe(dings->env);




array = (*(dings->env))->NewObjectArray(dings->env,size,cls5, obj5);









int i;
 for(i=0; i<ulAttributeCount;i++)
{
jmethodID constructor5 = (*(dings->env))->GetMethodID(dings->env, cls5, "<init>", "(JZ)V");

 (*(dings->env))->ExceptionDescribe(dings->env);
obj5=(*(dings->env))->NewObject(dings->env, cls5, constructor5, pTemplate+i, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);


if (pTemplate+i == NULL){
 obj5 = NULL; 

}
 (*(dings->env))->SetObjectArrayElement(dings->env, array,i, obj5);
 (*(dings->env))->ExceptionDescribe(dings->env);

}

}else{
 array = NULL;
}jobject obj7;
 if(phKey != NULL) { jclass cls7 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor7 = (*(dings->env))->GetMethodID(dings->env, cls7, "<init>", "(JZ)V");
                                obj7=(*(dings->env))->NewObject(dings->env, cls7, constructor7, phKey, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj7==NULL){
                                }else{
				   }
} else{ obj7=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_UnwrapKeyJava, hSession, obj1, hUnwrappingKey, result, ulWrappedKeyLen, array, ulAttributeCount, obj7);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_WrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hWrappingKey, CK_OBJECT_HANDLE hKey, CK_BYTE_PTR pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen)
{ /*printf("\nC: called: C_WrapKey    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_WrapKeyJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_WrapKey", "(JLobjects/MECHANISM;JJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_WrapKeyJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj4;
 if(pWrappedKey != NULL) { jclass cls4 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor4 = (*(dings->env))->GetMethodID(dings->env, cls4, "<init>", "(JZ)V");
                                obj4=(*(dings->env))->NewObject(dings->env, cls4, constructor4, pWrappedKey, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }jobject obj5;
 if(pulWrappedKeyLen != NULL) { jclass cls5 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor5 = (*(dings->env))->GetMethodID(dings->env, cls5, "<init>", "(JZ)V");
                                obj5=(*(dings->env))->NewObject(dings->env, cls5, constructor5, pulWrappedKeyLen, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj5==NULL){
                                }else{
				   }
} else{ obj5=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_WrapKeyJava, hSession, obj1, hWrappingKey, hKey, obj4, obj5);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_SignUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{ /*printf("\nC: called: C_SignUpdate    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_SignUpdateJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_SignUpdate", "(J[BJ)J");
        
        if(C_SignUpdateJava !=0)
        {
jintArray result;
result = (*(dings->env))->NewIntArray(dings->env, ulPartLen);

 (*(dings->env))->ExceptionDescribe(dings->env);
int j;
jint fill[ulPartLen];
for (j = 0; j < ulPartLen; j++) {
fill[j] =pPart[j]; }
(*(dings->env))->SetIntArrayRegion(dings->env, result, 0, ulPartLen, fill);

 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_SignUpdateJava, hSession, result, ulPartLen);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}


CK_RV C_SignFinal(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{ /*printf("\nC: called: C_SignFinal    ");*/ 
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->cls !=0)
{
        jmethodID C_SignFinalJava = (*(dings->env))->GetStaticMethodID(dings->env, dings->cls,"C_SignFinal", "(JLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_SignFinalJava !=0)
        {
jobject obj1;
 if(pSignature != NULL) { jclass cls1 = (*(dings->env))->FindClass(dings->env, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor1 = (*(dings->env))->GetMethodID(dings->env, cls1, "<init>", "(JZ)V");
                                obj1=(*(dings->env))->NewObject(dings->env, cls1, constructor1, pSignature, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj2;
 if(pulSignatureLen != NULL) { jclass cls2 = (*(dings->env))->FindClass(dings->env, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor2 = (*(dings->env))->GetMethodID(dings->env, cls2, "<init>", "(JZ)V");
                                obj2=(*(dings->env))->NewObject(dings->env, cls2, constructor2, pulSignatureLen, JNI_FALSE);

 (*(dings->env))->ExceptionDescribe(dings->env);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(dings->env))->CallStaticLongMethod(dings->env, dings->cls, C_SignFinalJava, hSession, obj1, obj2);
 (*(dings->env))->ExceptionDescribe(dings->env);
 
}}

return retVal;
}




#define CK_DEFINE_FUNCTION(returnType, name) returnType name
CK_DEFINE_FUNCTION(CK_RV, C_InitPIN)(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_GetOperationState)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pOperationState, CK_ULONG_PTR pulOperationStateLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pLastEncryptedPart, CK_ULONG_PTR pulLastEncryptedPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DecryptFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pLastPart, CK_ULONG_PTR pulLastPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DigestInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_Digest)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pDigest, CK_ULONG_PTR pulDigestLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DigestUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DigestKey)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DigestFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pDigest, CK_ULONG_PTR pulDigestLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_SignRecoverInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_SignRecover)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_Verify)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyRecoverInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyRecover)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen, CK_BYTE_PTR pData, CK_ULONG_PTR pulDataLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DigestEncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DecryptDigestUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_SignEncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DecryptVerifyUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_GenerateKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_GenerateKeyPair)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_ATTRIBUTE_PTR pPublicKeyTemplate, CK_ULONG ulPublicKeyAttributeCount, CK_ATTRIBUTE_PTR pPrivateKeyTemplate, CK_ULONG ulPrivateKeyAttributeCount, CK_OBJECT_HANDLE_PTR phPublicKey, CK_OBJECT_HANDLE_PTR phPrivateKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_DeriveKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hBaseKey, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_SetOperationState)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pOperationState, CK_ULONG ulOperationStateLen, CK_OBJECT_HANDLE hEncryptionKey, CK_OBJECT_HANDLE hAuthenticationKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_CopyObject)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phNewObject)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_GetObjectSize)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ULONG_PTR pulSize)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_GetFunctionStatus)(CK_SESSION_HANDLE hSession)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_CancelFunction)(CK_SESSION_HANDLE hSession)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_InitToken)(CK_SLOT_ID slotID, CK_CHAR_PTR pPin, CK_ULONG ulPinLen, CK_CHAR_PTR pLabel)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_Encrypt)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pEncryptedData, CK_ULONG_PTR pulEncryptedDataLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_Decrypt)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedData, CK_ULONG ulEncryptedDataLen, CK_BYTE_PTR pData, CK_ULONG_PTR pulDataLen)
{ printf("not implemented shit called"); return CKR_OK;}
CK_DEFINE_FUNCTION(CK_RV, C_WaitForSlotEvent)(CK_FLAGS flags, CK_SLOT_ID_PTR pSlot, CK_VOID_PTR pReserved){ printf("not implemented shit called"); return 0x00000008;}
