#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "pkcs11.h"
#include <pthread.h>

#ifndef __JVM_H
#define __JVM_H

#ifndef SYKTRUSTJAR
#define SYKTRUSTJAR "/home/faxxe/skytrust-pkcs11/lib/PKCS11.jar"
#endif


//struct jvm_singleton;

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
    //char my_cwd[1024];
    //getcwd(my_cwd, 1024);
    //printf("\nPath: %s\n", my_cwd);
    if (instance == NULL)
    {
	instance =(sing*) malloc(sizeof(sing));
	if(pthread_create(&(instance->thread), NULL, get_instance_thread, NULL)){
		fprintf(stderr, "Error creating thread\n");
	}else{
		sleep(1);
	}
	
    }
    //(*(instance->jvm))->AttachCurrentThread(instance->jvm,(void**)&(instance->env),NULL);
    return instance;
}

void destroyVM() {
    printf("destroyVM called....\n");
    if(instance->DestroyMutex != NULL) {
        instance->DestroyMutex(instance->ppMutex);
    }
    printf("before destroying jvm....\n");
    (*(instance->jvm))->DetachCurrentThread(instance->jvm);
    (*(instance->jvm))->DestroyJavaVM(instance->jvm);
    printf("jvm destoryed....\n");
    free(instance);
    instance = NULL;
}

void get_instance_thread(){
	
        printf("\ncreating new singleton... starting up vm\n");
        printf("-Djava.class.path="SYKTRUSTJAR" \n");
        if(instance==NULL) {
            printf("\n malloc jvm.h failed \n");
            //out of memory --> die gracefully
        }
        instance->options[0].optionString = "-Djava.class.path="SYKTRUSTJAR;
        printf("%s \n", instance->options[0].optionString);

#ifdef DEBUG
        instance->options[2].optionString = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:8000";
#endif

        instance->vm_args.version = JNI_VERSION_1_4;
        instance->vm_args.nOptions = NUMJAVAOPTIONS;
        instance->vm_args.ignoreUnrecognized = JNI_FALSE;
        JNI_GetDefaultJavaVMInitArgs(&(instance->vm_args));
        instance->vm_args.options = instance->options;
        printf("\npre create vm\n");
	if(instance->vm_args.options==NULL){
	printf("as guessed, instance is NULL");
	}
        instance->status = JNI_CreateJavaVM(&instance->jvm, (void**)&(instance->env), &(instance->vm_args));
	printf("\nafter pre create vm \n");
	
	(*(instance->jvm))->AttachCurrentThread((instance->jvm),(void**)&(instance->env),NULL);
        if(instance->status==JNI_ERR) {
            //VM - Error --> cleanup & die gracefully
            //printf("jvm startup error %d",instance->status);
            free(instance);
            instance = NULL;
            return;
        }
        instance->cls = (*(instance->env))->FindClass(instance->env,"pkcs11/JAVApkcs11Interface");
        if ((*(instance->env))->ExceptionCheck(instance->env)) {

            printf("\n\n exception..... \n\n");
            (*(instance->env))->ExceptionDescribe(instance->env);
            return;
        }
        if(instance->cls ==0) {
            char* buf = malloc(99);

            buf= getcwd(buf, 99);
            printf("Class not found!....%s\n",buf);

            return;

        } else {
            char* buf = malloc(99);
	    
            buf= getcwd(buf, 99);

            printf("Class found... hooorrrrraaaaaayyyyy! %s   ",buf);
        }

	printf("thats odd\n");
	pthread_mutex_init(&(instance->dummymutex), NULL);
	pthread_cond_init (&(instance->finish), NULL);
	
	pthread_cond_wait(&(instance->finish),&(instance->dummymutex));
	//for(;instance->finish==0;){
		//pthread_yield();
	//}
	destroyVM();

}

#ifdef _WIN32
#define PATH_SEPARATOR ';'
#else
#define PATH_SEPARATOR ':'
#endif



#endif
