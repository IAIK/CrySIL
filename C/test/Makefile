CC = g++
CFLAGS = -Wall 

TESTDIR = .
TESTNAME = $(TESTDIR)/runallTests
TESTSOURCES = $(TESTDIR)/maintest.cpp $(TESTDIR)/easytests.cpp
TESTOBJS := ${TESTSOURCES:.cpp=.o}
TEST_INCLUDE_DIRS := -I../../include/java/ -I../../include/
TEST_LIBRARY_DIRS := -L../../lib/
TEST_LIBRARIES := -lgtest -lpthread

%.o: %.cpp 
	$(CC) -c -o $@ $< $(CFLAGS) $(TEST_INCLUDE_DIRS)

test: $(TESTOBJS)
		$(CC) $(TESTOBJS) -o $(TESTNAME) $(TEST_INCLUDE_DIRS) $(TEST_LIBRARY_DIRS) $(TEST_LIBRARIES)

testclean:
	rm -f $(TESTOBJS)
	rm -f $(TESTNAME)

run: test
	$(TESTNAME)

