cmake_minimum_required(VERSION 2.8)
project (skytrust-pkcs11)

SET(CMAKE_INSTALL_PREFIX "/usr/lib/skytrust/")

SET(CMAKE_BUILD_TYPE Release)

FIND_PACKAGE(JNI REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})

LINK_DIRECTORIES(${JNI_LIBRARIES})


if (NOT JAR)
SET(JAR "/usr/lib/skytrust/PKCS11.jar")
endif()

add_definitions(-DSYKTRUSTJAR="${JAR}")
if (WIN32)
	message ("This is a WIN32 machine!")
	SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DWIN32=true")
	
else()
	message ("This is a linux machine!")
	SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -std=c90 -pedantic")
	add_definitions(-DWIN32=true)

endif()

SET(LIBSRC "C/src/pkcs11.c")
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -std=c99 -pedantic")
add_library(skytrustpkcs11 SHARED ${LIBSRC})
target_link_libraries(skytrustpkcs11 ${JAVA_JVM_LIBRARY})
install(TARGETS skytrustpkcs11 DESTINATION ${CMAKE_INSTALL_PREFIX}/) 
