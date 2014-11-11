#include <jni.h>
#include <stdlib.h>
#include<string.h>
#include"Pkcs11Config.h"

#ifndef __JVM_H
#define __JVM_H

#ifndef SYKTRUSTJAR
#ifndef WIN32
#define SYKTRUSTJAR "/home/faxxe/skytrust-pkcs11/lib/PKCS11.jar"
#else
#define SYKTRUSTJAR "C:\pthread"
#endif
#endif





#ifdef DEBUG
#define NUMJAVAOPTIONS 2
#else
#define NUMJAVAOPTIONS 1
#endif

typedef struct jvm_singleton
{
    JavaVMOption options[NUMJAVAOPTIONS];
    JNIEnv *env;
    JavaVM *jvm;
    JavaVMInitArgs vm_args;
    long status;
    jclass cls;
    jmethodID mid;
    jint square;
    jboolean not;
    CK_CREATEMUTEX CreateMutex;
    CK_DESTROYMUTEX DestroyMutex;
    CK_LOCKMUTEX LockMutex;
    CK_UNLOCKMUTEX UnlockMutex;
    CK_VOID_PTR ppMutex;
#ifndef WIN32
    pthread_t thread;
    pthread_cond_t finish;
    pthread_mutex_t dummymutex;
#else
    HANDLE thread;
    HANDLE thread_wait;
    HANDLE thread_wait_instance;

#endif

} sing;

#ifndef WIN32
void get_instance_thread();
#else
DWORD WINAPI get_instance_thread(void *data);
#endif


void destroyVM();
static struct jvm_singleton* instance = NULL;

sing* get_instance()
{
	printf("getinstancethread...");

    if (instance == NULL)
    {
        instance =(sing*) malloc(sizeof(sing));

#ifndef WIN32
        if(pthread_create(&(instance->thread), NULL, (void * (*)(void *))get_instance_thread, NULL)) {
#else
        //instance->thread = CreateThread(NULL,0,get_instance_thread,NULL, 0, NULL);
        //if(instance->thread!=NULL){
        if(1) {
            get_instance_thread(NULL);
#endif


        } else {
            /*wait here*/
#ifndef WIN32
	sleep(5);
#else
            //WaitForSingleObject(instance->thread_wait_instance, INFINITE);
#endif
        }

    }
    return instance;
}

void destroyVM() {
    if(instance->DestroyMutex != NULL) {
        instance->DestroyMutex(instance->ppMutex);
    }
    (*(instance->jvm))->DetachCurrentThread(instance->jvm);
    (*(instance->jvm))->DestroyJavaVM(instance->jvm);
    free(instance);
    instance = NULL;
}

#ifndef WIN32
void get_instance_thread() {
#else
DWORD WINAPI get_instance_thread(void* data) {
#endif
    /*	typedef jint (WINAPI* JNI_CREATEJAVAVM)(JavaVM **pvm, void ** penv, void *args); */
    long status=77;
    /*	HMODULE hLib = LoadLibrary("C:\\Program Files (x86)\\Java\\jdk1.8.0_25\\jre\\bin\\server\\jvm.dll"); */
    /*	HMODULE hLib = LoadLibrary("C:\\Program Files (x86)\\Java\\jre1.8.0_25\\bin\\client\\jvm.dll"); */
    /*	JNI_CREATEJAVAVM JNI_CreateJavaVM = NULL; */
    instance->options[0].optionString = "-Djava.class.path="SYKTRUSTJAR;
    instance->options[1].optionString = "-Xcheck:jni";
    instance->options[2].optionString = "-verbose:class";


#ifdef DEBUG
    instance->options[2].optionString = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:8000";
#endif

    JNI_GetDefaultJavaVMInitArgs(&(instance->vm_args));

    instance->vm_args.version = JNI_VERSION_1_6;
    instance->vm_args.nOptions = NUMJAVAOPTIONS;
    instance->vm_args.ignoreUnrecognized = JNI_FALSE;
    instance->status=66;
    instance->env = NULL;
    instance->jvm=NULL;
    instance->vm_args.options = instance->options;



    /*	JNI_CreateJavaVM = (JNI_CREATEJAVAVM) GetProcAddress (hLib, "JNI_CreateJavaVM"); */
    instance->status = JNI_CreateJavaVM(&(instance->jvm), (void**)&(instance->env), &(instance->vm_args));




    (*(instance->jvm))->AttachCurrentThread((instance->jvm),(void**)&(instance->env),NULL);

    if(instance->status==JNI_ERR) {
        free(instance);
        instance = NULL;
        return;
    }
    instance->cls = (*(instance->env))->FindClass(instance->env,"pkcs11/JAVApkcs11Interface");

    if ((*(instance->env))->ExceptionCheck(instance->env)) {
        (*(instance->env))->ExceptionDescribe(instance->env);
        return;
    }
    if(instance->cls ==0) {

        return;

    } else {

    }

#ifndef WIN32
    pthread_mutex_init(&(instance->dummymutex), NULL);
    pthread_cond_init (&(instance->finish), NULL);

    pthread_cond_wait(&(instance->finish),&(instance->dummymutex));
#else
    //SetEvent(instance->thread_wait_instance);
    // WaitForSingleObject(instance->thread_wait, INFINITE);

#endif

    /*	destroyVM(); */

}

#ifdef _WIN32
#define PATH_SEPARATOR ';'
#else
#define PATH_SEPARATOR ':'
#endif



#endif
