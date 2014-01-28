CC = gcc
WRAPSOURCES = pkcs11_java_wrap.c
SOURCES = pkcs11.c
LIBRARIES := jvm

CFLAGS = -Wall 
SRCDIR = src
SHAREDFLAGS = -shared -fPIC -g 

#following variables get overwritten through commandline from ant
BINDIR = build
INCLUDE_DIRS := ../include/java/ ../include/
LIBRARY_DIRS := ../lib/
NAME = skytrustpkcs11
WRAPNAME = pkcs11_java_wrap
JAR = /home/cloud/uni/ACN/pkcs11_private/lib/PKCS11.jar
#end
CFLAGS += $(foreach includedir,$(INCLUDE_DIRS),-I$(includedir))
LDFLAGS += $(foreach librarydir,$(LIBRARY_DIRS),-L$(librarydir))
LDFLAGS += $(foreach library,$(LIBRARIES),-l$(library))

CFLAGS += -DSYKTRUSTJAR=\"$(JAR)\"

pkcs11: $(SRCDIR)/$(SOURCES) pkcs11wrapper
				$(CC) $(CFLAGS) $(SHAREDFLAGS) $(SRCDIR)/$(SOURCES) -o $(BINDIR)/lib$(NAME).so $(LDFLAGS)
        
pkcs11wrapper: $(SRCDIR)/$(WRAPSOURCES)
				$(CC) $(CFLAGS) $(SHAREDFLAGS) $(SRCDIR)/$(WRAPSOURCES) -o $(BINDIR)/lib$(WRAPNAME).so $(LDFLAGS)

all: pkcs11

clean: 
	rm -f $(BINDIR)/lib$(NAME).so
	rm -f $(BINDIR)/lib$(WRAPNAME).so


test: pkcs11
		$(MAKE) -C ./test/ test

testclean:
	$(MAKE) -C ./test/ clean
	
runtest: test
	$(MAKE) -C ./test/ run

