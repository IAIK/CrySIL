pkcs11_private
==============

dependencies:
swig2.0


to build shared object: 
ant init
ant build

some other targets
swig_preproc: processes pkcs11t.h for swig
swig: generates Proxy classes and jni interface
JAVA-compile: 
compile: JAVA-compile, C-compile
clean: JAVA-clean, C-clean, SWIG-clean
C-compile: führt make im C ordner aus; übergibt Variablen JAR (pfad zum .jar) und DEBUG (ob VM im debug mode anstarten)

if debug in build.xml is true the JVM waits for remote debugger ant startup

Makefiles:
C/Makefile
	targets for swig wrapper lib and pkcs11 lib
	calls test/Makefile for test targets
	commandline Variable:
		 DEBUG=true/false (default false) 
		 JAR=path_to_JAR (default ../lib/PKCS11.jar)
C/test/Makefile

