CC = g++
CFLAGS = -Wall -Wl,-rpath,../../lib/

TESTDIR = .
TESTNAME = $(TESTDIR)/runallTests
TESTSOURCES = $(TESTDIR)/maintest.cpp $(TESTDIR)/easytests.cpp
TESTOBJS := ${TESTSOURCES:.cpp=.o}
TEST_INCLUDE_DIRS := -I../../include/java/ -I../../include/
TEST_LIBRARY_DIRS := -L../../lib/
TEST_LIBRARIES := -lgtest -lpthread -lskytrustpkcs11

%.o: %.cpp 
	$(CC) $(CFLAGS) -c -o $@ $< $(CFLAGS) $(TEST_INCLUDE_DIRS)

test: $(TESTOBJS)
		$(CC) $(CFLAGS) $(TESTOBJS) -o $(TESTNAME) $(TEST_INCLUDE_DIRS) $(TEST_LIBRARY_DIRS) $(TEST_LIBRARIES)

testclean:
	rm -f $(TESTOBJS)
	rm -f $(TESTNAME)

run: test
	$(TESTNAME)

