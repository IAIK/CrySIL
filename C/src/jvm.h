#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

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
#define NUMJAVAOPTIONS 4
#else
#define NUMJAVAOPTIONS 3
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
	printf("-Djava.class.path="SYKTRUSTJAR" \n");
	instance =(sing*) malloc(sizeof(sing));
	if(instance==NULL){
		printf("\n malloc jvm.h failed \n");
		//out of memory --> die gracefully
	}
	instance->options[0].optionString = "-Djava.class.path="SYKTRUSTJAR;

	instance->vm_args.version = JNI_VERSION_1_4;
	JNI_GetDefaultJavaVMInitArgs(&(instance->vm_args));
	instance->vm_args.nOptions = NUMJAVAOPTIONS;
	instance->vm_args.ignoreUnrecognized = JNI_FALSE;
	instance->vm_args.options = instance->options;
	instance->status = JNI_CreateJavaVM(&instance->jvm, (void**)&(instance->env), &(instance->vm_args));
	if(instance->status==JNI_ERR){
		//VM - Error --> cleanup & die gracefully 
		printf("jvm startup error");
	}
	instance->cls = (*(instance->env))->FindClass(instance->env,"JAVApkcs11Interface");
	if ((*(instance->env))->ExceptionCheck(instance->env)) {

	  printf("\n\n exception..... \n\n");
	(*(instance->env))->ExceptionDescribe(instance->env);
	  return NULL;
	}
	if(instance->cls ==0){
		printf("Class not found!....\n");

		return NULL;

	}else{


		printf("Class found... hooorrrrraaaaaayyyyy!    ");
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
