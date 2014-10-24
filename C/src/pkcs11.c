#include"pkcs11.h"
#include"jvm.h"
#include"Pkcs11Config.h"




jobject createAttributeArray(JNIEnv* environment, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount);
jobject createAttributeValue(JNIEnv* environment, CK_ATTRIBUTE_PTR attribute);
void copyDataToPTR(JNIEnv* environment, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, jarray array, CK_ULONG retVal);
int getAttributeType(CK_ULONG type);

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
    &C_WaitForSlotEvent
};

CK_RV C_GetFunctionList(CK_FUNCTION_LIST_PTR_PTR ppFunctionList) {
    *ppFunctionList=&pkcs11_functions;
    return CKR_OK;
}

CK_RV C_Initialize(CK_VOID_PTR pInitArgs)
{   //fprintf(stdout,"\nC: called: C_Initialize    ");
    //fprintf(stdout,"\n\n\n\n************** lala\n\n\n\n\n");
    CK_C_INITIALIZE_ARGS* args = pInitArgs;
    sing* blargl = get_instance();
    if(pInitArgs!=NULL) {
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
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_InitializeJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Initialize", "()J");

        if(C_InitializeJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_InitializeJava);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}

CK_RV C_Finalize(CK_VOID_PTR pReserved)
{   //fprintf(stdout,"\nC: called: C_Finalize    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_FinalizeJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Finalize", "()J");

        if(C_FinalizeJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FinalizeJava);
            (*(environment))->ExceptionDescribe(environment);
            if(pReserved != NULL) {
                retVal = CKR_ARGUMENTS_BAD;
            }

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }
//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
	pthread_cond_signal(&(dings->finish));	
	pthread_join(instance->thread,NULL);
    return retVal;
}


CK_RV C_GetSlotList(CK_BBOOL tokenPresent, CK_SLOT_ID_PTR pSlotList, CK_ULONG_PTR pulCount)
{

    //fprintf(stdout,"\nC: called: C_GetSlotList    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
//build arguments

//build CK_ULONG_PTR
        jobject _pulCount = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        (*(environment))->ExceptionDescribe(environment);
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        (*(environment))->ExceptionDescribe(environment);
        _pulCount  =(*(environment))->NewObject(environment, longClass, constructorLong, *pulCount);
        (*(environment))->ExceptionDescribe(environment);
        //fprintf(stdout,"\n pulCount: %d\n", *pulCount);

//build long[]
        jlongArray _pSlotList = NULL;

        if(pSlotList != NULL) {
            _pSlotList= (*(environment))->NewLongArray(environment, *pulCount);
            (*(environment))->ExceptionDescribe(environment);
        }

        //build boolean
        jboolean _tokenPresent = tokenPresent;

        jmethodID C_GetSlotListJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSlotList", "(Z[JLobj/CK_ULONG_PTR;)J");
        (*(environment))->ExceptionDescribe(environment);

        if(C_GetSlotListJava==NULL) {
            //fprintf(stdout,"no id found!\n");
        }
        if(C_GetSlotListJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSlotListJava, _tokenPresent, _pSlotList, _pulCount);
            (*(environment))->ExceptionDescribe(environment);
        }
        jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *pulCount = (*(environment))->CallLongMethod(environment, _pulCount, getValue);
        //fprintf(stdout,"\n pulCount: %d\n", *pulCount);

        if(_pSlotList!=NULL && retVal!=CKR_BUFFER_TOO_SMALL) {
            long* data= (*(environment))->GetLongArrayElements(environment,_pSlotList, NULL);
            int j;
            for(j=0; j<*pulCount; j++) {
                pSlotList[j]=data[j];
            }
        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}

CK_RV C_GetSlotInfo(CK_SLOT_ID slotID, CK_SLOT_INFO_PTR pInfo)
{   
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        //arguments:
        //1.) slotid
        //2.) CK_SLOT_INFO

        //Version
        jclass versionClass = (*(environment))->FindClass(environment, "Lobj/CK_VERSION;");
        jmethodID versionConstructor = (*(environment))->GetMethodID(environment, versionClass, "<init>", "(BB)V");
        jobject version1=(*(environment))->NewObject(environment, versionClass, versionConstructor, 0, 0);
        jobject version2=(*(environment))->NewObject(environment, versionClass, versionConstructor, 0, 0);


        //CK_SLOT_INFO
        jclass slotinfoClass = (*(environment))->FindClass(environment, "Lobj/CK_SLOT_INFO;");
        jmethodID slotinfoConstructor = (*(environment))->GetMethodID(environment,slotinfoClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;JLobj/CK_VERSION;Lobj/CK_VERSION;)V");
        jobject slotinfo = (*(environment))->NewObject(environment, slotinfoClass, slotinfoConstructor, NULL, NULL, 0, version1, version2);

        jmethodID C_GetSlotInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSlotInfo", "(JLobj/CK_SLOT_INFO;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSlotInfoJava, slotID, slotinfo);
        (*(environment))->ExceptionDescribe(environment);

        //pinfo :)
        jmethodID getSlotDescription = (*(environment))->GetMethodID(environment, slotinfoClass,"getSlotDescription", "()Ljava/lang/String;");
        jstring slotDescription = (*(environment))->CallObjectMethod(environment, slotinfo, getSlotDescription);
        jsize lenDescription = (*(environment))->GetStringLength(environment, slotDescription);
        const jchar* charDescription = (*(environment))->GetStringChars(environment, slotDescription, NULL);
        int x =0;
        for(x=0; x<lenDescription; x++) {
            pInfo->slotDescription[x]=charDescription[x];
        }


        jmethodID getmanufacturerID = (*(environment))->GetMethodID(environment, slotinfoClass,"getManufacturerID", "()Ljava/lang/String;");
        jstring manufacturerID = (*(environment))->CallObjectMethod(environment, slotinfo, getmanufacturerID);

        jsize lenManufacturerID = (*(environment))->GetStringLength(environment, manufacturerID);
        const jchar* charManufacturer = (*(environment))->GetStringChars(environment, manufacturerID, NULL);
        for(x=0; x<lenManufacturerID; x++) {
            pInfo->manufacturerID[x]=charManufacturer[x];
        }

        jmethodID getFlags = (*(environment))->GetMethodID(environment, slotinfoClass,"getFlags", "()J");
        jlong flags = (*(environment))->CallLongMethod(environment, slotinfo, getFlags);
        pInfo->flags= flags;

        jmethodID getHardwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jbyte hardwareVersionMajor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMajor);
        pInfo->hardwareVersion.major=hardwareVersionMajor;
        jmethodID getHardwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jbyte hardwareVersionMinor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMinor);
        pInfo->hardwareVersion.minor=hardwareVersionMinor;

        jmethodID getFirmwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jbyte firmwareVersionMajor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMajor);
        pInfo->firmwareVersion.major=firmwareVersionMajor;
        jmethodID getFirmwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jbyte firmwareVersionMinor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMinor);
        pInfo->firmwareVersion.minor=firmwareVersionMinor;

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}

CK_RV C_GetTokenInfo(CK_SLOT_ID slotID, CK_TOKEN_INFO_PTR pInfo)
{   //fprintf(stdout,"\nC: called: C_GetTokenInfo    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {



        //Version
        jclass versionClass = (*(environment))->FindClass(environment, "Lobj/CK_VERSION;");
        jmethodID versionConstructor = (*(environment))->GetMethodID(environment, versionClass, "<init>", "(BB)V");
        jobject version1=(*(environment))->NewObject(environment, versionClass, versionConstructor, 0, 0);
        jobject version2=(*(environment))->NewObject(environment, versionClass, versionConstructor, 0, 0);

        //CK_TOKEN_INFO
        jclass tokeninfoClass = (*(environment))->FindClass(environment, "Lobj/CK_TOKEN_INFO;");
        jmethodID tokeninfoConstructor = (*(environment))->GetMethodID(environment,tokeninfoClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJJJJJJJJJJLobj/CK_VERSION;Lobj/CK_VERSION;Ljava/lang/String;)V");
        jobject tokeninfo = (*(environment))->NewObject(environment, tokeninfoClass, tokeninfoConstructor, NULL, NULL, NULL, NULL, 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, version1, version2, NULL);

        jmethodID C_GetTokenInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetTokenInfo", "(JLobj/CK_TOKEN_INFO;)J");
        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetTokenInfoJava, slotID,tokeninfo );
        (*(environment))->ExceptionDescribe(environment);



        jmethodID getTokenLabel = (*(environment))->GetMethodID(environment, tokeninfoClass,"getLabel", "()Ljava/lang/String;");
        jstring label = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenLabel);
        jsize lenLabel = (*(environment))->GetStringLength(environment, label);
        const jchar* charLabel = (*(environment))->GetStringChars(environment, label, NULL);
        int i=0;
        for(i=0; i<lenLabel; i++) {
            pInfo->label[i]=charLabel[i];
        }

        jmethodID getTokenManufacturerID = (*(environment))->GetMethodID(environment, tokeninfoClass,"getManufacturerID", "()Ljava/lang/String;");
        jstring ManufacturerID = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenManufacturerID);
        jsize lenManufacturerID = (*(environment))->GetStringLength(environment, ManufacturerID);
        const jchar* charManufacturerID = (*(environment))->GetStringChars(environment, ManufacturerID, NULL);
        for(i=0; i<lenManufacturerID; i++) {
            pInfo->manufacturerID[i]=charManufacturerID[i];
        }

        jmethodID getTokenModel = (*(environment))->GetMethodID(environment, tokeninfoClass,"getModel", "()Ljava/lang/String;");
        jstring Model = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenModel);
        jsize lenModel = (*(environment))->GetStringLength(environment, Model);
        const jchar* charModel = (*(environment))->GetStringChars(environment, Model, NULL);
        for(i=0; i<lenModel; i++) {
            pInfo->model[i]=charModel[i];
        }

        jmethodID getTokenSerialNumber = (*(environment))->GetMethodID(environment, tokeninfoClass,"getSerialNumber", "()Ljava/lang/String;");
        jstring SerialNumber = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenSerialNumber);
        jsize lenSerialNumber = (*(environment))->GetStringLength(environment, SerialNumber);
        const jchar* charSerialNumber = (*(environment))->GetStringChars(environment, SerialNumber, NULL);
        for(i=0; i<lenSerialNumber; i++) {
            pInfo->serialNumber[i]=charSerialNumber[i];
        }

        jmethodID get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getFlags", "()J");
        pInfo->flags = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMaxSessionCount", "()J");
        pInfo->ulMaxSessionCount = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlSessionCount", "()J");
        pInfo->ulSessionCount = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMaxRwSessionCount", "()J");
        pInfo-> ulMaxRwSessionCount = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlRwSessionCount", "()J");
        pInfo-> ulRwSessionCount = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMaxPinLen", "()J");
        pInfo-> ulMaxPinLen = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMinPinLen", "()J");
        pInfo-> ulMinPinLen = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlTotalPublicMemory", "()J");
        pInfo-> ulTotalPublicMemory = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlFreePublicMemory", "()J");
        pInfo-> ulFreePublicMemory = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlTotalPrivateMemory", "()J");
        pInfo-> ulTotalPrivateMemory = (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlFreePrivateMemory", "()J");
        pInfo-> ulFreePrivateMemory = (*(environment))->CallLongMethod(environment, tokeninfo, get);



        jmethodID getHardwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jbyte hardwareVersionMajor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMajor);
        pInfo->hardwareVersion.major=hardwareVersionMajor;
        jmethodID getHardwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jbyte hardwareVersionMinor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMinor);
        pInfo->hardwareVersion.minor=hardwareVersionMinor;

        jmethodID getFirmwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jbyte firmwareVersionMajor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMajor);
        pInfo->firmwareVersion.major=firmwareVersionMajor;
        jmethodID getFirmwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jbyte firmwareVersionMinor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMinor);
        pInfo->firmwareVersion.minor=firmwareVersionMinor;


        jmethodID getTime = (*(environment))->GetMethodID(environment, tokeninfoClass, "getUtcTime", "()Ljava/lang/String;");
        jstring utcTime = (*(environment))->CallObjectMethod(environment, tokeninfo, getTime);
        if(utcTime!=NULL) {
            (*(environment))->GetStringUTFRegion(environment, utcTime, 0, 16, ((char*)pInfo->utcTime));
        } else {
            int i=0;
            for(i=0; i<16; i++) {
                pInfo->utcTime[i]='\0';
            }
        }

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}

CK_RV C_GetMechanismList(CK_SLOT_ID slotID, CK_MECHANISM_TYPE_PTR pMechanismList, CK_ULONG_PTR pulCount)
{   //fprintf(stdout,"\nC: called: C_GetMechanismList    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {



        jobject _pulCount = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _pulCount  =(*(environment))->NewObject(environment, longClass, constructorLong, *pulCount);



        jlongArray _pMechanismList = NULL;

        if(pMechanismList != NULL) {
            _pMechanismList= (*(environment))->NewLongArray(environment, *pulCount);
        }

        CK_SLOT_ID _slotID = slotID;

        jmethodID C_GetMechanismListJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetMechanismList", "(J[JLobj/CK_ULONG_PTR;)J");
        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetMechanismListJava, _slotID, _pMechanismList, _pulCount);


        jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *pulCount = (*(environment))->CallLongMethod(environment, _pulCount, getValue);
        //fprintf(stdout,"\n pulCount: %d\n", *pulCount);

        if(_pMechanismList!=NULL && retVal==CKR_OK) {
            //fprintf(stdout,"C: C_GetMechanismList: writing back....\n");
            long* data= (*(environment))->GetLongArrayElements(environment,_pMechanismList, NULL);
            (*(environment))->ExceptionDescribe(environment);
            int j;
            for(j=0; j<*pulCount; j++) {
                pMechanismList[j]=data[j];
            }
            //fprintf(stdout,"writing back finished\n");
        }

        //fprintf(stdout,"\n pulCount returned: : %d\n", *pulCount);

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_GetMechanismInfo(CK_SLOT_ID slotID, CK_MECHANISM_TYPE type, CK_MECHANISM_INFO_PTR pInfo)
{   //fprintf(stdout,"\nC: called: C_GetMechanismInfo    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        jclass mechanismInfoClass = (*(environment))->FindClass(environment, "Lobj/CK_MECHANISM_INFO;");
        jmethodID mechanismInfoConstructor = (*(environment))->GetMethodID(environment,mechanismInfoClass, "<init>", "(JJJ)V");
        jobject mechanismInfo = (*(environment))->NewObject(environment, mechanismInfoClass, mechanismInfoConstructor, 0, 0, 0);


        jmethodID C_GetMechanismInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetMechanismInfo", "(JJLobj/CK_MECHANISM_INFO;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetMechanismInfoJava, slotID, type, mechanismInfo);
        (*(environment))->ExceptionDescribe(environment);


        if(retVal==CKR_OK && pInfo!=NULL) {

            jmethodID get = (*(environment))->GetMethodID(environment,mechanismInfoClass, "getUlMinKeySize", "()J");
            pInfo->ulMinKeySize = (*(environment))->CallLongMethod(environment, mechanismInfo, get);
            get = (*(environment))->GetMethodID(environment,mechanismInfoClass, "getUlMaxKeySize", "()J");
            pInfo->ulMaxKeySize = (*(environment))->CallLongMethod(environment, mechanismInfo, get);
            get = (*(environment))->GetMethodID(environment,mechanismInfoClass, "getFlags", "()J");
            pInfo->flags = (*(environment))->CallLongMethod(environment, mechanismInfo, get);
        }

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }
//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_OpenSession(CK_SLOT_ID slotID, CK_FLAGS flags, CK_VOID_PTR pApplication, CK_NOTIFY Notify, CK_SESSION_HANDLE_PTR phSession)
{   //fprintf(stdout,"\nC: called: C_OpenSession    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

        jobject _phSession = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _phSession  =(*(environment))->NewObject(environment, longClass, constructorLong, *phSession);

        jmethodID C_OpenSessionJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_OpenSession", "(JJLobj/CK_ULONG_PTR;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_OpenSessionJava, slotID, flags, _phSession);
        (*(environment))->ExceptionDescribe(environment);


        if(retVal==CKR_OK) {
            jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
            *phSession = (*(environment))->CallLongMethod(environment, _phSession, getValue);
        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}

CK_RV C_CloseAllSessions(CK_SLOT_ID slotID)
{   //fprintf(stdout,"\nC: called: C_CloseAllSessions    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_CloseAllSessionsJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CloseAllSessions", "(J)J");

        if(C_CloseAllSessionsJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CloseAllSessionsJava, slotID);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_CloseSession(CK_SESSION_HANDLE hSession)
{   //fprintf(stdout,"\nC: called: C_CloseSession    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_CloseSessionJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CloseSession", "(J)J");

        if(C_CloseSessionJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CloseSessionJava, hSession);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


jobject createAttributeArray(JNIEnv* environment, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount) {
    //fprintf(stdout,"\n\n*****creating attributeARRAY****\n\n");
    if(pTemplate==NULL) {
        return NULL;
    }

    //sing* dings = get_instance();
    jobject pTemplateArray=NULL;
    jobject pValue = NULL;

    jclass ck_attributeClass = (*(environment))->FindClass(environment, "Lobj/CK_ATTRIBUTE;");
    jmethodID ck_attributeConstructor = (*(environment))->GetMethodID(environment, ck_attributeClass, "<init>", "(JLjava/lang/Object;J)V");
    pTemplateArray = (*(environment))->NewObjectArray(environment, ulCount,ck_attributeClass, NULL);

    int i;
    for(i=0; i<ulCount; i++) {
        if((pTemplate+i)->pValue==NULL) {
            //fprintf(stdout,"template->pValue==NULL\n");
            pValue=NULL;
        } else {
            //fprintf(stdout,"template->pValue!=NULL\n");
            pValue = createAttributeValue(environment, pTemplate+i);
        }
	//if(pValue!=NULL){
        CK_ULONG len = (pTemplate+i)->ulValueLen;
        jobject tmp=(*(environment))->NewObject(environment, ck_attributeClass, ck_attributeConstructor, (pTemplate+i)->type, pValue, len);
        (*(environment))->SetObjectArrayElement(environment, pTemplateArray, i, tmp);
	//}
    }

    return  pTemplateArray;
}
jobject createAttributeValue(JNIEnv* environment, CK_ATTRIBUTE_PTR attribute) {
    //sing* dings = get_instance();


    int type = getAttributeType(attribute->type);
    switch(type) {

        //long
    default:
    case 5:
    case 6:
    case 8:
    case 9:
    case 10:
    case 0: {
        //fprintf(stdout,"creating longvalue\n");
        CK_ULONG_PTR _pValue = (CK_ULONG_PTR) (attribute->pValue);
        //fprintf(stdout,"value: %d\n",*_pValue);
        jclass longClass = (*(environment))->FindClass(environment, "Ljava/lang/Long;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        jobject ob =  (*(environment))->NewObject(environment, longClass, constructorLong, *_pValue);
        //fprintf(stdout,"longvalue created!\n");
        return ob;
    }

    break;
    //bool
    case 1: {
        //fprintf(stdout,"creating boolvalue\n");
        CK_BBOOL* _pValue = (CK_BBOOL*) attribute->pValue;
        jclass longClass = (*(environment))->FindClass(environment, "Ljava/lang/Boolean;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(Z)V");
        return (*(environment))->NewObject(environment, longClass, constructorLong, _pValue);
    }
    break;
    //string
    case 2:
    case 7:
        //fprintf(stdout,"creating stringvalue |%s\n", attribute->pValue);
        //return (*(environment))->NewStringUTF(environment, attribute->pValue);

        //break;
        //byte[]
    case 3: {

        //fprintf(stdout,"creating byte[]value\n");
        jbyteArray array =  (*(environment))->NewByteArray(environment, attribute->ulValueLen);
        (*(environment))->SetByteArrayRegion(environment, array, 0, attribute->ulValueLen, attribute->pValue);
        return array;
    }
    break;
    //date
    case 4: {
        //fprintf(stdout,"creating date value\n");
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_DATE;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(IIIIIIII)V");
        CK_DATE* date = (CK_DATE*) (attribute->pValue);
        unsigned char* year = date->year;
        unsigned char* month = date->month;
        unsigned char* day = date->day;

        return (*(environment))->NewObject(environment, longClass, constructorLong, year[0], year[1], year[2], year[3], month[0], month[1], day[0], day[1] );
    }
    break;
    case 100: 
	return NULL;
    //handle wrapped attribute arrays...

    }

}



int getAttributeType(CK_ULONG type) {
    int value=0;
    switch(type) {
//CK_ULONG:		  0
//CK_BBOOL:		  1
//CK_CHAR :		  2
//ByteArray:		  3
//CK_DATE:		  4
//CK_OBJECT_CLASS:	  5
//CK_HW_FEATURE:	  6
//RFC2279string		  7
//CK_CERTIFICATE_TYPE	  8
//CK_KEY_TYPE		  9
//CK_MECHANISM_TYPE	 10
//CK_MECHANISM_TYPE_PTR to a CK_MECHANISM_TYPE array 11
//CK_ATTRIPUTE_PTR	12
//Big Integer		13

    case CKA_CLASS:
        value = 5;
        break;
    case CKA_TOKEN:
        value = 1;
        break;
    case CKA_PRIVATE:
        value = 1;
        break;
    case CKA_LABEL:
        value = 7;
        break;
    case CKA_APPLICATION:
        value = 7;
        break;
    case CKA_VALUE:
        value = 3;
        break;
    case CKA_OBJECT_ID:
        value = 3;
        break;
    case CKA_CERTIFICATE_TYPE:
        value = 8;
        break;
    case CKA_ISSUER:
        value = 3;
        break;
    case CKA_SERIAL_NUMBER:
        value = 3;
        break;
    case CKA_AC_ISSUER:
        value = 3;
        break;
    case CKA_OWNER:
        value = 3;
        break;
    case CKA_ATTR_TYPES:
        value = 3;
        break;
    case CKA_TRUSTED:
        value = 1;
        break;
    case CKA_CERTIFICATE_CATEGORY:
        value = 0;
        break;
    case CKA_JAVA_MIDP_SECURITY_DOMAIN:
        value = 0;
        break;
    case CKA_URL:
        value = 7;
        break;
    case CKA_HASH_OF_SUBJECT_PUBLIC_KEY:
        value = 3;
        break;
    case CKA_HASH_OF_ISSUER_PUBLIC_KEY:
        value = 3;
        break;
    case CKA_CHECK_VALUE:
        value = 3;
        break;
    case CKA_KEY_TYPE:
        value = 9;
        break;
    case CKA_SUBJECT:
        value = 3;
        break;
    case CKA_ID:
        value = 3;
        break;
    case CKA_SENSITIVE:
        value = 1;
        break;
    case CKA_ENCRYPT:
        value = 1;
        break;
    case CKA_DECRYPT:
        value = 1;
        break;
    case CKA_WRAP:
        value = 1;
        break;
    case CKA_UNWRAP:
        value = 1;
        break;
    case CKA_SIGN:
        value = 1;
        break;
    case CKA_SIGN_RECOVER:
        value = 1;
        break;
    case CKA_VERIFY:
        value = 1;
        break;
    case CKA_VERIFY_RECOVER:
        value = 1;
        break;
    case CKA_DERIVE:
        value = 1;
        break;
    case CKA_START_DATE:
        value = 4;
        break;
    case CKA_END_DATE:
        value = 4;
        break;
    case CKA_MODULUS:
        value = 3;
        break;
    case CKA_MODULUS_BITS:
        value = 0;
        break;
    case CKA_PUBLIC_EXPONENT:
        value = 3;
        break;
    case CKA_PRIVATE_EXPONENT:
        value = 3;
        break;
    case CKA_PRIME_1:
        value = 0;
        break;
    case CKA_PRIME_2:
        value = 0;
        break;
    case CKA_EXPONENT_1:
        value = 0;
        break;
    case CKA_EXPONENT_2:
        value = 0;
        break;
    case CKA_COEFFICIENT:
        value = 0;
        break;
    case CKA_PRIME:
        value = 0;
        break;
    case CKA_SUBPRIME:
        value = 0;
        break;
    case CKA_BASE:
        value = 0;
        break;
    case CKA_PRIME_BITS:
        value = 0;
        break;
    case CKA_SUB_PRIME_BITS:
        value = 0;
        break;
    case CKA_VALUE_BITS:
        value = 0;
        break;
    case CKA_VALUE_LEN:
        value = 0;
        break;
    case CKA_EXTRACTABLE:
        value = 1;
        break;
    case CKA_LOCAL:
        value = 1;
        break;
    case CKA_NEVER_EXTRACTABLE:
        value = 1;
        break;
    case CKA_ALWAYS_SENSITIVE:
        value = 1;
        break;
    case CKA_KEY_GEN_MECHANISM:
        value = 10;
        break;
    case CKA_MODIFIABLE:
        value = 1;
        break;
    case CKA_ECDSA_PARAMS:
        value = 0;
        break;
    case CKA_EC_POINT:
        value = 0;
        break;
    case CKA_SECONDARY_AUTH:
        value = 0;
        break;
    case CKA_AUTH_PIN_FLAGS:
        value = 0;
        break;
    case CKA_ALWAYS_AUTHENTICATE:
        value = 1;
        break;
    case CKA_WRAP_WITH_TRUSTED:
        value = 1;
        break;
    case CKA_WRAP_TEMPLATE:
        value = 100;
        break;
    case CKA_UNWRAP_TEMPLATE:
        value = 100;
        break;
    case CKA_OTP_FORMAT:
        value = 0;
        break;
    case CKA_OTP_LENGTH:
        value = 0;
        break;
    case CKA_OTP_TIME_INTERVAL:
        value = 0;
        break;
    case CKA_OTP_USER_FRIENDLY_MODE:
        value = 0;
        break;
    case CKA_OTP_CHALLENGE_REQUIREMENT:
        value = 0;
        break;
    case CKA_OTP_TIME_REQUIREMENT:
        value = 0;
        break;
    case CKA_OTP_COUNTER_REQUIREMENT:
        value = 0;
        break;
    case CKA_OTP_PIN_REQUIREMENT:
        value = 0;
        break;
    case CKA_OTP_COUNTER:
        value = 0;
        break;
    case CKA_OTP_TIME:
        value = 0;
        break;
    case CKA_OTP_USER_IDENTIFIER:
        value = 0;
        break;
    case CKA_OTP_SERVICE_IDENTIFIER:
        value = 0;
        break;
    case CKA_OTP_SERVICE_LOGO:
        value = 0;
        break;
    case CKA_OTP_SERVICE_LOGO_TYPE:
        value = 0;
        break;
    case CKA_HW_FEATURE_TYPE:
        value = 6;
        break;
    case CKA_RESET_ON_INIT:
        value = 1;
        break;
    case CKA_HAS_RESET:
        value = 1;
        break;
    case CKA_PIXEL_X:
        value = 0;
        break;
    case CKA_PIXEL_Y:
        value = 0;
        break;
    case CKA_RESOLUTION:
        value = 0;
        break;
    case CKA_CHAR_ROWS:
        value = 0;
        break;
    case CKA_CHAR_COLUMNS:
        value = 0;
        break;
    case CKA_COLOR:
        value = 0;
        break;
    case CKA_BITS_PER_PIXEL:
        value = 0;
        break;
    case CKA_CHAR_SETS:
        value = 0;
        break;
    case CKA_ENCODING_METHODS:
        value = 0;
        break;
    case CKA_MIME_TYPES:
        value = 0;
        break;
    case CKA_MECHANISM_TYPE:
        value = 10;
        break;
    case CKA_REQUIRED_CMS_ATTRIBUTES:
        value = 0;
        break;
    case CKA_DEFAULT_CMS_ATTRIBUTES:
        value = 0;
        break;
    case CKA_SUPPORTED_CMS_ATTRIBUTES:
        value = 0;
        break;
    case CKA_ALLOWED_MECHANISMS:
        value = 11;
        break;  // not yet suported!
    case CKA_VENDOR_DEFINED:
        value = 0;
        break;
    }
    return value;
}



CK_RV C_CreateObject(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phObject)
{   //fprintf(stdout,"\nC: called: C_CreateObject    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

        jobject array = createAttributeArray(environment, pTemplate, ulCount);


        jobject _phObject = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _phObject  =(*(environment))->NewObject(environment, longClass, constructorLong, *phObject);

        jmethodID C_CreateObjectJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CreateObject", "(J[Lobj/CK_ATTRIBUTE;JLobj/CK_ULONG_PTR;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CreateObjectJava, hSession, array, ulCount, _phObject);
        (*(environment))->ExceptionDescribe(environment);

        if(phObject!=NULL) {
            jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
            *phObject = (*(environment))->CallLongMethod(environment, _phObject, getValue);
	    //fprintf(stdout, "Created Object with handle %d\n", *phObject);
        }



    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_DecryptInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{   //fprintf(stdout,"\nC: called: C_DecryptInit    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DecryptInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DecryptInit", "(JLobj/CK_MECHANISM;J)J");

	
        jclass mechanismClass = (*(environment))->FindClass(environment, "Lobj/CK_MECHANISM;");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject _pMechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, pMechanism->mechanism, NULL, 0);
	
	//printf("decryptInit__:%d\n", pMechanism->mechanism);


            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptInitJava, hSession, _pMechanism, hKey);
            (*(environment))->ExceptionDescribe(environment);

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_DecryptUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{   //fprintf(stdout,"\nC: called: C_DecryptUpdate    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
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
            if(pPart != NULL) {
                jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
                jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                obj3=(*(environment))->NewObject(environment, cls3, constructor3, pPart, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj3==NULL) {
                } else {
                }
            } else {
                obj3=NULL;
            }
            jobject obj4;
            if(pulPartLen != NULL) {
                jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
                jmethodID constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                obj4=(*(environment))->NewObject(environment, cls4, constructor4, pulPartLen, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj4==NULL) {
                } else {
                }
            } else {
                obj4=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptUpdateJava, hSession, result, ulEncryptedPartLen, obj3, obj4);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_DestroyObject(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject)
{   //fprintf(stdout,"\nC: called: C_DestroyObject    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DestroyObjectJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DestroyObject", "(JJ)J");

        if(C_DestroyObjectJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DestroyObjectJava, hSession, hObject);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}




CK_RV C_FindObjects(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE_PTR phObject, CK_ULONG ulMaxObjectCount, CK_ULONG_PTR pulObjectCount)
{   //fprintf(stdout,"\nC: called: C_FindObjects    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

// CK_ULONG_PTR
        jobject _pulObjectCount = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _pulObjectCount  =(*(environment))->NewObject(environment, longClass, constructorLong, *pulObjectCount);


//empty long array
        jlongArray _phObject = NULL;
        if(phObject != NULL && *pulObjectCount >=0) {
            _phObject= (*(environment))->NewLongArray(environment, ulMaxObjectCount);
        }


        jmethodID C_FindObjectsJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjects", "(J[JJLobj/CK_ULONG_PTR;)J");


        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsJava, hSession, _phObject, ulMaxObjectCount, _pulObjectCount);
        (*(environment))->ExceptionDescribe(environment);

        //fprintf(stdout,"\n backToBlack \n");


        jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *pulObjectCount = (*(environment))->CallLongMethod(environment, _pulObjectCount, getValue);

        if(retVal==CKR_OK) {

            if(phObject!=NULL) {
                jlong* val = (*(environment))->GetLongArrayElements(environment, _phObject, NULL);
                jlong len = (*(environment))->GetArrayLength(environment, _phObject);
                memcpy(phObject, val, len*sizeof(CK_ULONG));

            }
        }



    }

    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_FindObjectsFinal(CK_SESSION_HANDLE hSession)
{   //fprintf(stdout,"\nC: called: C_FindObjectsFinal    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_FindObjectsFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjectsFinal", "(J)J");

        if(C_FindObjectsFinalJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsFinalJava, hSession);
            (*(environment))->ExceptionDescribe(environment);
            //fprintf(stdout,"findObjectsFinal is back to c again...");
        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_FindObjectsInit(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{   //fprintf(stdout,"\nC: called: C_FindObjectsInit    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jobject array = createAttributeArray(environment, pTemplate, ulCount);

        jmethodID C_FindObjectsInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjectsInit", "(J[Lobj/CK_ATTRIBUTE;J)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsInitJava, hSession, array, ulCount);
        (*(environment))->ExceptionDescribe(environment);

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_GenerateRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR RandomData, CK_ULONG ulRandomLen)
{   //fprintf(stdout,"\nC: called: C_GenerateRandom    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_GenerateRandomJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GenerateRandom", "(JLproxys/CK_BYTE_ARRAY;J)J");

        if(C_GenerateRandomJava !=0)
        {
            jobject obj1;
            if(RandomData != NULL) {
                jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
                jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                obj1=(*(environment))->NewObject(environment, cls1, constructor1, RandomData, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj1==NULL) {
                } else {
                }
            } else {
                obj1=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GenerateRandomJava, hSession, obj1, ulRandomLen);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_GetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{   //fprintf(stdout,"\nC: called: C_GetAttributeValue    ");
    //fprintf(stdout,"C: hObject: %d\n", hObject);
    //fprintf(stdout,"C: hObject: %p\n", pTemplate);
    //fprintf(stdout,"C: hObject: %d\n", ulCount);
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        jobject array = NULL;
        if(pTemplate!=NULL) {
            array = createAttributeArray(environment, pTemplate, ulCount);
        }

        jmethodID C_GetAttributeValueJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetAttributeValue", "(JJ[Lobj/CK_ATTRIBUTE;J)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetAttributeValueJava, hSession, hObject, array, ulCount);
        (*(environment))->ExceptionDescribe(environment);

        //fprintf(stdout,"\n backToBlack \n");
        copyDataToPTR(environment, pTemplate, ulCount, array, retVal);
        //fprintf(stdout,"\n the pure black...\n");

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}
void copyDataToPTR(JNIEnv* environment, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, jarray array, CK_ULONG retVal) {
    jclass ckClass = (*(environment))->FindClass(environment, "Lobj/CK_ATTRIBUTE;");
    jmethodID getpValue = (*(environment))->GetMethodID(environment, ckClass, "getpValue", "()Ljava/lang/Object;");
    jmethodID getType = (*(environment))->GetMethodID(environment, ckClass, "getType", "()J");
    jmethodID getLength = (*(environment))->GetMethodID(environment, ckClass, "getUlValueLen", "()J");
    int i=0;

    for(i=0; i< ulCount; i++) {

        CK_VOID_PTR ptr = (pTemplate+i)->pValue;
        CK_ULONG type = (pTemplate+i)->type;

        jobject val = (*(environment))->GetObjectArrayElement(environment, array, i);

        CK_ULONG ckType = (*(environment))->CallLongMethod(environment, val, getType);
        CK_ULONG ckLen = (*(environment))->CallLongMethod(environment, val, getLength);
        (pTemplate+i)->type = ckType;
        ((pTemplate+i)->ulValueLen) = ckLen;
        if(retVal !=CKR_OK && retVal!=CKR_ATTRIBUTE_TYPE_INVALID) {
            return;
        }

        int dataType = getAttributeType(type);
        switch(dataType) {

            //long
        default:
        case 5:
        case 6:
        case 8:
        case 9:
        case 10:
        case 0: {
            //fprintf(stdout,"copy back some long value\n");
            if(ptr!=NULL) {
                jmethodID getpValueL = (*(environment))->GetMethodID(environment, ckClass, "getpValueAsLong", "()J");
                CK_ULONG ckType = (*(environment))->CallLongMethod(environment, val, getpValueL);
                CK_ULONG_PTR p = (CK_ULONG_PTR) ptr;
                *p=ckType;
                //fprintf(stdout,"really copying back some long value: %d\n",*p);
                //fprintf(stdout,"really copying back some long value: %d\n",ckType);
            }
        }
        break;
        //bool
        case 1: {
            //fprintf(stdout,"copy back some bool value\n");
            if(ptr!=NULL) {
                jmethodID getpValueB = (*(environment))->GetMethodID(environment, ckClass, "getpValueAsBool", "()Z");
                CK_BBOOL ckType = (*(environment))->CallBooleanMethod(environment, val, getpValueB);
                CK_BBOOL* p = (CK_BBOOL*) ptr;
                *p=ckType;
            }
        }
        break;
        //string
        case 2:
        case 7:
            //return (*(environment))->NewStringUTF(environment, attribute->pValue);
            //fprintf(stdout,"copy back some string value\n");
		
     /*       if(ptr!=NULL) {
                jstring ckpValue = (*(environment))->CallObjectMethod(environment, val, getpValue);
		jsize strnlen =0;
		if(ckpValue==NULL){
                    //fprintf(stdout,"ERROR: ckpValue == NULL\n");
		}else{
                    //fprintf(stdout,"SUCCESS: woohooo!\n    $$$$$$ ---> %s <------$$$$$$",ckpValue);
                    strnlen = (*(environment))->GetStringLength(environment, ckpValue);
                    //fprintf(stdout,"SUCCESS: ckpValue != NULL\n");
		}
                if(strnlen != ckLen) {
                    //fprintf(stdout,"ERROR: Problem with Stringlength in copyDataToPtr(..)\n");
                    //fprintf(stdout,"ERROR: strnlen: %d    ckLen: %d\n", strnlen, ckLen);
                    if(strnlen > ckLen) {

                    } else {
                        ckLen = strnlen;
                        ((pTemplate+i)->ulValueLen)=ckLen;

                    }
                }
                (*(environment))->GetStringUTFRegion(environment, ckpValue, 0, ckLen, ((char*)ptr));
            }
            //fprintf(stdout,"finished: copy back some string value\n");
            break;
      */      //byte[]
        case 12:
        case 3: {
            //fprintf(stdout,"copy back some byte[] value\n");

            if(ptr!=NULL) {
                jbyteArray ckpValue = (*(environment))->CallObjectMethod(environment, val, getpValue);
                jsize arrlen = (*(environment))->GetArrayLength(environment, ckpValue);
                if(arrlen != ckLen) {
                    //fprintf(stdout,"ERROR: Problem with Arraylength in copyDataToPtr(..)\n");
                    //fprintf(stdout,"ERROR: arrlen: %d    ckLen: %d\n", arrlen, ckLen);
                    if(arrlen>ckLen) {

                    } else {

                        ckLen = arrlen;
                        ((pTemplate+i)->ulValueLen)=ckLen;
                    }

                }


                jbyte* bytes=(*(environment))->GetByteArrayElements(environment, ckpValue, NULL);
                memcpy((CK_BYTE_PTR)ptr, bytes, ckLen);
            }



        }
        break;
        //date
        case 4: {
            //fprintf(stdout,"copy back some CK_DATE value\n");

            CK_DATE* date = (CK_DATE*) ((pTemplate+i)->pValue);
            date->year[0]=1;
            date->year[1]=9;
            date->year[2]=8;
            date->year[3]=2;
            date->month[0]=0;
            date->month[1]=2;
            date->day[0]=0;
            date->day[1]=8;

        }
        break;
        //handle wrapped attribute arrays...

        }


    }
}




CK_RV C_GetInfo(CK_INFO_PTR pInfo)
{   //fprintf(stdout,"\nC: called: C_GetInfo    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jclass versionClass = (*(environment))->FindClass(environment, "Lobj/CK_VERSION;");
        jmethodID versionConstructor = (*(environment))->GetMethodID(environment, versionClass, "<init>", "(BB)V");
        jobject version1=(*(environment))->NewObject(environment, versionClass, versionConstructor, 0, 0);
        jobject version2=(*(environment))->NewObject(environment, versionClass, versionConstructor, 0, 0);



        jobject _pInfo = NULL;
        jclass infoClass = (*(environment))->FindClass(environment, "Lobj/CK_INFO;");
        jmethodID constructorInfo = (*(environment))->GetMethodID(environment, infoClass, "<init>", "(Lobj/CK_VERSION;Ljava/lang/String;JLjava/lang/String;Lobj/CK_VERSION;)V");
        _pInfo  =(*(environment))->NewObject(environment, infoClass, constructorInfo, version1, NULL, 0, NULL, version2);



        jmethodID C_GetInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetInfo", "(Lobj/CK_INFO;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetInfoJava, _pInfo);
        (*(environment))->ExceptionDescribe(environment);

        jmethodID getLibraryDescription = (*(environment))->GetMethodID(environment, infoClass,"getLibraryDescription", "()Ljava/lang/String;");
        jstring libraryDescription = (*(environment))->CallObjectMethod(environment, _pInfo, getLibraryDescription);
        jsize lenDescription = (*(environment))->GetStringLength(environment, libraryDescription);
        (*(environment))->GetStringUTFRegion(environment, libraryDescription, 0, lenDescription, ((char*)pInfo->libraryDescription));

        jmethodID getmanufacturerID = (*(environment))->GetMethodID(environment, infoClass,"getManufacturerID", "()Ljava/lang/String;");
        jstring manufacturerID = (*(environment))->CallObjectMethod(environment, _pInfo, getmanufacturerID);

        jsize lenManufacturerID = (*(environment))->GetStringLength(environment, manufacturerID);
        (*(environment))->GetStringUTFRegion(environment, manufacturerID, 0, lenManufacturerID, ((char*)pInfo->manufacturerID));


        jmethodID getFlags = (*(environment))->GetMethodID(environment, infoClass,"getFlags", "()J");
        jlong flags = (*(environment))->CallLongMethod(environment, _pInfo, getFlags);
        pInfo->flags= flags;

        jmethodID getCryptokiVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jbyte cryptokiVersionMajor = (*(environment))->CallLongMethod(environment, version1, getCryptokiVersionMajor);
        pInfo->cryptokiVersion.major=cryptokiVersionMajor;
        jmethodID getCryptokiVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jbyte cryptokiVersionMinor = (*(environment))->CallLongMethod(environment, version1, getCryptokiVersionMinor);
        pInfo->cryptokiVersion.minor=cryptokiVersionMinor;

        jmethodID getLibraryVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jbyte libraryVersionMajor = (*(environment))->CallLongMethod(environment, version2, getLibraryVersionMajor);
        pInfo->libraryVersion.major=libraryVersionMajor;
        jmethodID getLibraryVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jbyte libraryVersionMinor = (*(environment))->CallLongMethod(environment, version2, getLibraryVersionMinor);
        pInfo->libraryVersion.minor=libraryVersionMinor;




    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}




CK_RV C_GetSessionInfo(CK_SESSION_HANDLE hSession, CK_SESSION_INFO_PTR pInfo)
{   //fprintf(stdout,"\nC: called: C_GetSessionInfo    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        jclass sessionInfoClass = (*(environment))->FindClass(environment, "Lobj/CK_SESSION_INFO;");
        jmethodID sessionInfoConstructor = (*(environment))->GetMethodID(environment,sessionInfoClass, "<init>", "(JJJJ)V");
        jobject sessionInfo = (*(environment))->NewObject(environment, sessionInfoClass, sessionInfoConstructor, 0, 0, 0, 0);

        jmethodID C_GetSessionInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSessionInfo", "(JLobj/CK_SESSION_INFO;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSessionInfoJava, hSession, sessionInfo);
        (*(environment))->ExceptionDescribe(environment);


        if(retVal==CKR_OK) {
            jmethodID get = (*(environment))->GetMethodID(environment,sessionInfoClass, "getSlotID", "()J");
            pInfo->slotID = (*(environment))->CallLongMethod(environment, sessionInfo, get);
            get = (*(environment))->GetMethodID(environment,sessionInfoClass, "getState", "()J");
            pInfo->state = (*(environment))->CallLongMethod(environment, sessionInfo, get);
            get = (*(environment))->GetMethodID(environment,sessionInfoClass, "getFlags", "()J");
            pInfo->flags = (*(environment))->CallLongMethod(environment, sessionInfo, get);
            get = (*(environment))->GetMethodID(environment,sessionInfoClass, "getUlDeviceError", "()J");
            pInfo->ulDeviceError = (*(environment))->CallLongMethod(environment, sessionInfo, get);



        }

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}










CK_RV C_Login(CK_SESSION_HANDLE hSession, CK_USER_TYPE userType, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
{   //fprintf(stdout,"\nC: called: C_Login    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

        //string
        jstring _pPin = NULL;
        if(pPin!=NULL) {
            _pPin =(*(environment))->NewStringUTF(environment, (const char *)pPin);
        }



        jmethodID C_LoginJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Login", "(JJLjava/lang/String;)J");

        if(C_LoginJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_LoginJava, hSession, userType, _pPin);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_Logout(CK_SESSION_HANDLE hSession)
{   //fprintf(stdout,"\nC: called: C_Logout    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_LogoutJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Logout", "(J)J");

        if(C_LogoutJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_LogoutJava, hSession);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}




CK_RV C_SeedRandom(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSeed, CK_ULONG ulSeedLen)
{   //fprintf(stdout,"\nC: called: C_SeedRandom    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SeedRandomJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SeedRandom", "(JLjava/lang/String;J)J");

        if(C_SeedRandomJava !=0)
        {
            jobject string1;
            string1 =(*(environment))->NewStringUTF(environment,(const char *) pSeed);
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SeedRandomJava, hSession, string1, ulSeedLen);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_SetAttributeValue(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{   //fprintf(stdout,"\nC: called: C_SetAttributeValue    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
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
                for(i=0; i<ulCount; i++)
                {
                    jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");

                    (*(environment))->ExceptionDescribe(environment);
                    obj2=(*(environment))->NewObject(environment, cls2, constructor2, pTemplate+i, JNI_FALSE);

                    (*(environment))->ExceptionDescribe(environment);


                    if (pTemplate+i == NULL) {
                        obj2 = NULL;

                    }
                    (*(environment))->SetObjectArrayElement(environment, array,i, obj2);
                    (*(environment))->ExceptionDescribe(environment);

                }

            } else {
                array = NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SetAttributeValueJava, hSession, hObject, array, ulCount);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_SetPIN(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pOldPin, CK_ULONG ulOldLen, CK_CHAR_PTR pNewPin, CK_ULONG ulNewLen)
{   //fprintf(stdout,"\nC: called: C_SetPIN    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SetPINJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SetPIN", "(JLjava/lang/String;JLjava/lang/String;J)J");

        if(C_SetPINJava !=0)
        {
            jobject string1;
            string1 =(*(environment))->NewStringUTF(environment,(const char *) pOldPin);
            jobject string3;
            string3 =(*(environment))->NewStringUTF(environment, (const char *)pNewPin);
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SetPINJava, hSession, string1, ulOldLen, string3, ulNewLen);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_Sign(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{   //fprintf(stdout,"\nC: called: C_Sign    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SignJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Sign", "(J[BJ[BLobj/CK_ULONG_PTR;)J");

        jbyteArray arr;
        jbyteArray arr2;
        if(pData!=NULL) {
            arr = (*(environment))->NewByteArray(environment, ulDataLen);
            (*(environment))->SetByteArrayRegion(environment, arr, 0, ulDataLen,(jbyte*)pData);
        }


        if(pSignature!=NULL) {
            arr2 = (*(environment))->NewByteArray(environment, *pulSignatureLen);
            (*(environment))->SetByteArrayRegion(environment, arr2, 0, *pulSignatureLen,(jbyte*)pSignature);
        }


        jobject _pulSignatureLen = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        if(pSignature!=NULL) {
            jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
            _pulSignatureLen  =(*(environment))->NewObject(environment, longClass, constructorLong, *pulSignatureLen);
        }

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignJava, hSession, arr, ulDataLen, arr2, _pulSignatureLen);
        (*(environment))->ExceptionDescribe(environment);



        if(pSignature!=NULL && retVal==CKR_OK) {

            jlong* val = (*(environment))->GetLongArrayElements(environment, arr2, NULL);
            jlong len = (*(environment))->GetArrayLength(environment, arr2);
            memcpy(pSignature, val, len*sizeof(CK_BYTE));
        }

        if(_pulSignatureLen !=NULL) {
            jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
            *pulSignatureLen = (*(environment))->CallLongMethod(environment, _pulSignatureLen, getValue);

        }



    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_SignInit(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{   //fprintf(stdout,"\nC: called: C_SignInit    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SignInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SignInit", "(JLobj/CK_MECHANISM;J)J");

        jclass mechanismClass = (*(environment))->FindClass(environment, "Lobj/CK_MECHANISM;");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject mechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, pMechanism->mechanism, NULL, 0);



        if(C_SignInitJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignInitJava, hSession, mechanism, hKey);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_UnwrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hUnwrappingKey, CK_BYTE_PTR pWrappedKey, CK_ULONG ulWrappedKeyLen, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
{   //fprintf(stdout,"\nC: called: C_UnwrapKey    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_UnwrapKeyJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_UnwrapKey", "(JLobj/CK_MECHANISM;J[BJ[Lobj/CK_ATTRIBUTE;JLobj/CK_ULONG_PTR;)J");

        jclass mechanismClass = (*(environment))->FindClass(environment, "Lobj/CK_MECHANISM;");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject _pMechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, pMechanism->mechanism, NULL, 0);

        jobject array = createAttributeArray(environment, pTemplate, ulAttributeCount);

        jbyteArray _pWrappedKey;
        if(pWrappedKey!=NULL) {
            _pWrappedKey = (*(environment))->NewByteArray(environment, ulWrappedKeyLen);
            (*(environment))->SetByteArrayRegion(environment, _pWrappedKey, 0, ulWrappedKeyLen,(jbyte*)pWrappedKey);
        }

        jobject _phKey = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _phKey  =(*(environment))->NewObject(environment, longClass, constructorLong, *phKey);



        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_UnwrapKeyJava, hSession, _pMechanism, hUnwrappingKey, _pWrappedKey, ulWrappedKeyLen, array, ulAttributeCount, _phKey);
        (*(environment))->ExceptionDescribe(environment);

        jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *phKey = (*(environment))->CallLongMethod(environment, _phKey, getValue);



    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_WrapKey(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hWrappingKey, CK_OBJECT_HANDLE hKey, CK_BYTE_PTR pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen)
{   //fprintf(stdout,"\nC: called: C_WrapKey    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_WrapKeyJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_WrapKey", "(JLobj/CK_MECHANISM;JJ[BLobj/CK_ULONG_PTR;)J");



        jclass mechanismClass = (*(environment))->FindClass(environment, "Lobj/CK_MECHANISM;");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject _pMechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, pMechanism->mechanism, NULL, 0);


        jbyteArray _pWrappedKey;
        if(pWrappedKey!=NULL) {
            _pWrappedKey = (*(environment))->NewByteArray(environment, *pulWrappedKeyLen);
            (*(environment))->SetByteArrayRegion(environment, _pWrappedKey, 0, *pulWrappedKeyLen,(jbyte*)pWrappedKey);
        }


        jobject _pulWrappedKeyLen = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _pulWrappedKeyLen  =(*(environment))->NewObject(environment, longClass, constructorLong, *pulWrappedKeyLen);

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_WrapKeyJava, hSession, _pMechanism, hWrappingKey, hKey, _pWrappedKey, _pulWrappedKeyLen);
            (*(environment))->ExceptionDescribe(environment);
		
	if(pWrappedKey!=NULL){
                jbyte* bytes=(*(environment))->GetByteArrayElements(environment, _pWrappedKey, NULL);
                memcpy((CK_BYTE_PTR)pWrappedKey, bytes, *pulWrappedKeyLen);
	}

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_SignUpdate(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{   //fprintf(stdout,"\nC: called: C_SignUpdate    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
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

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_SignFinal(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{   //fprintf(stdout,"\nC: called: C_SignFinal    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SignFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SignFinal", "(JLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");

        if(C_SignFinalJava !=0)
        {
            jobject obj1;
            if(pSignature != NULL) {
                jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
                jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pSignature, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj1==NULL) {
                } else {
                }
            } else {
                obj1=NULL;
            }
            jobject obj2;
            if(pulSignatureLen != NULL) {
                jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
                jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                obj2=(*(environment))->NewObject(environment, cls2, constructor2, pulSignatureLen, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj2==NULL) {
                } else {
                }
            } else {
                obj2=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignFinalJava, hSession, obj1, obj2);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_DecryptFinal(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pLastPart, CK_ULONG_PTR pulLastPartLen)
{   //fprintf(stdout,"\nC: called: C_DecryptFinal    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DecryptFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DecryptFinal", "(JLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");

        if(C_DecryptFinalJava !=0)
        {
            jobject obj1;
            if(pLastPart != NULL) {
                jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
                jmethodID constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pLastPart, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj1==NULL) {
                } else {
                }
            } else {
                obj1=NULL;
            }
            jobject obj2;
            if(pulLastPartLen != NULL) {
                jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
                jmethodID constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                obj2=(*(environment))->NewObject(environment, cls2, constructor2, pulLastPartLen, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj2==NULL) {
                } else {
                }
            } else {
                obj2=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptFinalJava, hSession, obj1, obj2);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}


CK_RV C_Decrypt(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedData, CK_ULONG ulEncryptedDataLen, CK_BYTE_PTR pData, CK_ULONG_PTR pulDataLen)
{   //fprintf(stdout,"\nC: called: C_Decrypt    ");
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    JNIEnv* environment;
    (*(dings->jvm))->AttachCurrentThreadAsDaemon(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DecryptJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Decrypt", "(J[BJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");

        if(C_DecryptJava !=0)
        {
            jbyteArray result;
            result = (*(environment))->NewByteArray(environment, ulEncryptedDataLen);

            (*(environment))->ExceptionDescribe(environment);
            (*(environment))->SetByteArrayRegion(environment, result, 0, ulEncryptedDataLen,(jbyte*)pEncryptedData);
            jobject obj3;
            if(pData != NULL) {
                jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY"); //
                jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                obj3=(*(environment))->NewObject(environment, cls3, constructor3, pData, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj3==NULL) {
                } else {
                }
            } else {
                obj3=NULL;
            }
            jobject obj4;
            if(pulDataLen != NULL) {
                jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR"); //
                jmethodID constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                obj4=(*(environment))->NewObject(environment, cls4, constructor4, pulDataLen, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj4==NULL) {
                } else {
                }
            } else {
                obj4=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptJava, hSession, result, ulEncryptedDataLen, obj3, obj4);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

//g    (*(dings->jvm))->DetachCurrentThread(dings->jvm);
    return retVal;
}




#define CK_DEFINE_FUNCTION(returnType, name) returnType name
CK_DEFINE_FUNCTION(CK_RV, C_InitPIN)(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
{
    //fprintf(stdout,"not implemented function called: C_InitPIN\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{
    //fprintf(stdout,"not implemented function called: C_EncryptUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GetOperationState)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pOperationState, CK_ULONG_PTR pulOperationStateLen)
{
    //fprintf(stdout,"not implemented function called: C_GetOperationState\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pLastEncryptedPart, CK_ULONG_PTR pulLastEncryptedPartLen)
{
    //fprintf(stdout,"not implemented function called: C_EncryptFinal\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism)
{
    //fprintf(stdout,"not implemented function called: C_DigestInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_Digest)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pDigest, CK_ULONG_PTR pulDigestLen)
{
    //fprintf(stdout,"not implemented function called: C_Digest\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{
    //fprintf(stdout,"not implemented function called: C_DigestUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestKey)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hKey)
{
    //fprintf(stdout,"not implemented function called: C_DigestKey\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pDigest, CK_ULONG_PTR pulDigestLen)
{
    //fprintf(stdout,"not implemented function called: C_DigestFinal\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SignRecoverInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    //fprintf(stdout,"not implemented function called: C_SignRecoverInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SignRecover)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{
    //fprintf(stdout,"not implemented function called: C_SignRecover\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    //fprintf(stdout,"not implemented function called: C_VerifyInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_Verify)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen)
{
    //fprintf(stdout,"not implemented function called: C_Verify\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{
    //fprintf(stdout,"not implemented function called: C_VerifyUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen)
{
    //fprintf(stdout,"not implemented function called: C_VerifyFinal\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyRecoverInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    //fprintf(stdout,"not implemented function called: C_VerifyRecoverInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyRecover)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen, CK_BYTE_PTR pData, CK_ULONG_PTR pulDataLen)
{
    //fprintf(stdout,"not implemented function called: C_VerifyRecover\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestEncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{
    //fprintf(stdout,"not implemented function called: C_DigestEncryptUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DecryptDigestUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{
    //fprintf(stdout,"not implemented function called: C_DecryptDigestUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SignEncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{
    //fprintf(stdout,"not implemented function called: C_SignEncryptUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DecryptVerifyUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{
    //fprintf(stdout,"not implemented function called: C_DecryptVerifyUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GenerateKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phKey)
{
    //fprintf(stdout,"not implemented function called: C_GenerateKey\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GenerateKeyPair)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_ATTRIBUTE_PTR pPublicKeyTemplate, CK_ULONG ulPublicKeyAttributeCount, CK_ATTRIBUTE_PTR pPrivateKeyTemplate, CK_ULONG ulPrivateKeyAttributeCount, CK_OBJECT_HANDLE_PTR phPublicKey, CK_OBJECT_HANDLE_PTR phPrivateKey)
{
    //fprintf(stdout,"not implemented function called: C_GenerateKeyPair\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DeriveKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hBaseKey, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
{
    //fprintf(stdout,"not implemented function called: C_DeriveKey\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SetOperationState)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pOperationState, CK_ULONG ulOperationStateLen, CK_OBJECT_HANDLE hEncryptionKey, CK_OBJECT_HANDLE hAuthenticationKey)
{
    //fprintf(stdout,"not implemented function called: C_SetOperationState\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_CopyObject)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phNewObject)
{
    //fprintf(stdout,"not implemented function called: C_CopyObject\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GetObjectSize)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ULONG_PTR pulSize)
{
    //fprintf(stdout,"not implemented function called: C_GetObjectSize\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GetFunctionStatus)(CK_SESSION_HANDLE hSession)
{
    //fprintf(stdout,"not implemented function called: C_GetFunctionStatus\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_CancelFunction)(CK_SESSION_HANDLE hSession)
{
    //fprintf(stdout,"not implemented function called: C_CancelFunction\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_InitToken)(CK_SLOT_ID slotID, CK_CHAR_PTR pPin, CK_ULONG ulPinLen, CK_CHAR_PTR pLabel)
{
    //fprintf(stdout,"not implemented function called: C_InitToken\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    //fprintf(stdout,"not implemented function called: C_EncryptInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_Encrypt)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pEncryptedData, CK_ULONG_PTR pulEncryptedDataLen)
{
    //fprintf(stdout,"not implemented function called: C_Encrypt\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_WaitForSlotEvent)(CK_FLAGS flags, CK_SLOT_ID_PTR pSlot, CK_VOID_PTR pReserved) {
    //fprintf(stdout,"not implemented function called: C_WaitForSlotEvent\n");
    return 0x00000054;
}
