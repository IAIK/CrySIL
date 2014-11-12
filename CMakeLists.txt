cmake_minimum_required(VERSION 2.8)
project (skytrust-pkcs11)

SET(CMAKE_BUILD_TYPE Release)
FIND_PACKAGE(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
LINK_DIRECTORIES(${JNI_LIBRARIES})

add_definitions(-DSYKTRUSTJAR="${PROJECT_BINARY_DIR}/lib/PKCS11.jar")

if (WIN32)
	message ("This is a WIN32 machine!")
	SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DWIN32=true ")
elseif (UNIX)
	message ("This is a linux machine!")
	SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -std=c99 -pedantic")
	ADD_CUSTOM_TARGET(ant_build ant jar)
	ADD_CUSTOM_COMMAND(TARGET ant_build COMMAND ant)

elseif (APPLE)
	message ("This is a mac machine!")
	

endif()

SET(LIBSRC "src/C/src/pkcs11.c")
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -std=c89 -pedantic")
SET(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/lib)
SET(CMAKE_RUNTIME_OUTPUT_DIRECTORY_DEBUG ${PROJECT_BINARY_DIR}/lib)
SET(CMAKE_RUNTIME_OUTPUT_DIRECTORY_RELEASE ${PROJECT_BINARY_DIR}/lib)
SET(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/lib)
add_library(skytrustpkcs11 SHARED ${LIBSRC})
target_link_libraries(skytrustpkcs11 ${JAVA_JVM_LIBRARY})
#install(TARGETS skytrustpkcs11 DESTINATION ${CMAKE_INSTALL_PREFIX}/lib) 
