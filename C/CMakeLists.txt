cmake_minimum_required(VERSION 2.8)
project (test)


set(CMAKE_BUILD_TYPE Release)


FIND_PACKAGE(JNI REQUIRED)


include_directories(${JNI_INCLUDE_DIRS})

LINK_DIRECTORIES(	${JNI_LIBRARIES})


SET(WRAPPERSRC "src/pkcs11_java_wrap.c")
add_library(pkcs11_java_wrap SHARED ${WRAPPERSRC})
target_link_libraries(pkcs11_java_wrap ${JAVA_JVM_LIBRARY})
install(TARGETS pkcs11_java_wrap DESTINATION ${CMAKE_SOURCE_DIR}/../lib/)

if (NOT JAR)
SET(JAR "../lib/PKCS11.jar")
endif()


if (DEBUG STREQUAL "true")
 	message (STATUS "Java remote DEBUG is On (VM will wait for debugger)")
	add_definitions(-DDEBUG="${DEBUG}")
endif()

add_definitions(-DSYKTRUSTJAR="${JAR}")

SET(LIBSRC "src/pkcs11.c")
add_library(skytrustpkcs11 SHARED ${LIBSRC})
target_link_libraries(skytrustpkcs11 ${JAVA_JVM_LIBRARY})
install(TARGETS skytrustpkcs11 DESTINATION ${CMAKE_SOURCE_DIR}/../lib/)

