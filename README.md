pkcs11_private
==============


to build shared object: 

g++ pkcs11.c -o pkcs11.so -shared -fPIC -g

build.xml
swig_preproc: processes pkcs11t.h for swig
swig: generates Proxy classes and jni interface
JAVA-compile: 
compile: JAVA-compile, C-compile
clean: JAVA-clean, C-clean, SWIG-clean
