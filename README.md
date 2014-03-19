skytrust-pkcs11
===============

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
C-compile: executes make in C directory; übergibt Variablen JAR (pfad zum .jar) und DEBUG (ob VM im debug mode anstarten)

if debug in build.xml is true the JVM waits for remote debugger ant startup

execute export LD_LIBRARY_PATH=path to libjvm.so
install libpkcs11_java_wrap.so into /usr/lib/
install libskytrustpkcs11.so into /usr/lib/


# INSTALLATION:



# DESIGN:
each server that is configured in the GUI is represented by a Slot
It is planned that the GUI is its own process that gets called via RMI or something similar.

## Flow Diagrams:

on first use
JAVAInterface			ResourceManager				    		GUI
    |---getInstance----->|											|
    |    								 |--getServerInfoList-->|
		|										 |											|
		|										 |<--list of Servers----|	
		|							addToSlotList
		|<---- instance------|

on Sign command:
JAVAInterface			ResourceManager				Session			Slot			IToken			   Skytrust     	 GUI
    |   getInstance     |                 |           |         |								 |           |
    |------------------>|                 |           |         |								 |           |
		|getSessionForHandle|                 |           |         |                |           |
		|------------------>|                 |           |         |								 |           |						
		|	<-----------------|                 |           |         |								 |           |
		|									  |                 |           |         |								 |           |
		|			    	Sign    |                 |           |         |								 |           |			
		|	----------------------------------->|           |         |								 |           |
		|										|									|-getSlot-->|         |								 |           |
		|										|									|	---------getToken-->|								 |           |
		|										|									|						|					|	   doSign  	   |           |
		|										|									|						|  				|--------------->|           |
		|										|									|						|					|		authRequest  |           |
		|										|									|						|					|<---------------|           |
		|										|									|						|		      |    askForCredentials       |
		|										|									|						|					|--------------------------->|
		|										|									|						|				  |<---------------------------|
		|										|									|						|					|	authResponse   |					 |
		|										|									|						|					|--------------->|					 |
		|										|									|						|					|  Sign Response |					 |
		|										|									|      Signature			|<---------------|					 |
		|										|									|<--------------------|								 |					 |




## Class Diagram:


                  +-------------+         +-------------+       +---------+
                  |objectManager|         |PKCS11Object |       |ATTRIBUTE|
                  |-------------|1       n|-------------|1     n|---------|
                  |create       +--------->             +------->         |
                  |getById      |         |             |       |         |
                  +-------------+         +-------------+       +---------+
 +--------------+    1^
 |  MECAHNISM   |     |
 +--------------+     |
              ^n      |                  +-------------+
              |       |                  |    Token    |
              |1     1|                  |-------------|
            +-+-------+--+1             1|             |
            |    Slot    +--------------->             |         +---------+
            |------------|               +-------------+         |   GUI   |
            |getTokenInfo|                                       |---------|
            |getMechanism|                                       |         |
            |newSession  |                                       |         |
            |delSession  |1           n  +-------------+         +---------+
            +-----^------+--------------->   Session   |
                  |n                     |-------------|
                  |                      |sign         |
                  |                      |decrypt      |
                  |                      |findObj      |
                  |1                     |....         |
            +-----+-----+                +-------------+
            |     RM    |
            |-----------|
            |           |
            +-----------+






