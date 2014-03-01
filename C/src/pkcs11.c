#include<pthread.h>
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
{ /*printf("\nC: called: C_CloseAllSessions    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_CloseAllSessionsJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CloseAllSessions", "(J)J");
        
        if(C_CloseAllSessionsJava !=0)
        {

 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CloseAllSessionsJava, slotID);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_CloseSession(CK_SESSION_HANDLE hSession)
{ /*printf("\nC: called: C_CloseSession    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_CloseSessionJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CloseSession", "(J)J");
        
        if(C_CloseSessionJava !=0)
        {

 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CloseSessionJava, hSession);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_CreateObject(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phObject)
{ /*printf("\nC: called: C_CreateObject    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_CreateObjectJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CreateObject", "(J[Lobjects/ATTRIBUTE;JLproxys/CK_ULONG_JPTR;)J");
        
        if(C_CreateObjectJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls1 = (*(environment))->FindClass(environment, "objects/ATTRIBUTE"); //
jobject obj1;



array = (*(environment))->NewObjectArray(environment,size,cls1, NULL);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");

 (*(environment))->ExceptionDescribe(environment);
obj1=(*(environment))->NewObject(environment, cls1, constructor1, pTemplate+i, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);


if (pTemplate+i == NULL){
 obj1 = NULL; 

}
 (*(environment))->SetObjectArrayElement(environment, array,i, obj1);
 (*(environment))->ExceptionDescribe(environment);

}

}else{
 array = NULL;
}jobject obj3;
 if(phObject != NULL) { jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                                obj3=(*(environment))->NewObject(environment, cls3, constructor3, phObject, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CreateObjectJava, hSession, array, ulCount, obj3);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_DecryptInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ /*printf("\nC: called: C_DecryptInit    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_DecryptInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DecryptInit", "(JLobjects/MECHANISM;J)J");
        
        if(C_DecryptInitJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptInitJava, hSession, obj1, hKey);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_DecryptUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{ /*printf("\nC: called: C_DecryptUpdate    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_DecryptUpdateJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DecryptUpdate", "(J[BJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_DecryptUpdateJava !=0)
        {
jbyteArray result;
result = (*(environment))->NewByteArray(environment, ulEncryptedPartLen);

 (*(environment))->ExceptionDescribe(environment);
(*(environment))->SetByteArrayRegion(environment, result, 0, ulEncryptedPartLen,(jbyte*)pEncryptedPart);
jobject obj3;
 if(pPart != NULL) { jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                                obj3=(*(environment))->NewObject(environment, cls3, constructor3, pPart, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }jobject obj4;
 if(pulPartLen != NULL) { jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                                obj4=(*(environment))->NewObject(environment, cls4, constructor4, pulPartLen, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptUpdateJava, hSession, result, ulEncryptedPartLen, obj3, obj4);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_DestroyObject(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject)
{ /*printf("\nC: called: C_DestroyObject    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_DestroyObjectJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DestroyObject", "(JJ)J");
        
        if(C_DestroyObjectJava !=0)
        {

 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DestroyObjectJava, hSession, hObject);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_Finalize(CK_VOID_PTR pReserved)
{ /*printf("\nC: called: C_Finalize    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_FinalizeJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Finalize", "(Lproxys/CK_BYTE_ARRAY;)J");
        
        if(C_FinalizeJava !=0)
        {
jobject obj0;
 if(pReserved != NULL) { jclass cls0 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor0 = (*(environment))->GetMethodID(environment, cls0, "<init>", "(JZ)V");
                                obj0=(*(environment))->NewObject(environment, cls0, constructor0, pReserved, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj0==NULL){
                                }else{
				   }
} else{ obj0=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FinalizeJava, obj0);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}
destroyVM();
return retVal;
}


CK_RV C_FindObjects(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE_PTR phObject, CK_ULONG ulMaxObjectCount, CK_ULONG_PTR pulObjectCount)
{ /*printf("\nC: called: C_FindObjects    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_FindObjectsJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjects", "(JLproxys/CK_ULONG_ARRAY;JLproxys/CK_ULONG_JPTR;)J");
        
        if(C_FindObjectsJava !=0)
        {
jobject obj1;
 if(phObject != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_ARRAY"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, phObject, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj3;
 if(pulObjectCount != NULL) { jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                                obj3=(*(environment))->NewObject(environment, cls3, constructor3, pulObjectCount, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsJava, hSession, obj1, ulMaxObjectCount, obj3);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_FindObjectsFinal(CK_SESSION_HANDLE hSession)
{ /*printf("\nC: called: C_FindObjectsFinal    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_FindObjectsFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjectsFinal", "(J)J");
        
        if(C_FindObjectsFinalJava !=0)
        {

 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsFinalJava, hSession);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_FindObjectsInit(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{ /*printf("\nC: called: C_FindObjectsInit    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_FindObjectsInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjectsInit", "(J[Lobjects/ATTRIBUTE;J)J");
        
        if(C_FindObjectsInitJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls1 = (*(environment))->FindClass(environment, "objects/ATTRIBUTE"); //
jobject obj1;



array = (*(environment))->NewObjectArray(environment,size,cls1, NULL);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");

 (*(environment))->ExceptionDescribe(environment);
obj1=(*(environment))->NewObject(environment, cls1, constructor1, pTemplate+i, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);


if (pTemplate+i == NULL){
 obj1 = NULL; 

}
 (*(environment))->SetObjectArrayElement(environment, array,i, obj1);
 (*(environment))->ExceptionDescribe(environment);

}

}else{
 array = NULL;
}
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsInitJava, hSession, array, ulCount);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GenerateRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR RandomData, CK_ULONG ulRandomLen)
{ /*printf("\nC: called: C_GenerateRandom    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GenerateRandomJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GenerateRandom", "(JLproxys/CK_BYTE_ARRAY;J)J");
        
        if(C_GenerateRandomJava !=0)
        {
jobject obj1;
 if(RandomData != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, RandomData, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GenerateRandomJava, hSession, obj1, ulRandomLen);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{ /*printf("\nC: called: C_GetAttributeValue    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetAttributeValueJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetAttributeValue", "(JJ[Lobjects/ATTRIBUTE;J)J");
        
        if(C_GetAttributeValueJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls2 = (*(environment))->FindClass(environment, "objects/ATTRIBUTE"); //
jobject obj2;



array = (*(environment))->NewObjectArray(environment,size,cls2, NULL);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");

 (*(environment))->ExceptionDescribe(environment);
obj2=(*(environment))->NewObject(environment, cls2, constructor2, pTemplate+i, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);


if (pTemplate+i == NULL){
 obj2 = NULL; 

}
 (*(environment))->SetObjectArrayElement(environment, array,i, obj2);
 (*(environment))->ExceptionDescribe(environment);

}

}else{
 array = NULL;
}
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetAttributeValueJava, hSession, hObject, array, ulCount);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetInfo(CK_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetInfo    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetInfo", "(Lproxys/CK_INFO;)J");
        
        if(C_GetInfoJava !=0)
        {
jobject obj0;
 if(pInfo != NULL) { jclass cls0 = (*(environment))->FindClass(environment, "proxys/CK_INFO"); //
         jmethodID constructor0 = (*(environment))->GetMethodID(environment, cls0, "<init>", "(JZ)V");
                                obj0=(*(environment))->NewObject(environment, cls0, constructor0, pInfo, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj0==NULL){
                                }else{
				   }
} else{ obj0=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetInfoJava, obj0);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetMechanismInfo(CK_SLOT_ID slotID, CK_MECHANISM_TYPE type, CK_MECHANISM_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetMechanismInfo    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetMechanismInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetMechanismInfo", "(JJLproxys/CK_MECHANISM_INFO;)J");
        
        if(C_GetMechanismInfoJava !=0)
        {
jobject obj2;
 if(pInfo != NULL) { jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_MECHANISM_INFO"); //
         jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                                obj2=(*(environment))->NewObject(environment, cls2, constructor2, pInfo, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetMechanismInfoJava, slotID, type, obj2);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetMechanismList(CK_SLOT_ID slotID, CK_MECHANISM_TYPE_PTR pMechanismList, CK_ULONG_PTR pulCount)
{ /*printf("\nC: called: C_GetMechanismList    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetMechanismListJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetMechanismList", "(JLproxys/CK_ULONG_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_GetMechanismListJava !=0)
        {
jobject obj1;
 if(pMechanismList != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_ARRAY"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pMechanismList, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj2;
 if(pulCount != NULL) { jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                                obj2=(*(environment))->NewObject(environment, cls2, constructor2, pulCount, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetMechanismListJava, slotID, obj1, obj2);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetSessionInfo(CK_SESSION_HANDLE hSession, CK_SESSION_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetSessionInfo    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetSessionInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSessionInfo", "(JLproxys/CK_SESSION_INFO;)J");
        
        if(C_GetSessionInfoJava !=0)
        {
jobject obj1;
 if(pInfo != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_SESSION_INFO"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pInfo, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSessionInfoJava, hSession, obj1);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetSlotInfo(CK_SLOT_ID slotID, CK_SLOT_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetSlotInfo    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetSlotInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSlotInfo", "(JLproxys/CK_SLOT_INFO;)J");
        
        if(C_GetSlotInfoJava !=0)
        {
jobject obj1;
 if(pInfo != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_SLOT_INFO"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pInfo, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSlotInfoJava, slotID, obj1);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetSlotList(CK_BBOOL tokenPresent, CK_SLOT_ID_PTR pSlotList, CK_ULONG_PTR pulCount)
{ /*printf("\nC: called: C_GetSlotList    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetSlotListJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSlotList", "(SLproxys/CK_ULONG_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_GetSlotListJava !=0)
        {
jobject obj1;
 if(pSlotList != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_ARRAY"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pSlotList, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj2;
 if(pulCount != NULL) { jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                                obj2=(*(environment))->NewObject(environment, cls2, constructor2, pulCount, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSlotListJava, tokenPresent, obj1, obj2);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_GetTokenInfo(CK_SLOT_ID slotID, CK_TOKEN_INFO_PTR pInfo)
{ /*printf("\nC: called: C_GetTokenInfo    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_GetTokenInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetTokenInfo", "(JLproxys/CK_TOKEN_INFO;)J");
        
        if(C_GetTokenInfoJava !=0)
        {
jobject obj1;
 if(pInfo != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_TOKEN_INFO"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pInfo, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetTokenInfoJava, slotID, obj1);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_Initialize(CK_VOID_PTR pInitArgs)
{ /*printf("\nC: called: C_Initialize    "); */
CK_C_INITIALIZE_ARGS* args = pInitArgs; 
sing* blargl = get_instance(); 
if(pInitArgs!=NULL){
blargl->CreateMutex = args->CreateMutex; 
blargl->DestroyMutex = args->DestroyMutex; 
blargl->LockMutex = args->LockMutex; 
blargl->UnlockMutex = args->UnlockMutex;
blargl->CreateMutex(&(blargl->ppMutex)); 
} else { 
blargl->CreateMutex = NULL; 
blargl->DestroyMutex = NULL; 
blargl->LockMutex = NULL; 
blargl->UnlockMutex = NULL;
}



long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_InitializeJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Initialize", "(Lproxys/CK_BYTE_ARRAY;)J");
        
        if(C_InitializeJava !=0)
        {
jobject obj0;
 if(pInitArgs != NULL) { jclass cls0 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor0 = (*(environment))->GetMethodID(environment, cls0, "<init>", "(JZ)V");
                                obj0=(*(environment))->NewObject(environment, cls0, constructor0, pInitArgs, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj0==NULL){
                                }else{
				   }
} else{ obj0=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_InitializeJava, obj0);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_Login(CK_SESSION_HANDLE hSession, CK_USER_TYPE userType, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
{ /*printf("\nC: called: C_Login    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_LoginJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Login", "(JJLjava/lang/String;J)J");
        
        if(C_LoginJava !=0)
        {
			jobject string2;
			string2 =(*(environment))->NewStringUTF(environment, pPin);
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_LoginJava, hSession, userType, string2, ulPinLen);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_Logout(CK_SESSION_HANDLE hSession)
{ /*printf("\nC: called: C_Logout    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_LogoutJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Logout", "(J)J");
        
        if(C_LogoutJava !=0)
        {

 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_LogoutJava, hSession);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_OpenSession(CK_SLOT_ID slotID, CK_FLAGS flags, CK_VOID_PTR pApplication, CK_NOTIFY Notify, CK_SESSION_HANDLE_PTR phSession)
{ /*printf("\nC: called: C_OpenSession    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_OpenSessionJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_OpenSession", "(JJLproxys/CK_BYTE_ARRAY;Lproxys/CK_NOTIFY_CALLBACK;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_OpenSessionJava !=0)
        {
jobject obj2;
 if(pApplication != NULL) { jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                                obj2=(*(environment))->NewObject(environment, cls2, constructor2, pApplication, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }jobject obj3;
 if(Notify != NULL) { jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_NOTIFY_CALLBACK"); //
         jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                                obj3=(*(environment))->NewObject(environment, cls3, constructor3, Notify, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }jobject obj4;
 if(phSession != NULL) { jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                                obj4=(*(environment))->NewObject(environment, cls4, constructor4, phSession, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_OpenSessionJava, slotID, flags, obj2, obj3, obj4);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_SeedRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSeed, CK_ULONG ulSeedLen)
{ /*printf("\nC: called: C_SeedRandom    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_SeedRandomJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SeedRandom", "(JLjava/lang/String;J)J");
        
        if(C_SeedRandomJava !=0)
        {
			jobject string1;
			string1 =(*(environment))->NewStringUTF(environment, pSeed);
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SeedRandomJava, hSession, string1, ulSeedLen);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_SetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{ /*printf("\nC: called: C_SetAttributeValue    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_SetAttributeValueJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SetAttributeValue", "(JJ[Lobjects/ATTRIBUTE;J)J");
        
        if(C_SetAttributeValueJava !=0)
        {
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulCount;
jclass cls2 = (*(environment))->FindClass(environment, "objects/ATTRIBUTE"); //
jobject obj2;



array = (*(environment))->NewObjectArray(environment,size,cls2, NULL);









int i;
 for(i=0; i<ulCount;i++)
{
jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");

 (*(environment))->ExceptionDescribe(environment);
obj2=(*(environment))->NewObject(environment, cls2, constructor2, pTemplate+i, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);


if (pTemplate+i == NULL){
 obj2 = NULL; 

}
 (*(environment))->SetObjectArrayElement(environment, array,i, obj2);
 (*(environment))->ExceptionDescribe(environment);

}

}else{
 array = NULL;
}
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SetAttributeValueJava, hSession, hObject, array, ulCount);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_SetPIN(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pOldPin, CK_ULONG ulOldLen, CK_CHAR_PTR pNewPin, CK_ULONG ulNewLen)
{ /*printf("\nC: called: C_SetPIN    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_SetPINJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SetPIN", "(JLjava/lang/String;JLjava/lang/String;J)J");
        
        if(C_SetPINJava !=0)
        {
			jobject string1;
			string1 =(*(environment))->NewStringUTF(environment, pOldPin);			jobject string3;
			string3 =(*(environment))->NewStringUTF(environment, pNewPin);
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SetPINJava, hSession, string1, ulOldLen, string3, ulNewLen);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_Sign(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{ /*printf("\nC: called: C_Sign    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_SignJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Sign", "(J[BJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_SignJava !=0)
        {
jbyteArray result;
result = (*(environment))->NewByteArray(environment, ulDataLen);

 (*(environment))->ExceptionDescribe(environment);
(*(environment))->SetByteArrayRegion(environment, result, 0, ulDataLen,(jbyte*)pData);
jobject obj3;
 if(pSignature != NULL) { jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                                obj3=(*(environment))->NewObject(environment, cls3, constructor3, pSignature, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj3==NULL){
                                }else{
				   }
} else{ obj3=NULL; }jobject obj4;
 if(pulSignatureLen != NULL) { jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                                obj4=(*(environment))->NewObject(environment, cls4, constructor4, pulSignatureLen, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignJava, hSession, result, ulDataLen, obj3, obj4);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_SignInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{ /*printf("\nC: called: C_SignInit    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_SignInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SignInit", "(JLobjects/MECHANISM;J)J");
        
        if(C_SignInitJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignInitJava, hSession, obj1, hKey);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_UnwrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hUnwrappingKey, CK_BYTE_PTR pWrappedKey, CK_ULONG ulWrappedKeyLen, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
{ /*printf("\nC: called: C_UnwrapKey    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_UnwrapKeyJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_UnwrapKey", "(JLobjects/MECHANISM;J[BJ[Lobjects/ATTRIBUTE;JLproxys/CK_ULONG_JPTR;)J");
        
        if(C_UnwrapKeyJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jbyteArray result;
result = (*(environment))->NewByteArray(environment, ulWrappedKeyLen);

 (*(environment))->ExceptionDescribe(environment);
(*(environment))->SetByteArrayRegion(environment, result, 0, ulWrappedKeyLen,(jbyte*)pWrappedKey);
jobjectArray array;
 if(pTemplate != NULL) { 
jsize size = ulAttributeCount;
jclass cls5 = (*(environment))->FindClass(environment, "objects/ATTRIBUTE"); //
jobject obj5;



array = (*(environment))->NewObjectArray(environment,size,cls5, NULL);









int i;
 for(i=0; i<ulAttributeCount;i++)
{
jmethodID constructor5 = (*(environment))->GetMethodID(environment, cls5, "<init>", "(JZ)V");

 (*(environment))->ExceptionDescribe(environment);
obj5=(*(environment))->NewObject(environment, cls5, constructor5, pTemplate+i, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);


if (pTemplate+i == NULL){
 obj5 = NULL; 

}
 (*(environment))->SetObjectArrayElement(environment, array,i, obj5);
 (*(environment))->ExceptionDescribe(environment);

}

}else{
 array = NULL;
}jobject obj7;
 if(phKey != NULL) { jclass cls7 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor7 = (*(environment))->GetMethodID(environment, cls7, "<init>", "(JZ)V");
                                obj7=(*(environment))->NewObject(environment, cls7, constructor7, phKey, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj7==NULL){
                                }else{
				   }
} else{ obj7=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_UnwrapKeyJava, hSession, obj1, hUnwrappingKey, result, ulWrappedKeyLen, array, ulAttributeCount, obj7);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_WrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hWrappingKey, CK_OBJECT_HANDLE hKey, CK_BYTE_PTR pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen)
{ /*printf("\nC: called: C_WrapKey    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_WrapKeyJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_WrapKey", "(JLobjects/MECHANISM;JJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_WrapKeyJava !=0)
        {
jobject obj1;
 if(pMechanism != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "objects/MECHANISM"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pMechanism, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj4;
 if(pWrappedKey != NULL) { jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                                obj4=(*(environment))->NewObject(environment, cls4, constructor4, pWrappedKey, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj4==NULL){
                                }else{
				   }
} else{ obj4=NULL; }jobject obj5;
 if(pulWrappedKeyLen != NULL) { jclass cls5 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor5 = (*(environment))->GetMethodID(environment, cls5, "<init>", "(JZ)V");
                                obj5=(*(environment))->NewObject(environment, cls5, constructor5, pulWrappedKeyLen, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj5==NULL){
                                }else{
				   }
} else{ obj5=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_WrapKeyJava, hSession, obj1, hWrappingKey, hKey, obj4, obj5);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_SignUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{ /*printf("\nC: called: C_SignUpdate    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_SignUpdateJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SignUpdate", "(J[BJ)J");
        
        if(C_SignUpdateJava !=0)
        {
jbyteArray result;
result = (*(environment))->NewByteArray(environment, ulPartLen);

 (*(environment))->ExceptionDescribe(environment);
(*(environment))->SetByteArrayRegion(environment, result, 0, ulPartLen,(jbyte*)pPart);

 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignUpdateJava, hSession, result, ulPartLen);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

return retVal;
}


CK_RV C_SignFinal(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{ /*printf("\nC: called: C_SignFinal    "); */
long retVal=CKR_GENERAL_ERROR;
sing* dings = get_instance();
if(dings->LockMutex != NULL){
dings->LockMutex((dings->ppMutex));
}
JNIEnv* environment;
(*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
if(dings->cls !=0)
{
        jmethodID C_SignFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SignFinal", "(JLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");
        
        if(C_SignFinalJava !=0)
        {
jobject obj1;
 if(pSignature != NULL) { jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
         jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pSignature, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj1==NULL){
                                }else{
				   }
} else{ obj1=NULL; }jobject obj2;
 if(pulSignatureLen != NULL) { jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
         jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                                obj2=(*(environment))->NewObject(environment, cls2, constructor2, pulSignatureLen, JNI_FALSE);

 (*(environment))->ExceptionDescribe(environment);
								   if(obj2==NULL){
                                }else{
				   }
} else{ obj2=NULL; }
 retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignFinalJava, hSession, obj1, obj2);
 (*(environment))->ExceptionDescribe(environment);
 
}}
if(dings->UnlockMutex != NULL){
dings->UnlockMutex(dings->ppMutex);
}

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
