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

CK_DEFINE_FUNCTION(CK_RV, C_GetFunctionList)(CK_FUNCTION_LIST_PTR_PTR ppFunctionList) {
    *ppFunctionList=&pkcs11_functions;
    return CKR_OK;
}

CK_DEFINE_FUNCTION(CK_RV,C_Initialize)(CK_VOID_PTR pInitArgs)
{
    JNIEnv* environment;
    jmethodID C_InitializeJava ;
    CK_C_INITIALIZE_ARGS_PTR args = (CK_C_INITIALIZE_ARGS_PTR)pInitArgs;
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();

    if(pInitArgs!=NULL) {

        dings->CreateMutex = args->CreateMutexX;
        dings->DestroyMutex = args->DestroyMutex;
        dings->LockMutex = args->LockMutex;
        dings->UnlockMutex = args->UnlockMutex;
        dings->CreateMutex(&(dings->ppMutex));
    } else {
        dings->CreateMutex = NULL;
        dings->DestroyMutex = NULL;
        dings->LockMutex = NULL;
        dings->UnlockMutex = NULL;
    }


    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
	

    (*(dings->jvm))->AttachCurrentThread((dings->jvm),(void**)&environment,NULL);
printf("\n\nblafasl!\n %p %p\n", dings, dings->jvm);

    if(dings->cls !=0)
    {
        C_InitializeJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Initialize", "()J");

        if(C_InitializeJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_InitializeJava);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }




    return retVal;


}

CK_DEFINE_FUNCTION(CK_RV,C_Finalize)(CK_VOID_PTR pReserved)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    jmethodID C_FinalizeJava;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        C_FinalizeJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Finalize", "()J");

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
#ifndef WIN32
    pthread_cond_signal(&(dings->finish));
    pthread_join(instance->thread,NULL);
#else

#endif
    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_GetSlotList)(CK_BBOOL tokenPresent, CK_SLOT_ID_PTR pSlotList, CK_ULONG_PTR pulCount)
{


    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    jlongArray _pSlotList = NULL;
    jmethodID constructorLong;
    jboolean _tokenPresent;
    jmethodID C_GetSlotListJava;
    jmethodID getValue;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

        jobject _pulCount = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        (*(environment))->ExceptionDescribe(environment);
        constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        (*(environment))->ExceptionDescribe(environment);
        _pulCount  =(*(environment))->NewObject(environment, longClass, constructorLong, (jlong)*pulCount);
        (*(environment))->ExceptionDescribe(environment);



        if(pSlotList != NULL) {
            _pSlotList= (*(environment))->NewLongArray(environment, *pulCount);
            (*(environment))->ExceptionDescribe(environment);
        }

        _tokenPresent = tokenPresent;

        C_GetSlotListJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSlotList", "(Z[JLobj/CK_ULONG_PTR;)J");
        (*(environment))->ExceptionDescribe(environment);

        if(C_GetSlotListJava==NULL) {

        }
        if(C_GetSlotListJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSlotListJava, _tokenPresent, _pSlotList, _pulCount);
            (*(environment))->ExceptionDescribe(environment);
        }
        getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *pulCount = (*(environment))->CallLongMethod(environment, _pulCount, getValue);


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


    return retVal;
}

CK_DEFINE_FUNCTION(CK_RV,C_GetSlotInfo)(CK_SLOT_ID slotID, CK_SLOT_INFO_PTR pInfo)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    jclass versionClass;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

        jmethodID getHardwareVersionMajor;
        jbyte hardwareVersionMajor;
        jmethodID getHardwareVersionMinor;
        jbyte hardwareVersionMinor;
        jmethodID getFirmwareVersionMajor;
        jbyte firmwareVersionMajor;
        jmethodID getFirmwareVersionMinor;
        jbyte firmwareVersionMinor;
        jmethodID getFlags;
        jlong flags;
        jmethodID getmanufacturerID;
        jstring manufacturerID;
        jsize lenManufacturerID;
        const jchar* charManufacturer;
        jmethodID getSlotDescription;
        jstring slotDescription;
        jsize lenDescription;
        const jchar* charDescription;
        int x =0;
        jmethodID versionConstructor;
        jobject version1;
        jobject version2;
        jclass slotinfoClass;
        jmethodID slotinfoConstructor;
        jobject slotinfo;
        jmethodID C_GetSlotInfoJava;
        FILE* file=fopen("C:\\pthread\\my.log.txt", "a");



        versionClass = (*(environment))->FindClass(environment, "obj/CK_VERSION");
        versionConstructor = (*(environment))->GetMethodID(environment, versionClass, "<init>", "(BB)V");
        version1=(*(environment))->NewObject(environment, versionClass, versionConstructor, (jbyte)0, (jbyte)0);
        version2=(*(environment))->NewObject(environment, versionClass, versionConstructor, (jbyte)0, (jbyte)0);


        slotinfoClass = (*(environment))->FindClass(environment, "obj/CK_SLOT_INFO");
        slotinfoConstructor = (*(environment))->GetMethodID(environment,slotinfoClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;JLobj/CK_VERSION;Lobj/CK_VERSION;)V");
        slotinfo = (*(environment))->NewObject(environment, slotinfoClass, slotinfoConstructor, NULL, NULL, (jlong)0, version1, version2);

        C_GetSlotInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSlotInfo", "(JLobj/CK_SLOT_INFO;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSlotInfoJava, (jlong)slotID, slotinfo);
        fprintf(file, "beginning");
        fclose(file);
        (*(environment))->ExceptionDescribe(environment);

        getSlotDescription = (*(environment))->GetMethodID(environment, slotinfoClass,"getSlotDescription", "()Ljava/lang/String;");
        slotDescription = (*(environment))->CallObjectMethod(environment, slotinfo, getSlotDescription);
        lenDescription = (*(environment))->GetStringLength(environment, slotDescription);
        charDescription = (*(environment))->GetStringChars(environment, slotDescription, NULL);

        for(x=0; x<lenDescription; x++) {
            pInfo->slotDescription[x]=charDescription[x];
        }


        getmanufacturerID = (*(environment))->GetMethodID(environment, slotinfoClass,"getManufacturerID", "()Ljava/lang/String;");
        manufacturerID = (*(environment))->CallObjectMethod(environment, slotinfo, getmanufacturerID);
        lenManufacturerID = (*(environment))->GetStringLength(environment, manufacturerID);
        charManufacturer = (*(environment))->GetStringChars(environment, manufacturerID, NULL);

        for(x=0; x<lenManufacturerID; x++) {
            pInfo->manufacturerID[x]=charManufacturer[x];
        }

        getFlags = (*(environment))->GetMethodID(environment, slotinfoClass,"getFlags", "()J");
        flags = (*(environment))->CallLongMethod(environment, slotinfo, getFlags);
        pInfo->flags= flags;

        getHardwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        hardwareVersionMajor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMajor);
        getHardwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        hardwareVersionMinor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMinor);
        getFirmwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        firmwareVersionMajor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMajor);
        getFirmwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        firmwareVersionMinor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMinor);

        pInfo->hardwareVersion.major=hardwareVersionMajor;
        pInfo->hardwareVersion.minor=hardwareVersionMinor;
        pInfo->firmwareVersion.major=firmwareVersionMajor;
        pInfo->firmwareVersion.minor=firmwareVersionMinor;

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

    return retVal;
}

CK_DEFINE_FUNCTION(CK_RV,C_GetTokenInfo)(CK_SLOT_ID slotID, CK_TOKEN_INFO_PTR pInfo)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {



        jclass versionClass = (*(environment))->FindClass(environment, "obj/CK_VERSION");
        jmethodID versionConstructor = (*(environment))->GetMethodID(environment, versionClass, "<init>", "(BB)V");
        jobject version1=(*(environment))->NewObject(environment, versionClass, versionConstructor, (jbyte)0, (jbyte)0);
        jobject version2=(*(environment))->NewObject(environment, versionClass, versionConstructor, (jbyte)0, (jbyte)0);

        jclass tokeninfoClass = (*(environment))->FindClass(environment, "Lobj/CK_TOKEN_INFO;");
        jmethodID tokeninfoConstructor = (*(environment))->GetMethodID(environment,tokeninfoClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJJJJJJJJJJLobj/CK_VERSION;Lobj/CK_VERSION;Ljava/lang/String;)V");
        jobject tokeninfo = (*(environment))->NewObject(environment, tokeninfoClass, tokeninfoConstructor, NULL, NULL, NULL, NULL, (jlong)0 , (jlong)0, (jlong)0, (jlong)0, (jlong)0, (jlong)0, (jlong)0, (jlong)0, (jlong)0, (jlong)0, (jlong)0, version1, version2, NULL);

        jmethodID C_GetTokenInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetTokenInfo", "(JLobj/CK_TOKEN_INFO;)J");
        jmethodID getTokenLabel;
        jstring label;
        jsize lenLabel;
        jmethodID getTokenManufacturerID;
        jstring ManufacturerID;
        jsize lenManufacturerID;
        const jchar* charLabel;
        int i=0;

        jmethodID getTokenModel;
        jstring Model;
        jsize lenModel;
        const jchar* charModel;
        jmethodID getHardwareVersionMajor;
        jmethodID getHardwareVersionMinor;
        jmethodID getFirmwareVersionMajor;
        jmethodID getFirmwareVersionMinor;
        jmethodID getTime;
        jstring utcTime;
        jbyte hardwareVersionMajor;
        jbyte hardwareVersionMinor;
        jbyte firmwareVersionMajor;
        jbyte firmwareVersionMinor;
        jmethodID get;
        jmethodID getTokenSerialNumber;
        jstring SerialNumber;
        jsize lenSerialNumber;
        const jchar* charManufacturerID;
        const jchar* charSerialNumber;


        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetTokenInfoJava, (jlong)slotID,tokeninfo );
        (*(environment))->ExceptionDescribe(environment);



        getTokenLabel = (*(environment))->GetMethodID(environment, tokeninfoClass,"getLabel", "()Ljava/lang/String;");
        label = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenLabel);
        lenLabel = (*(environment))->GetStringLength(environment, label);
        charLabel = (*(environment))->GetStringChars(environment, label, NULL);
        for(i=0; i<lenLabel; i++) {
            pInfo->label[i]=charLabel[i];
        }

        getTokenManufacturerID = (*(environment))->GetMethodID(environment, tokeninfoClass,"getManufacturerID", "()Ljava/lang/String;");
        ManufacturerID = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenManufacturerID);
        lenManufacturerID = (*(environment))->GetStringLength(environment, ManufacturerID);
        charManufacturerID = (*(environment))->GetStringChars(environment, ManufacturerID, NULL);

        for(i=0; i<lenManufacturerID; i++) {
            pInfo->manufacturerID[i]=charManufacturerID[i];
        }

        getTokenModel = (*(environment))->GetMethodID(environment, tokeninfoClass,"getModel", "()Ljava/lang/String;");
        Model = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenModel);
        lenModel = (*(environment))->GetStringLength(environment, Model);
        charModel = (*(environment))->GetStringChars(environment, Model, NULL);

        for(i=0; i<lenModel; i++) {
            pInfo->model[i]=charModel[i];
        }

        getTokenSerialNumber = (*(environment))->GetMethodID(environment, tokeninfoClass,"getSerialNumber", "()Ljava/lang/String;");
        SerialNumber = (*(environment))->CallObjectMethod(environment, tokeninfo, getTokenSerialNumber);
        lenSerialNumber = (*(environment))->GetStringLength(environment, SerialNumber);
        charSerialNumber = (*(environment))->GetStringChars(environment, SerialNumber, NULL);

        for(i=0; i<lenSerialNumber; i++) {
            pInfo->serialNumber[i]=charSerialNumber[i];
        }

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getFlags", "()J");
        pInfo->flags = (jlong)(*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMaxSessionCount", "()J");
        pInfo->ulMaxSessionCount =(jlong) (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlSessionCount", "()J");
        pInfo->ulSessionCount =(jlong) (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMaxRwSessionCount", "()J");
        pInfo-> ulMaxRwSessionCount =(jlong) (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlRwSessionCount", "()J");
        pInfo-> ulRwSessionCount =(jlong) (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMaxPinLen", "()J");
        pInfo-> ulMaxPinLen = (jlong)(*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlMinPinLen", "()J");
        pInfo-> ulMinPinLen = (jlong)(*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlTotalPublicMemory", "()J");
        pInfo-> ulTotalPublicMemory = (jlong)(*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlFreePublicMemory", "()J");
        pInfo-> ulFreePublicMemory = (jlong)(*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlTotalPrivateMemory", "()J");
        pInfo-> ulTotalPrivateMemory =(jlong) (*(environment))->CallLongMethod(environment, tokeninfo, get);

        get = (*(environment))->GetMethodID(environment, tokeninfoClass,"getUlFreePrivateMemory", "()J");
        pInfo-> ulFreePrivateMemory =(jlong) (*(environment))->CallLongMethod(environment, tokeninfo, get);



        getHardwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        hardwareVersionMajor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMajor);
        pInfo->hardwareVersion.major=hardwareVersionMajor;
        getHardwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        hardwareVersionMinor = (*(environment))->CallLongMethod(environment, version1, getHardwareVersionMinor);
        pInfo->hardwareVersion.minor=hardwareVersionMinor;

        getFirmwareVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        firmwareVersionMajor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMajor);
        pInfo->firmwareVersion.major=firmwareVersionMajor;
        getFirmwareVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        firmwareVersionMinor = (*(environment))->CallLongMethod(environment, version2, getFirmwareVersionMinor);
        pInfo->firmwareVersion.minor=firmwareVersionMinor;

        /*
        getTime = (*(environment))->GetMethodID(environment, tokeninfoClass, "getUtcTime", "()Ljava/lang/String;");
        utcTime = (*(environment))->CallObjectMethod(environment, tokeninfo, getTime);
        if(utcTime!=NULL) {
            (*(environment))->GetStringUTFRegion(environment, utcTime, 0, 16, ((char*)pInfo->utcTime));
        } else {
            int i=0;
            for(i=0; i<16; i++) {
                pInfo->utcTime[i]='\0';
            }
        }
        */
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}

CK_DEFINE_FUNCTION(CK_RV,C_GetMechanismList)(CK_SLOT_ID slotID, CK_MECHANISM_TYPE_PTR pMechanismList, CK_ULONG_PTR pulCount)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {



        jobject _pulCount = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        CK_SLOT_ID _slotID = slotID;
        jlongArray _pMechanismList = NULL;
        jmethodID C_GetMechanismListJava;
        jmethodID getValue;
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _pulCount  =(*(environment))->NewObject(environment, longClass, constructorLong, (jlong)*pulCount);




        if(pMechanismList != NULL) {
            _pMechanismList= (*(environment))->NewLongArray(environment, *pulCount);
        }


        C_GetMechanismListJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetMechanismList", "(J[JLobj/CK_ULONG_PTR;)J");
        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetMechanismListJava,(jlong)_slotID, _pMechanismList, _pulCount);


        getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *pulCount = (*(environment))->CallLongMethod(environment, _pulCount, getValue);


        if(_pMechanismList!=NULL && retVal==CKR_OK) {

            int j;
            jlong* data= (*(environment))->GetLongArrayElements(environment,_pMechanismList, NULL);
            (*(environment))->ExceptionDescribe(environment);
            for(j=0; j<*pulCount; j++) {
                pMechanismList[j]=data[j];
            }
        }



    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_GetMechanismInfo)(CK_SLOT_ID slotID, CK_MECHANISM_TYPE type, CK_MECHANISM_INFO_PTR pInfo)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        jclass mechanismInfoClass = (*(environment))->FindClass(environment, "obj/CK_MECHANISM_INFO");
        jmethodID mechanismInfoConstructor = (*(environment))->GetMethodID(environment,mechanismInfoClass, "<init>", "(JJJ)V");
        jobject mechanismInfo = (*(environment))->NewObject(environment, mechanismInfoClass, mechanismInfoConstructor, (jlong)0, (jlong)0, (jlong)0);


        jmethodID C_GetMechanismInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetMechanismInfo", "(JJLobj/CK_MECHANISM_INFO;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetMechanismInfoJava, (jlong)slotID, (jlong)type, mechanismInfo);
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

    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_OpenSession)(CK_SLOT_ID slotID, CK_FLAGS flags, CK_VOID_PTR pApplication, CK_NOTIFY Notify, CK_SESSION_HANDLE_PTR phSession)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

        jmethodID C_OpenSessionJava;

        jobject _phSession = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _phSession  =(*(environment))->NewObject(environment, longClass, constructorLong, (jlong)(*phSession));

        C_OpenSessionJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_OpenSession", "(JJLobj/CK_ULONG_PTR;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_OpenSessionJava, (jlong)slotID, (jlong)flags, _phSession);
        (*(environment))->ExceptionDescribe(environment);


        if(retVal==CKR_OK) {
            jlong lo;
            jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
            lo = (*(environment))->CallLongMethod(environment, _phSession, getValue);
            *phSession = (CK_ULONG) lo;
            printf("SessionID in C: %u\n", *phSession);
        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }
    return retVal;
}

CK_DEFINE_FUNCTION(CK_RV,C_CloseAllSessions)(CK_SLOT_ID slotID)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_CloseAllSessionsJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CloseAllSessions", "(J)J");

        if(C_CloseAllSessionsJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CloseAllSessionsJava, (jlong)slotID);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_CloseSession)(CK_SESSION_HANDLE hSession)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_CloseSessionJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CloseSession", "(J)J");

        if(C_CloseSessionJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CloseSessionJava, (jlong)hSession);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


jobject createAttributeArray(JNIEnv* environment, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount) {
    jobject pTemplateArray=NULL;
    jobject pValue = NULL;
    jclass ck_attributeClass;
    jmethodID ck_attributeConstructor;
    CK_ULONG i;

    if(pTemplate==NULL) {
        return NULL;
    }


    ck_attributeClass = (*(environment))->FindClass(environment, "obj/CK_ATTRIBUTE");
    ck_attributeConstructor = (*(environment))->GetMethodID(environment, ck_attributeClass, "<init>", "(JLjava/lang/Object;J)V");
    pTemplateArray = (*(environment))->NewObjectArray(environment, (jlong)ulCount,ck_attributeClass, NULL);

    for(i=0; i<ulCount; i++) {

        CK_ULONG len;
        jobject tmp;

        if((pTemplate+i)->pValue==NULL) {

            pValue=NULL;

        } else {
            pValue = createAttributeValue(environment, pTemplate+i);

        }

        len = (pTemplate+i)->ulValueLen;
        tmp=(*(environment))->NewObject(environment, ck_attributeClass, ck_attributeConstructor, (jlong)(pTemplate+i)->type, pValue, (jlong)len);
        (*(environment))->SetObjectArrayElement(environment, pTemplateArray, (jlong)i, tmp);
    }


    return  pTemplateArray;
}
jobject createAttributeValue(JNIEnv* environment, CK_ATTRIBUTE_PTR attribute) {



    int type = getAttributeType(attribute->type);
    switch(type) {

    default:
    case 5:
    case 6:
    case 8:
    case 9:
    case 10:
    case 0: {

        CK_ULONG_PTR _pValue = (CK_ULONG_PTR) (attribute->pValue);

        jclass longClass = (*(environment))->FindClass(environment, "Ljava/lang/Long;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        jobject ob =  (*(environment))->NewObject(environment, longClass, constructorLong, (jlong)*_pValue);

        return ob;
    }

    break;
    case 1: {
        CK_BBOOL* _pValue = (CK_BBOOL*) attribute->pValue;
        jclass longClass = (*(environment))->FindClass(environment, "java/lang/Boolean");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(Z)V");
		printf("\n\n strange bool value %u \n\n", (jboolean) _pValue);
		if(_pValue == 0){
			        return (*(environment))->NewObject(environment, longClass, constructorLong, JNI_FALSE);

		}else{
			        return (*(environment))->NewObject(environment, longClass, constructorLong, JNI_TRUE);

		}
    }
    break;
    case 2:
    case 7:

    case 3: {

        jbyteArray array =  (*(environment))->NewByteArray(environment, attribute->ulValueLen);
        (*(environment))->SetByteArrayRegion(environment, array, 0, attribute->ulValueLen, attribute->pValue);
        return array;
    }
    break;
    case 4: {
        jclass longClass = (*(environment))->FindClass(environment, "Lobj/CK_DATE;");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(IIIIIIII)V");
        CK_DATE* date = (CK_DATE*) (attribute->pValue);
        unsigned char* year = date->year;
        unsigned char* month = date->month;
        unsigned char* day = date->day;

        return (*(environment))->NewObject(environment, longClass, constructorLong, (jint)year[0],  (jint)year[1],  (jint)year[2],  (jint)year[3],  (jint)month[0],  (jint)month[1],  (jint)day[0],  (jint)day[1] );
    }
    break;
    case 100:
        return NULL;

    }

}



int getAttributeType(CK_ULONG type) {
    int value=0;
    switch(type) {
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
        break;
    case CKA_VENDOR_DEFINED:
        value = 0;
        break;
    }
    return value;
}



CK_DEFINE_FUNCTION(CK_RV,C_CreateObject)(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phObject)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_CreateObjectJava;
        jobject array = createAttributeArray(environment, pTemplate, ulCount);

        jobject _phObject = NULL;

        jclass longClass = (*(environment))->FindClass(environment, "obj/CK_ULONG_PTR");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _phObject  =(*(environment))->NewObject(environment, longClass, constructorLong, (jlong)*phObject);

        C_CreateObjectJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_CreateObject", "(J[Lobj/CK_ATTRIBUTE;JLobj/CK_ULONG_PTR;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_CreateObjectJava, (jlong)hSession, array, (jlong)ulCount, _phObject);
        (*(environment))->ExceptionDescribe(environment);

        if(phObject!=NULL) {
            jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
            *phObject = (*(environment))->CallLongMethod(environment, _phObject, getValue);

        }



    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_DecryptInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DecryptInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DecryptInit", "(JLobj/CK_MECHANISM;J)J");


        jclass mechanismClass = (*(environment))->FindClass(environment, "Lobj/CK_MECHANISM;");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject _pMechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, (jlong)pMechanism->mechanism, NULL, (jlong)0);




        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptInitJava, (jlong)hSession, _pMechanism, (jlong)hKey);
        (*(environment))->ExceptionDescribe(environment);

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_DecryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DecryptUpdateJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DecryptUpdate", "(J[BJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");

        if(C_DecryptUpdateJava !=0)
        {
            jbyteArray result;
            jobject obj3;
            jobject obj4;
            result = (*(environment))->NewByteArray(environment, ulEncryptedPartLen);

            (*(environment))->ExceptionDescribe(environment);
            (*(environment))->SetByteArrayRegion(environment, result, 0, ulEncryptedPartLen,(jbyte*)pEncryptedPart);
            if(pPart != NULL) {
                jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY");
                jmethodID constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                obj3=(*(environment))->NewObject(environment, cls3, constructor3, pPart, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj3==NULL) {
                } else {
                }
            } else {
                obj3=NULL;
            }
            if(pulPartLen != NULL) {
                jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR");
                jmethodID constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                obj4=(*(environment))->NewObject(environment, cls4, constructor4, pulPartLen, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj4==NULL) {
                } else {
                }
            } else {
                obj4=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptUpdateJava, (jlong)hSession, result, (jlong)ulEncryptedPartLen, obj3, obj4);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_DestroyObject)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DestroyObjectJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DestroyObject", "(JJ)J");

        if(C_DestroyObjectJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DestroyObjectJava, (jlong)hSession, (jlong)hObject);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

    return retVal;
}




CK_DEFINE_FUNCTION(CK_RV,C_FindObjects)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE_PTR phObject, CK_ULONG ulMaxObjectCount, CK_ULONG_PTR pulObjectCount)
{
    long retVal=CKR_GENERAL_ERROR;
    jlongArray _phObject = NULL;
    sing* dings = get_instance();
    JNIEnv* environment;
    jmethodID C_FindObjectsJava;
    jmethodID getValue;
    int i=0;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        jobject _pulObjectCount = NULL;
        jclass longClass = (*(environment))->FindClass(environment, "obj/CK_ULONG_PTR");
        jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _pulObjectCount  =(*(environment))->NewObject(environment, longClass, constructorLong, (jlong)*pulObjectCount);



        if(phObject != NULL && *pulObjectCount >=0) {
            _phObject= (*(environment))->NewLongArray(environment, (jlong)ulMaxObjectCount);
        }


        C_FindObjectsJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjects", "(J[JJLobj/CK_ULONG_PTR;)J");


        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsJava, (jlong)hSession, _phObject, (jlong)ulMaxObjectCount, _pulObjectCount);
        (*(environment))->ExceptionDescribe(environment);




        getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *pulObjectCount = (*(environment))->CallLongMethod(environment, _pulObjectCount, getValue);

        if(retVal==CKR_OK) {

            if(phObject!=NULL) {
                jlong* val = (*(environment))->GetLongArrayElements(environment, _phObject, NULL);
                jlong len = (*(environment))->GetArrayLength(environment, _phObject);
                for(i=0; i<len; i++) {
                    phObject[i]=val[i];
                }

            }
        }



    }

    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_FindObjectsFinal)(CK_SESSION_HANDLE hSession)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_FindObjectsFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjectsFinal", "(J)J");

        if(C_FindObjectsFinalJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsFinalJava, (jlong)hSession);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_FindObjectsInit)(CK_SESSION_HANDLE hSession, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    jlong jhSession =(jlong)hSession;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jobject array;

        jmethodID C_FindObjectsInitJava;


        array = createAttributeArray(environment, pTemplate, ulCount);

        C_FindObjectsInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_FindObjectsInit", "(J[Lobj/CK_ATTRIBUTE;J)J");



        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_FindObjectsInitJava, jhSession, array, (jlong)ulCount);
        (*(environment))->ExceptionDescribe(environment);

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_GenerateRandom)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR RandomData, CK_ULONG ulRandomLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_GenerateRandomJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GenerateRandom", "(J[BJ)J");

        if(C_GenerateRandomJava !=0)
        {
            retVal = CKR_OK;
        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_GetAttributeValue)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{



    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    jmethodID C_GetAttributeValueJava;
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        jarray array = NULL;
        if(pTemplate!=NULL) {
            array = createAttributeArray(environment, pTemplate, ulCount);
        }

        C_GetAttributeValueJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetAttributeValue", "(JJ[Lobj/CK_ATTRIBUTE;J)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetAttributeValueJava, (jlong)hSession, (jlong)hObject, array, (jlong)ulCount);
        (*(environment))->ExceptionDescribe(environment);


        copyDataToPTR(environment, pTemplate, ulCount, array, retVal);


    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}
void copyDataToPTR(JNIEnv* environment, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, jarray array, CK_ULONG retVal) {
    jclass ckClass = (*(environment))->FindClass(environment, "Lobj/CK_ATTRIBUTE;");
    jmethodID getpValue = (*(environment))->GetMethodID(environment, ckClass, "getpValue", "()Ljava/lang/Object;");
    jmethodID getType = (*(environment))->GetMethodID(environment, ckClass, "getType", "()J");
    jmethodID getLength = (*(environment))->GetMethodID(environment, ckClass, "getUlValueLen", "()J");
    int i=0;

    for(i=0; i< ulCount; i++) {
        int dataType;
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

        dataType = getAttributeType(type);
        switch(dataType) {


        default:
        case 5:
        case 6:
        case 8:
        case 9:
        case 10:
        case 0: {
            if(ptr!=NULL) {
                jmethodID getpValueL = (*(environment))->GetMethodID(environment, ckClass, "getpValueAsLong", "()J");
                CK_ULONG ckType = (*(environment))->CallLongMethod(environment, val, getpValueL);
                CK_ULONG_PTR p = (CK_ULONG_PTR) ptr;
                *p=ckType;
            }
        }
        break;

        case 1: {

            if(ptr!=NULL) {
                jmethodID getpValueB = (*(environment))->GetMethodID(environment, ckClass, "getpValueAsBool", "()Z");
                CK_BBOOL ckType = (*(environment))->CallBooleanMethod(environment, val, getpValueB);
                CK_BBOOL* p = (CK_BBOOL*) ptr;
                *p=ckType;
            }
        }
        break;

        case 2:
        case 7:



        case 12:
        case 3: {

            if(ptr!=NULL) {
                jbyte* bytes;
                jbyteArray ckpValue = (*(environment))->CallObjectMethod(environment, val, getpValue);
                jsize arrlen = (*(environment))->GetArrayLength(environment, ckpValue);
                if(arrlen != ckLen) {
                    if(arrlen>ckLen) {

                    } else {

                        ckLen = arrlen;
                        ((pTemplate+i)->ulValueLen)=ckLen;
                    }

                }


                bytes=(*(environment))->GetByteArrayElements(environment, ckpValue, NULL);
                memcpy((CK_BYTE_PTR)ptr, bytes, ckLen);
            }



        }
        break;
        case 4: {


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

        }


    }
}




CK_DEFINE_FUNCTION(CK_RV,C_GetInfo)(CK_INFO_PTR pInfo)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jclass versionClass = (*(environment))->FindClass(environment, "obj/CK_VERSION");
        jclass infoClass = (*(environment))->FindClass(environment, "obj/CK_INFO");
        jmethodID getFlags = (*(environment))->GetMethodID(environment, infoClass,"getFlags", "()J");
        jmethodID getmanufacturerID = (*(environment))->GetMethodID(environment, infoClass,"getManufacturerID", "()Ljava/lang/String;");
        jmethodID getLibraryDescription = (*(environment))->GetMethodID(environment, infoClass,"getLibraryDescription", "()Ljava/lang/String;");
        jmethodID getCryptokiVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jmethodID getCryptokiVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jmethodID getLibraryVersionMajor = (*(environment))->GetMethodID(environment, versionClass,"getMajor", "()B");
        jmethodID getLibraryVersionMinor = (*(environment))->GetMethodID(environment, versionClass,"getMinor", "()B");
        jmethodID C_GetInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetInfo", "(Lobj/CK_INFO;)J");


        jstring libraryDescription;
        jsize lenDescription;
        jstring manufacturerID;
        jsize lenManufacturerID;
        jlong flags;
        jbyte cryptokiVersionMajor;
        jbyte cryptokiVersionMinor;
        jbyte libraryVersionMajor;
        jbyte libraryVersionMinor;


        jmethodID versionConstructor = (*(environment))->GetMethodID(environment, versionClass, "<init>", "(BB)V");
        jobject version1=(*(environment))->NewObject(environment, versionClass, versionConstructor, (jbyte)0, (jbyte)0);
        jobject version2=(*(environment))->NewObject(environment, versionClass, versionConstructor, (jbyte)0, (jbyte)0);
        jstring str = (*(environment))->NewStringUTF(environment, "jniText");
        jobject _pInfo = NULL;

        jmethodID constructorInfo = (*(environment))->GetMethodID(environment, infoClass, "<init>", "(Lobj/CK_VERSION;Ljava/lang/String;JLjava/lang/String;Lobj/CK_VERSION;)V");

        _pInfo  =(*(environment))->NewObject(environment, infoClass, constructorInfo, version1, str,(jlong) 0, str, version2);


        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetInfoJava, _pInfo);

        (*(environment))->ExceptionDescribe(environment);
        libraryDescription = (*(environment))->CallObjectMethod(environment, _pInfo, getLibraryDescription);
        lenDescription = (*(environment))->GetStringLength(environment, libraryDescription);
        manufacturerID = (*(environment))->CallObjectMethod(environment, _pInfo, getmanufacturerID);
        lenManufacturerID = (*(environment))->GetStringLength(environment, manufacturerID);
        flags = (*(environment))->CallLongMethod(environment, _pInfo, getFlags);
        cryptokiVersionMajor = (*(environment))->CallLongMethod(environment, version1, getCryptokiVersionMajor);
        cryptokiVersionMinor = (*(environment))->CallLongMethod(environment, version1, getCryptokiVersionMinor);
        libraryVersionMajor = (*(environment))->CallLongMethod(environment, version2, getLibraryVersionMajor);
        libraryVersionMinor = (*(environment))->CallLongMethod(environment, version2, getLibraryVersionMinor);

        pInfo->flags= flags;
        pInfo->cryptokiVersion.major=cryptokiVersionMajor;
        pInfo->cryptokiVersion.minor=cryptokiVersionMinor;
        pInfo->libraryVersion.major=libraryVersionMajor;
        pInfo->libraryVersion.minor=libraryVersionMinor;
        (*(environment))->GetStringUTFRegion(environment, manufacturerID, 0, lenManufacturerID, ((char*)pInfo->manufacturerID));
        (*(environment))->GetStringUTFRegion(environment, libraryDescription, 0, lenDescription, ((char*)pInfo->libraryDescription));

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}




CK_DEFINE_FUNCTION(CK_RV,C_GetSessionInfo)(CK_SESSION_HANDLE hSession, CK_SESSION_INFO_PTR pInfo)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    jlong jhSession = (jlong) hSession;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {


        jclass sessionInfoClass = (*(environment))->FindClass(environment, "Lobj/CK_SESSION_INFO;");
        jmethodID sessionInfoConstructor = (*(environment))->GetMethodID(environment,sessionInfoClass, "<init>", "(JJJJ)V");
        jobject sessionInfo = (*(environment))->NewObject(environment, sessionInfoClass, sessionInfoConstructor, (jlong)0,  (jlong)0,  (jlong)0, (jlong)0);

        jmethodID C_GetSessionInfoJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_GetSessionInfo", "(JLobj/CK_SESSION_INFO;)J");

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_GetSessionInfoJava, jhSession, sessionInfo);
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


    return retVal;
}










CK_DEFINE_FUNCTION(CK_RV,C_Login)(CK_SESSION_HANDLE hSession, CK_USER_TYPE userType, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    jstring _pPin;
    jmethodID C_LoginJava;
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {

        _pPin = NULL;
        if(pPin!=NULL) {
            _pPin =(*(environment))->NewStringUTF(environment, (const char *)pPin);
        }



        C_LoginJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Login", "(JJLjava/lang/String;)J");

        if(C_LoginJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_LoginJava, (jlong)hSession, (jlong)userType, _pPin);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_Logout)(CK_SESSION_HANDLE hSession)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_LogoutJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Logout", "(J)J");

        if(C_LogoutJava !=0)
        {

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_LogoutJava, (jlong)hSession);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}




CK_DEFINE_FUNCTION(CK_RV,C_SeedRandom)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSeed, CK_ULONG ulSeedLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SeedRandomJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SeedRandom", "(JLjava/lang/String;J)J");

        if(C_SeedRandomJava !=0)
        {
            jobject string1;
            string1 =(*(environment))->NewStringUTF(environment,(const char *) pSeed);
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SeedRandomJava, (jlong)hSession, string1, (jlong)ulSeedLen);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_SetAttributeValue)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SetAttributeValueJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SetAttributeValue", "(JJ[Lobjects/ATTRIBUTE;J)J");

        if(C_SetAttributeValueJava !=0)
        {
            jobjectArray array;
            if(pTemplate != NULL) {
                jsize size = ulCount;
                int i;
                jobject obj2;
                jclass cls2 = (*(environment))->FindClass(environment, "objects/ATTRIBUTE");



                array = (*(environment))->NewObjectArray(environment,size,cls2, NULL);









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
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SetAttributeValueJava, (jlong)hSession, (jlong)hObject, array, (jlong)ulCount);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_SetPIN)(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pOldPin, CK_ULONG ulOldLen, CK_CHAR_PTR pNewPin, CK_ULONG ulNewLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SetPINJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SetPIN", "(JLjava/lang/String;JLjava/lang/String;J)J");

        if(C_SetPINJava !=0)
        {
            jobject string1;
            jobject string3;
            string1 =(*(environment))->NewStringUTF(environment,(const char *) pOldPin);
            string3 =(*(environment))->NewStringUTF(environment, (const char *)pNewPin);
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SetPINJava, (jlong)hSession, string1, (jlong)ulOldLen, string3, (jlong)ulNewLen);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_Sign)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    jobject _pulSignatureLen = NULL;
    JNIEnv* environment;
    jbyteArray arr;
    jbyteArray arr2;
    jclass longClass;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SignJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Sign", "(J[BJ[BLobj/CK_ULONG_PTR;)J");

        if(pData!=NULL) {
            arr = (*(environment))->NewByteArray(environment, ulDataLen);
            (*(environment))->SetByteArrayRegion(environment, arr, (jsize)0, (jsize)ulDataLen,(jbyte*)pData);
        }


        if(pSignature!=NULL) {
            arr2 = (*(environment))->NewByteArray(environment, *pulSignatureLen);
            (*(environment))->SetByteArrayRegion(environment, arr2, (jsize)0, (jsize)*pulSignatureLen,(jbyte*)pSignature);
        }


        longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        if(pSignature!=NULL) {
            jmethodID constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
            _pulSignatureLen  =(*(environment))->NewObject(environment, longClass, constructorLong, (jlong)*pulSignatureLen);
        }

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignJava, (jlong)hSession, arr, (jlong)ulDataLen, arr2, _pulSignatureLen);
        (*(environment))->ExceptionDescribe(environment);



        if(pSignature!=NULL && retVal==CKR_OK) {

            jbyte* val = (*(environment))->GetByteArrayElements(environment, arr2, NULL);
            jlong len = (*(environment))->GetArrayLength(environment, arr2);
            int i=0;
            for(i; i<len; i++) {
                pSignature[i] = val[i];
            }
        }

        if(_pulSignatureLen !=NULL) {
            jmethodID getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
            *pulSignatureLen = (*(environment))->CallLongMethod(environment, _pulSignatureLen, getValue);

        }



    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_SignInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SignInitJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SignInit", "(JLobj/CK_MECHANISM;J)J");

        jclass mechanismClass = (*(environment))->FindClass(environment, "Lobj/CK_MECHANISM;");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject mechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, (jlong)pMechanism->mechanism, NULL, (jlong)0);



        if(C_SignInitJava !=0)
        {
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignInitJava, (jlong)hSession, mechanism, (jlong)hKey);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_UnwrapKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hUnwrappingKey, CK_BYTE_PTR pWrappedKey, CK_ULONG ulWrappedKeyLen, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    jmethodID getValue;
    jobject _phKey = NULL;
    JNIEnv* environment;
    jclass longClass;
    jmethodID constructorLong;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_UnwrapKeyJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_UnwrapKey", "(JLobj/CK_MECHANISM;J[BJ[Lobj/CK_ATTRIBUTE;JLobj/CK_ULONG_PTR;)J");

        jclass mechanismClass = (*(environment))->FindClass(environment, "obj/CK_MECHANISM");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject _pMechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, pMechanism->mechanism, NULL, (jlong)0);

        jobject array = createAttributeArray(environment, pTemplate, ulAttributeCount);

        jbyteArray _pWrappedKey;
        if(pWrappedKey!=NULL) {
            _pWrappedKey = (*(environment))->NewByteArray(environment, ulWrappedKeyLen);
            (*(environment))->SetByteArrayRegion(environment, _pWrappedKey, 0, ulWrappedKeyLen,(jbyte*)pWrappedKey);
        }

        longClass = (*(environment))->FindClass(environment, "obj/CK_ULONG_PTR");
        constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _phKey  =(*(environment))->NewObject(environment, longClass, constructorLong, (jlong)*phKey);



        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_UnwrapKeyJava,(jlong) hSession, _pMechanism, (jlong)hUnwrappingKey, _pWrappedKey, (jlong)ulWrappedKeyLen, array, (jlong)ulAttributeCount, _phKey);
        (*(environment))->ExceptionDescribe(environment);

        getValue = (*(environment))->GetMethodID(environment, longClass, "getValue", "()J");
        *phKey = (*(environment))->CallLongMethod(environment, _phKey, getValue);



    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_WrapKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hWrappingKey, CK_OBJECT_HANDLE hKey, CK_BYTE_PTR pWrappedKey, CK_ULONG_PTR pulWrappedKeyLen)
{
    long retVal=CKR_GENERAL_ERROR;
    jclass  longClass;
    sing* dings = get_instance();
    jobject _pulWrappedKeyLen = NULL;
    JNIEnv* environment;
    jmethodID constructorLong;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_WrapKeyJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_WrapKey", "(JLobj/CK_MECHANISM;JJ[BLobj/CK_ULONG_PTR;)J");



        jclass mechanismClass = (*(environment))->FindClass(environment, "obj/CK_MECHANISM");
        jmethodID mechanismConstructor = (*(environment))->GetMethodID(environment,mechanismClass, "<init>", "(JLjava/lang/Object;J)V");
        jobject _pMechanism = (*(environment))->NewObject(environment, mechanismClass, mechanismConstructor, (jlong)pMechanism->mechanism, NULL, (jlong)0);


        jbyteArray _pWrappedKey;
        if(pWrappedKey!=NULL) {
            _pWrappedKey = (*(environment))->NewByteArray(environment, (jlong)*pulWrappedKeyLen);
            (*(environment))->SetByteArrayRegion(environment, _pWrappedKey, (jlong)0, (jlong)*pulWrappedKeyLen,(jbyte*)pWrappedKey);
        }


        longClass = (*(environment))->FindClass(environment, "Lobj/CK_ULONG_PTR;");
        constructorLong = (*(environment))->GetMethodID(environment, longClass, "<init>", "(J)V");
        _pulWrappedKeyLen  =(*(environment))->NewObject(environment, longClass, constructorLong, *pulWrappedKeyLen);

        retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_WrapKeyJava, (jlong)hSession, _pMechanism, (jlong)hWrappingKey, (jlong)hKey, _pWrappedKey, _pulWrappedKeyLen);
        (*(environment))->ExceptionDescribe(environment);

        if(pWrappedKey!=NULL) {
            jbyte* bytes=(*(environment))->GetByteArrayElements(environment, _pWrappedKey, NULL);
            int i=0;
            for(i; i<*pulWrappedKeyLen; i++) {
                pWrappedKey[i] = bytes[i];
            }
        }

    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_SignUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
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

            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignUpdateJava, (jlong)hSession, result, (jlong)ulPartLen);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }


    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_SignFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{
    long retVal=CKR_GENERAL_ERROR;
    sing* dings = get_instance();
    jmethodID constructor2;
    jmethodID constructor1;
    jobject obj2;
    jobject obj1;
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_SignFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_SignFinal", "(JLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");

        if(C_SignFinalJava !=0)
        {
            if(pSignature != NULL) {
                jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY");
                constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pSignature, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj1==NULL) {
                } else {
                }
            } else {
                obj1=NULL;
            }
            if(pulSignatureLen != NULL) {
                jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR");
                constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
                obj2=(*(environment))->NewObject(environment, cls2, constructor2, (jlong)pulSignatureLen, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj2==NULL) {
                } else {
                }
            } else {
                obj2=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_SignFinalJava, (jlong)hSession, obj1, obj2);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_DecryptFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pLastPart, CK_ULONG_PTR pulLastPartLen)
{
    long retVal=CKR_GENERAL_ERROR;
    jmethodID constructor2;
    jmethodID constructor1;
    jobject obj2;
    jobject obj1;
    sing* dings = get_instance();
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DecryptFinalJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_DecryptFinal", "(JLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");

        if(C_DecryptFinalJava !=0)
        {
            if(pLastPart != NULL) {
                jclass cls1 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY");
                constructor1 = (*(environment))->GetMethodID(environment, cls1, "<init>", "(JZ)V");
                obj1=(*(environment))->NewObject(environment, cls1, constructor1, pLastPart, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj1==NULL) {
                } else {
                }
            } else {
                obj1=NULL;
            }
            if(pulLastPartLen != NULL) {
                jclass cls2 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR");
                constructor2 = (*(environment))->GetMethodID(environment, cls2, "<init>", "(JZ)V");
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

    return retVal;
}


CK_DEFINE_FUNCTION(CK_RV,C_Decrypt)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedData, CK_ULONG ulEncryptedDataLen, CK_BYTE_PTR pData, CK_ULONG_PTR pulDataLen)
{
    long retVal=CKR_GENERAL_ERROR;
    jobject obj4;
    jobject obj3;
    sing* dings = get_instance();
    jmethodID constructor4;
    jmethodID constructor3;
    JNIEnv* environment;
    if(dings->LockMutex != NULL) {
        dings->LockMutex((dings->ppMutex));
    }
    (*(dings->jvm))->AttachCurrentThread(dings->jvm,(void**)&environment,NULL);
    if(dings->cls !=0)
    {
        jmethodID C_DecryptJava = (*(environment))->GetStaticMethodID(environment, dings->cls,"C_Decrypt", "(J[BJLproxys/CK_BYTE_ARRAY;Lproxys/CK_ULONG_JPTR;)J");

        if(C_DecryptJava !=0)
        {
            jbyteArray result;
            result = (*(environment))->NewByteArray(environment, ulEncryptedDataLen);

            (*(environment))->ExceptionDescribe(environment);
            (*(environment))->SetByteArrayRegion(environment, result, 0, ulEncryptedDataLen,(jbyte*)pEncryptedData);
            if(pData != NULL) {
                jclass cls3 = (*(environment))->FindClass(environment, "proxys/CK_BYTE_ARRAY");
                constructor3 = (*(environment))->GetMethodID(environment, cls3, "<init>", "(JZ)V");
                obj3=(*(environment))->NewObject(environment, cls3, constructor3, pData, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj3==NULL) {
                } else {
                }
            } else {
                obj3=NULL;
            }
            if(pulDataLen != NULL) {
                jclass cls4 = (*(environment))->FindClass(environment, "proxys/CK_ULONG_JPTR");
                constructor4 = (*(environment))->GetMethodID(environment, cls4, "<init>", "(JZ)V");
                obj4=(*(environment))->NewObject(environment, cls4, constructor4, pulDataLen, JNI_FALSE);

                (*(environment))->ExceptionDescribe(environment);
                if(obj4==NULL) {
                } else {
                }
            } else {
                obj4=NULL;
            }
            retVal = (*(environment))->CallStaticLongMethod(environment, dings->cls, C_DecryptJava, (jlong)hSession, result, (jlong)ulEncryptedDataLen, obj3, obj4);
            (*(environment))->ExceptionDescribe(environment);

        }
    }
    if(dings->UnlockMutex != NULL) {
        dings->UnlockMutex(dings->ppMutex);
    }

    return retVal;
}




CK_DEFINE_FUNCTION(CK_RV, C_InitPIN)(CK_SESSION_HANDLE hSession, CK_CHAR_PTR pPin, CK_ULONG ulPinLen)
{
    fprintf(stderr,"not implemented function called: C_InitPIN\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{
    fprintf(stderr,"not implemented function called: C_EncryptUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GetOperationState)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pOperationState, CK_ULONG_PTR pulOperationStateLen)
{
    fprintf(stderr,"not implemented function called: C_GetOperationState\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pLastEncryptedPart, CK_ULONG_PTR pulLastEncryptedPartLen)
{
    fprintf(stderr,"not implemented function called: C_EncryptFinal\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism)
{
    fprintf(stderr,"not implemented function called: C_DigestInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_Digest)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pDigest, CK_ULONG_PTR pulDigestLen)
{
    fprintf(stderr,"not implemented function called: C_Digest\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{
    fprintf(stderr,"not implemented function called: C_DigestUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestKey)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hKey)
{
    fprintf(stderr,"not implemented function called: C_DigestKey\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pDigest, CK_ULONG_PTR pulDigestLen)
{
    fprintf(stderr,"not implemented function called: C_DigestFinal\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SignRecoverInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    fprintf(stderr,"not implemented function called: C_SignRecoverInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SignRecover)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG_PTR pulSignatureLen)
{
    fprintf(stderr,"not implemented function called: C_SignRecover\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    fprintf(stderr,"not implemented function called: C_VerifyInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_Verify)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen)
{
    fprintf(stderr,"not implemented function called: C_Verify\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen)
{
    fprintf(stderr,"not implemented function called: C_VerifyUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyFinal)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen)
{
    fprintf(stderr,"not implemented function called: C_VerifyFinal\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyRecoverInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    fprintf(stderr,"not implemented function called: C_VerifyRecoverInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_VerifyRecover)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pSignature, CK_ULONG ulSignatureLen, CK_BYTE_PTR pData, CK_ULONG_PTR pulDataLen)
{
    fprintf(stderr,"not implemented function called: C_VerifyRecover\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DigestEncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{
    fprintf(stderr,"not implemented function called: C_DigestEncryptUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DecryptDigestUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{
    fprintf(stderr,"not implemented function called: C_DecryptDigestUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SignEncryptUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pPart, CK_ULONG ulPartLen, CK_BYTE_PTR pEncryptedPart, CK_ULONG_PTR pulEncryptedPartLen)
{
    fprintf(stderr,"not implemented function called: C_SignEncryptUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DecryptVerifyUpdate)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pEncryptedPart, CK_ULONG ulEncryptedPartLen, CK_BYTE_PTR pPart, CK_ULONG_PTR pulPartLen)
{
    fprintf(stderr,"not implemented function called: C_DecryptVerifyUpdate\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GenerateKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phKey)
{
    fprintf(stderr,"not implemented function called: C_GenerateKey\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GenerateKeyPair)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_ATTRIBUTE_PTR pPublicKeyTemplate, CK_ULONG ulPublicKeyAttributeCount, CK_ATTRIBUTE_PTR pPrivateKeyTemplate, CK_ULONG ulPrivateKeyAttributeCount, CK_OBJECT_HANDLE_PTR phPublicKey, CK_OBJECT_HANDLE_PTR phPrivateKey)
{
    fprintf(stderr,"not implemented function called: C_GenerateKeyPair\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_DeriveKey)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hBaseKey, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulAttributeCount, CK_OBJECT_HANDLE_PTR phKey)
{
    fprintf(stderr,"not implemented function called: C_DeriveKey\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_SetOperationState)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pOperationState, CK_ULONG ulOperationStateLen, CK_OBJECT_HANDLE hEncryptionKey, CK_OBJECT_HANDLE hAuthenticationKey)
{
    fprintf(stderr,"not implemented function called: C_SetOperationState\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_CopyObject)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ATTRIBUTE_PTR pTemplate, CK_ULONG ulCount, CK_OBJECT_HANDLE_PTR phNewObject)
{
    fprintf(stderr,"not implemented function called: C_CopyObject\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GetObjectSize)(CK_SESSION_HANDLE hSession, CK_OBJECT_HANDLE hObject, CK_ULONG_PTR pulSize)
{
    fprintf(stderr,"not implemented function called: C_GetObjectSize\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_GetFunctionStatus)(CK_SESSION_HANDLE hSession)
{
    fprintf(stderr,"not implemented function called: C_GetFunctionStatus\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_CancelFunction)(CK_SESSION_HANDLE hSession)
{
    fprintf(stderr,"not implemented function called: C_CancelFunction\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_InitToken)(CK_SLOT_ID slotID, CK_CHAR_PTR pPin, CK_ULONG ulPinLen, CK_CHAR_PTR pLabel)
{
    fprintf(stderr,"not implemented function called: C_InitToken\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_EncryptInit)(CK_SESSION_HANDLE hSession, CK_MECHANISM_PTR pMechanism, CK_OBJECT_HANDLE hKey)
{
    fprintf(stderr,"not implemented function called: C_EncryptInit\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_Encrypt)(CK_SESSION_HANDLE hSession, CK_BYTE_PTR pData, CK_ULONG ulDataLen, CK_BYTE_PTR pEncryptedData, CK_ULONG_PTR pulEncryptedDataLen)
{
    fprintf(stderr,"not implemented function called: C_Encrypt\n");
    return CKR_OK;
}
CK_DEFINE_FUNCTION(CK_RV, C_WaitForSlotEvent)(CK_FLAGS flags, CK_SLOT_ID_PTR pSlot, CK_VOID_PTR pReserved) {
    fprintf(stderr,"not implemented function called: C_WaitForSlotEvent\n");
    return 0x00000054;
}
