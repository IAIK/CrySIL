pkcs11_private
==============

dependencies:
swig2.0

set env var JAVA_HOME so cmake can find JNI

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

execute export LD_LIBRARY_PATH=path to libjvm.so
install libpkcs11_java_wrap.so into /usr/lib/
install libskytrustpkcs11.so into /usr/lib/


