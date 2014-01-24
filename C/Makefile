CC = g++
WRAPSOURCES = pkcs11_java_wrap.c
SOURCES = pkcs11.c
INCLUDE_DIRS := /usr/lib/jvm/java-7-openjdk-amd64/include/
LIBRARY_DIRS := /usr/lib/jvm/java-7-openjdk-amd64/jre/lib/amd64/server
LIBRARIES := jvm

CFLAGS = -Wall 
BINDIR = build
SRCDIR = src
SHAREDFLAGS = -shared -fPIC -g 

NAME = skytrustpkcs11
WRAPNAME = $(NAME)_wrapper

CFLAGS += $(foreach includedir,$(INCLUDE_DIRS),-I$(includedir))
LDFLAGS += $(foreach librarydir,$(LIBRARY_DIRS),-L$(librarydir))
LDFLAGS += $(foreach library,$(LIBRARIES),-l$(library))

pkcs11: $(SRCDIR)/$(SOURCES) pkcs11wrapper
				$(CC) $(CFLAGS) $(SHAREDFLAGS) $(SRCDIR)/$(SOURCES) -o $(BINDIR)/lib$(NAME).so $(LDFLAGS)
        
pkcs11wrapper: $(SRCDIR)/$(WRAPSOURCES)
				$(CC) $(CFLAGS) $(SHAREDFLAGS) $(SRCDIR)/$(WRAPSOURCES) -o $(BINDIR)/lib$(WRAPNAME).so $(LDFLAGS)

all: pkcs11

TESTDIR = test
TESTNAME = $(TESTDIR)/mytest
TESTSOURCES = $(TESTDIR)/maintest.cpp $(TESTDIR)/easytests.cpp
TESTOBJS := ${TESTSOURCES:.cpp=.o}
TEST_INCLUDE_DIRS := -I/home/cloud/uni/ACN/pkcs11_private/include/
TEST_LIBRARY_DIRS := -L/home/cloud/uni/ACN/pkcs11_private/lib/
TEST_LIBRARIES := -lgtest -lpthread

%.o: %.cpp 
	$(CC) -c -o $@ $< $(CFLAGS) $(TEST_INCLUDE_DIRS)

test: $(TESTOBJS)
		$(CC) $(TESTOBJS) -o $(TESTNAME) $(TEST_INCLUDE_DIRS) $(TEST_LIBRARY_DIRS) $(TEST_LIBRARIES)

testclean:
	rm -f $(TESTOBJS)
	rm -f $(TESTNAME)
runtest: test
	$(TESTDIR)/teste

