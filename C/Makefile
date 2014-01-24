CC = gcc
WRAPSOURCES = pkcs11_java_wrap.c
SOURCES = pkcs11.c

    
INCLUDE_DIRS := /usr/lib/jvm/java-7-openjdk-amd64/include/
LIBRARY_DIRS := /usr/lib/jvm/java-7-openjdk-amd64/jre/lib/amd64/server
LIBRARIES := jvm

CFLAGS = -Wall -shared -fPIC -g 
BINDIR = build
SRCDIR = src
NAME = skytrustpkcs11
WRAPNAME = $(NAME)_wrapper

CFLAGS += $(foreach includedir,$(INCLUDE_DIRS),-I$(includedir))
LDFLAGS += $(foreach librarydir,$(LIBRARY_DIRS),-L$(librarydir))
LDFLAGS += $(foreach library,$(LIBRARIES),-l$(library))

pkcs11: $(SRCDIR)/$(SOURCES) pkcs11wrapper
				$(CC) $(CFLAGS) $(SRCDIR)/$(SOURCES) -o $(BINDIR)/lib$(NAME).so $(LDFLAGS)
        
pkcs11wrapper: $(SRCDIR)/$(WRAPSOURCES)
				$(CC) $(CFLAGS) $(SRCDIR)/$(WRAPSOURCES) -o $(BINDIR)/lib$(WRAPNAME).so $(LDFLAGS)

all: pkcs11


