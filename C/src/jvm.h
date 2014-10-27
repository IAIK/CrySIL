#include <jni.h>
#include <stdlib.h>
#include "pkcs11.h"
#include<string.h>
#include"Pkcs11Config.h"

#ifndef __JVM_H
#define __JVM_H

#ifndef SYKTRUSTJAR
#define SYKTRUSTJAR "/home/faxxe/skytrust-pkcs11/lib/PKCS11.jar"
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
    pthread_t thread;
    pthread_cond_t finish;
    pthread_mutex_t dummymutex;
} sing;

void get_instance_thread();
void destroyVM();
static struct jvm_singleton* instance = NULL;
sing* get_instance()
{
    if (instance == NULL)
    {
	instance =(sing*) malloc(sizeof(sing));
	if(pthread_create(&(instance->thread), NULL, (void * (*)(void *))get_instance_thread, NULL)){
		
	}else{
		/*wait here*/	
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

void get_instance_thread(){
	
        instance->options[0].optionString = "-Djava.class.path="SYKTRUSTJAR;

#ifdef DEBUG
        instance->options[2].optionString = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:8000";
#endif

        instance->vm_args.version = JNI_VERSION_1_4;
        instance->vm_args.nOptions = NUMJAVAOPTIONS;
        instance->vm_args.ignoreUnrecognized = JNI_FALSE;
        JNI_GetDefaultJavaVMInitArgs(&(instance->vm_args));
        instance->vm_args.options = instance->options;
        instance->status = JNI_CreateJavaVM(&instance->jvm, (void**)&(instance->env), &(instance->vm_args));
	
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

	pthread_mutex_init(&(instance->dummymutex), NULL);
	pthread_cond_init (&(instance->finish), NULL);
	
	pthread_cond_wait(&(instance->finish),&(instance->dummymutex));
	destroyVM();

}

#ifdef _WIN32
#define PATH_SEPARATOR ';'
#else
#define PATH_SEPARATOR ':'
#endif



#endif
