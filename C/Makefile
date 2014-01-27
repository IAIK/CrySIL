CC = gcc
WRAPSOURCES = pkcs11_java_wrap.c
SOURCES = pkcs11.c
INCLUDE_DIRS := ../include/java/
LIBRARY_DIRS := ../lib/
LIBRARIES := jvm

CFLAGS = -Wall 
BINDIR = build
SRCDIR = src
SHAREDFLAGS = -shared -fPIC -g 

NAME = skytrustpkcs11
WRAPNAME = pkcs11_java_wrap

CFLAGS += $(foreach includedir,$(INCLUDE_DIRS),-I$(includedir))
LDFLAGS += $(foreach librarydir,$(LIBRARY_DIRS),-L$(librarydir))
LDFLAGS += $(foreach library,$(LIBRARIES),-l$(library))

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

