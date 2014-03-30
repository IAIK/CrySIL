skytrust-pkcs11
===============

# INSTALLATION:

Dependencies:
 - swig2.0
 - java
 ....

set environment variable JAVA_HOME so cmake can find JNI

to build shared librarys: 
 ant init
 ant build

Targets:
 - SWIG-preprocess: processes pkcs11t.h for swig; executes sed scripts in ./swig/
 - SWIG: generates Proxy classes and jni interface
 - cmake: initializes C part of project, runs cmake with arguments: JAR (path to .jar of JAVA part)  
                                                                     DEBUG (true/false if the VM should start in Debug mode and wait for Remote Debugger at localhost:8000)
 - JAVA-compile: 
 - jar: build jar file; uses pre generated dependencies.jar with all dependencies for the skytrust element 
 - C-compile: executes cmake generated Makefile; install libraries into ./lib/
 - compile: JAVA-compile, C-compile
 - clean: JAVA-clean, C-clean, SWIG-clean



execute export LD_LIBRARY_PATH=path to libjvm.so
install libpkcs11_java_wrap.so into /usr/lib/
install libskytrustpkcs11.so into /usr/lib/

# DESIGN:
The JAVA component can be divided into two strong connected parts. 
The first part, seen from the C component, is the PKCS11 part. 
It implements the Slot,Session,Object management behavior as described in the pkcs11 specification.
The second part is the Skytrust part. It implements the communication with the skytrust server.
These two parts are connected through the IToken interface. The objects used to pass data 
through this interface are PKCSObject and MECHANISM.

### IToken: 
  defines the crypto methods encrypt, decrypt, sign, verify and the method getObejcts
  getObejcts gets called when the first connection to a Skytrust server is made.
  It should return a list of PKCS11Objects representing all available crypto objects (public key, private key,certificate..)
  Each of the crypto methods has a PKCS11Object as key and a MECHANISM as parameters.
  
### PKCSObject:
  Is the main object used for storing and managing crypto entitys.
  Each PKCS11Object owns a set of ATTRIBUTE objects which define its properties.

### MECHANISM:
  
The PKCS11 part consists mainly of the following classes: 
### JAVApkcs11Interface,
### ResourceManager(RM),
### Slot,
### Session,
### ObjectManager and ObjectBuilder



The Skytrust part consists of the classes 
### Token 
  (implements IToken)
### PKCS11SkyTrustMapper. 



### Slot:
each server that is configured in the (not yet existing) GUI is represented by a Slot
It is planned that the GUI is its own process that gets called via RMI or something similar.
When a Slot gets created it asks for the key list of the its skytrust server through the IToken interface.

session handle encodes session and Slot id
## Flow Diagrams:

on first use
JAVAInterface      ResourceManager                GUI
    |---getInstance----->|                      |
    |                    |--getServerInfoList-->|
    |                    |                      |
    |                    |<--list of Servers----|  
    |              addToSlotList
    |<---- instance------|

on Sign command:
JAVAInterface      ResourceManager        Session      Slot      IToken         Skytrust        GUI
    |   getInstance     |                 |           |         |                 |           |
    |------------------>|                 |           |         |                 |           |
    |getSessionForHandle|                 |           |         |                 |           |
    |------------------>|                 |           |         |                 |           |            
    | <-----------------|                 |           |         |                 |           |
    |                   |                 |           |         |                 |           |
    |           Sign    |                 |           |         |                 |           |      
    | ----------------------------------->|           |         |                 |           |
    |                   |                 |-getSlot-->|         |                 |           |
    |                   |                 |---------getToken--->|                 |           |
    |                   |                 |           |         |     doSign      |           |
    |                   |                 |           |         |---------------->|           |
    |                   |                 |           |         |    authRequest  |           |
    |                   |                 |           |         |<----------------|           |
    |                   |                 |           |         |     askForCredentials       |
    |                   |                 |           |         |---------------------------->|
    |                   |                 |           |         |<----------------------------|
    |                   |                 |           |         |  authResponse   |           |
    |                   |                 |           |         |---------------->|           |
    |                   |                 |           |         |   Sign Response |           |
    |                   |                 |      Signature      |<----------------|           |
    |                   |                 |<--------------------|                 |           |




## Class Diagram:

<pre>
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
</pre>




