CC = gcc
WRAPSOURCES = pkcs11_java_wrap.c
SOURCES = pkcs11.c
LIBRARIES := jvm

CFLAGS = -Wall
SRCDIR = src
SHAREDFLAGS = -shared -fPIC
BINDIR = build
export INCLUDE_DIRS := $(realpath ../include/java/ ../include/)
export LIBRARY_DIRS := $(realpath ../lib/)
NAME = skytrustpkcs11
WRAPNAME = pkcs11_java_wrap
export $(DEBUG)
#following variables get overwritten through commandline from ant
JAR = $(realpath ../lib/PKCS11.jar)
DEBUG = true
#end

CFLAGS += $(foreach includedir,$(INCLUDE_DIRS),-I$(includedir))
LDFLAGS += $(foreach librarydir,$(LIBRARY_DIRS),-L$(librarydir))
LDFLAGS += $(foreach library,$(LIBRARIES),-l$(library))

CFLAGS += -DSYKTRUSTJAR=\"$(JAR)\"
CFLAGS += -DSWIGLIBPATH=\"$(LIBRARY_DIRS)\"

ifeq ($(DEBUG),true)
CFLAGS += -g -DDEBUG
endif

pkcs11: $(SRCDIR)/$(SOURCES) pkcs11wrapper
				$(CC) $(CFLAGS) $(SHAREDFLAGS) $(SRCDIR)/$(SOURCES) -o $(BINDIR)/lib$(NAME).so $(LDFLAGS)
				ln -s -f $(realpath $(BINDIR)/lib$(NAME).so) $(LIBRARY_DIRS)/lib$(NAME).so
pkcs11wrapper: $(SRCDIR)/$(WRAPSOURCES)
				$(CC) $(CFLAGS) $(SHAREDFLAGS) $(SRCDIR)/$(WRAPSOURCES) -o $(BINDIR)/lib$(WRAPNAME).so $(LDFLAGS)
				ln -s -f $(realpath $(BINDIR)/lib$(WRAPNAME).so) $(LIBRARY_DIRS)/lib$(WRAPNAME).so
all: pkcs11

.PHONY: all clean test testclean runtest

clean: testclean
	rm -f $(BINDIR)/lib$(NAME).so
	rm -f $(BINDIR)/lib$(WRAPNAME).so

test: pkcs11
		$(MAKE) -C ./test/ test

testclean:
	$(MAKE) -C ./test/ clean
	
runtest: test
	$(MAKE) -C ./test/ run

