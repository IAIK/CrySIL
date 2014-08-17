#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "pkcs11.h"

#ifndef __JVM_H
#define __JVM_H

#ifndef SYKTRUSTJAR
#define SYKTRUSTJAR "/no path defined in gcc commandline/"
#endif

#ifndef SWIGLIBPATH
#define SWIGLIBPATH "/no libpath defined in gcc commandline/"
#endif

//struct jvm_singleton;

#ifdef DEBUG
#define NUMJAVAOPTIONS 3
#else
#define NUMJAVAOPTIONS 2
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
} sing;

static struct jvm_singleton* instance = NULL;

sing* get_instance()
{
    //char my_cwd[1024];
    //getcwd(my_cwd, 1024);
    //printf("\nPath: %s\n", my_cwd);
    if (instance == NULL)
    {
        printf("\ncreating new singleton... starting up vm\n");
        printf("-Djava.class.path="SYKTRUSTJAR" \n");
        instance =(sing*) malloc(sizeof(sing));
        if(instance==NULL) {
            printf("\n malloc jvm.h failed \n");
            //out of memory --> die gracefully
        }
        instance->options[0].optionString = "-Djava.class.path="SYKTRUSTJAR;
        instance->options[1].optionString = "-Djava.library.path="SWIGLIBPATH;  /* set native library path */

#ifdef DEBUG
        instance->options[2].optionString = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:8000";
#endif

        instance->vm_args.version = JNI_VERSION_1_4;
        instance->vm_args.nOptions = NUMJAVAOPTIONS;
        instance->vm_args.ignoreUnrecognized = JNI_FALSE;
        JNI_GetDefaultJavaVMInitArgs(&(instance->vm_args));
        instance->vm_args.options = instance->options;
        printf("\npre create vm\n");
        instance->status = JNI_CreateJavaVM(&instance->jvm, (void**)&(instance->env), &(instance->vm_args));
        if(instance->status==JNI_ERR) {
            //VM - Error --> cleanup & die gracefully
            printf("jvm startup error %d",instance->status);
            free(instance);
            instance = NULL;
            return NULL;
        }
        instance->cls = (*(instance->env))->FindClass(instance->env,"pkcs11/JAVApkcs11Interface");
        if ((*(instance->env))->ExceptionCheck(instance->env)) {

            printf("\n\n exception..... \n\n");
            (*(instance->env))->ExceptionDescribe(instance->env);
            return NULL;
        }
        if(instance->cls ==0) {
            char* buf = malloc(99);

            buf= getcwd(buf, 99);
            printf("Class not found!....%s\n",buf);

            return NULL;

        } else {
            char* buf = malloc(99);

            buf= getcwd(buf, 99);

            printf("Class found... hooorrrrraaaaaayyyyy! %s   ",buf);
        }


    }
    //(*(instance->jvm))->AttachCurrentThread(instance->jvm,(void**)&(instance->env),NULL);
    return instance;
}

void destroyVM() {
    printf("destroyVM called....\n");
    sing* dings = get_instance();
    if(dings->DestroyMutex != NULL) {
        dings->DestroyMutex(dings->ppMutex);
    }
    (*(dings->jvm))->DestroyJavaVM(dings->jvm);
    free(instance);
    instance = NULL;
}

#ifdef _WIN32
#define PATH_SEPARATOR ';'
#else
#define PATH_SEPARATOR ':'
#endif



#endif
