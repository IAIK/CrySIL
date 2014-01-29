/*
 * easytests.cpp
 *
 *  Created on: Jan 24, 2014
 *      Author: thomas malcher
 */

#include "../src/pkcs11.h"
#include "gtest/gtest.h"

using namespace std;

class FooTest: public ::testing::Test {
protected:
	// You can remove any or all of the following functions if its body
	// is empty.

	FooTest() {
		// You can do set-up work for each test here.
	}

	virtual ~FooTest() {
		// You can do clean-up work that doesn't throw exceptions here.
	}

	// If the constructor and destructor are not enough for setting up
	// and cleaning up each test, you can define the following methods:

	virtual void SetUp() {
		std::string id = "testing";
		CK_RV ret;
		ret = C_Initialize((void*)(id.c_str()));
	}

	virtual void TearDown() {
		void* tmp = NULL;
		C_Finalize(tmp);
	}
};

TEST(CJAVATests, getslots){
	std::string id = "testing";
	CK_RV ret;
	ret = C_Initialize((void*)(id.c_str()));
		ASSERT_EQ(ret,CKR_OK);
	CK_SLOT_ID_PTR ids = NULL;
	CK_ULONG size = 0;
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
	ids = new CK_SLOT_ID[size];
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);

	CK_SESSION_HANDLE sess;
	ret = C_OpenSession(ids[0],CKF_SERIAL_SESSION|CKF_RW_SESSION,(void*)(id.c_str()),NULL,&sess);
		ASSERT_EQ(ret,CKR_OK);
	ret = C_FindObjectsInit(sess,NULL,0);
		ASSERT_EQ(ret,CKR_OK);
	CK_OBJECT_HANDLE objs = 0;
	size = 0;
	while (1) {
		ret = C_FindObjects(sess, &objs, 1,&size);
		if (ret != CKR_OK || size == 0)
			break;
	}
	ASSERT_EQ(ret,CKR_OK);

	ret = C_FindObjectsFinal(sess);
	ASSERT_EQ(ret,CKR_OK);
	cout << "2nd Find RUN: " << size <<endl;
	ret = C_FindObjectsInit(sess,NULL,0);
		ASSERT_EQ(ret,CKR_OK);
	objs = 0;
	size = 0;
	while (1) {
		ret = C_FindObjects(sess, &objs, 1,&size);
		if (ret != CKR_OK || size == 0)
			break;
	}
	ASSERT_EQ(ret,CKR_OK);
	ret = C_FindObjectsFinal(sess);
	ASSERT_EQ(ret,CKR_OK);

	ret = C_Finalize(NULL);
		ASSERT_EQ(ret,CKR_OK);
}

//TEST(CJAVATests, init){
//	std::string id = "testing";
//	CK_RV ret;
//	ret = C_Initialize((void*)(id.c_str()));
//	ASSERT_EQ(ret,CKR_OK);
//	ret = C_Finalize(NULL);
//	ASSERT_EQ(ret,CKR_OK);
//}
//TEST(CJAVATests, init0){
//	std::string id = "testing";
//	CK_RV ret;
//	ret = C_Initialize((void*)(id.c_str()));
//	ASSERT_EQ(ret,CKR_OK);
//	ret = C_Finalize(NULL);
//	ASSERT_EQ(ret,CKR_OK);
//}
