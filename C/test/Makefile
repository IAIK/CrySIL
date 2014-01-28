CC = g++
CFLAGS = -Wall 

TESTDIR = .
TESTNAME = $(TESTDIR)/runallTests
TESTSOURCES = $(TESTDIR)/maintest.cpp $(TESTDIR)/easytests.cpp
TESTOBJS := ${TESTSOURCES:.cpp=.o}

TEST_LIBRARIES := gtest pthread skytrustpkcs11

#following variables get overwritten through commandline from ant
INCLUDE_DIRS := ../../include/java/ ../../include/
LIBRARY_DIRS := ../../lib/
#

CFLAGS += $(foreach includedir,$(INCLUDE_DIRS),-I$(includedir))
CFLAGS += $(foreach librarydir,$(LIBRARY_DIRS),-Wl,-rpath,$(librarydir))
LDFLAGS += $(foreach librarydir,$(LIBRARY_DIRS),-L$(librarydir))
LDFLAGS += $(foreach library,$(TEST_LIBRARIES),-l$(library))

%.o: %.cpp 
	$(CC) $(CFLAGS) -c -o $@ $< $(CFLAGS)

test: $(TESTOBJS)
		$(CC) $(CFLAGS) $(TESTOBJS) -o $(TESTNAME) $(LDFLAGS) 

testclean:
	rm -f $(TESTOBJS)
	rm -f $(TESTNAME)

run: test
	$(TESTNAME)

