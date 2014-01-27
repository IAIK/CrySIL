#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#ifndef __JVM_H
#define __JVM_H




//struct jvm_singleton;


typedef struct jvm_singleton
{
	JavaVMOption options[2];
	JNIEnv *env;
	JavaVM *jvm;
	JavaVMInitArgs vm_args;
	long status;
	jclass cls;
	jmethodID mid;
	jint square;
	jboolean not;
}sing;

sing* get_instance()
{
    static struct jvm_singleton* instance = NULL;
    //char my_cwd[1024];
    //getcwd(my_cwd, 1024);
    //printf("\nPath: %s\n", my_cwd);
    printf("in get_instance()\n");
    if (instance == NULL)
    {
	printf("\ncreating new singleton... starting up vm\n");

	instance =(sing*) malloc(sizeof(sing));
	if(instance==NULL){
		printf("\n malloc jvm.h failed \n");
		//out of memory --> die gracefully
	}
	instance->options[0].optionString = "-Djava.class.path=../../lib/PKCS11.jar";

//	memset(&(instance->vm_args), 0, sizeof(instance->vm_args));
	JNI_GetDefaultJavaVMInitArgs(&(instance->vm_args));
	instance->vm_args.version = JNI_VERSION_1_4;
	instance->vm_args.nOptions = 1;
	instance->vm_args.options = instance->options;
	instance->status = JNI_CreateJavaVM(&instance->jvm, (void**)&(instance->env), &(instance->vm_args));
	if(instance->status==JNI_ERR){
		//VM - Error --> cleanup & die gracefully 
		printf("jvm startup error");
	}
	instance->cls = (*(instance->env))->FindClass(instance->env,"JAVApkcs11Interface");
	if(instance->cls ==0){
		char* buf = malloc(99);

		buf= getcwd(buf, 99);
		printf("Class not found!....%s",buf);
		return NULL;

	}else{
		char* buf = malloc(99);

		buf= getcwd(buf, 99);

		printf("Class found... hooorrrrraaaaaayyyyy! %s   ",buf);
	}


    }

    printf("returning instance....\n");
    return instance;
}

void destroyVM(){
printf("destroyVM called....\n");
sing* dings = get_instance();
(*(dings->jvm))->DestroyJavaVM(dings->jvm);
}

#ifdef _WIN32
#define PATH_SEPARATOR ';'
#else
#define PATH_SEPARATOR ':'
#endif



#endif
