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
private:
	static bool initialized;
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
		if(!initialized){
			std::string id = "testing";
			CK_RV ret;
			ret = C_Initialize((void*)(id.c_str()));
			initialized = true;
		}
	}

	virtual void TearDown() {
	}
};
bool FooTest::initialized = false;

TEST(FooTest, getSlots){
	std::string id = "testing";
	CK_RV ret;

	CK_SLOT_ID_PTR ids = NULL;
	CK_ULONG size = 0;
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
		ASSERT_GT(size,0);
	ids = new CK_SLOT_ID[size];
	CK_ULONG oldsize = size;
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
		ASSERT_EQ(size,oldsize);
}

TEST(FooTest, getSlotInfo){
	std::string id = "testing";
	CK_RV ret;

	CK_SLOT_ID_PTR ids = NULL;
	CK_ULONG size = 0;
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
		ASSERT_GT(size,0);
	ids = new CK_SLOT_ID[size];
	CK_ULONG oldsize = size;
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
		ASSERT_EQ(size,oldsize);
	CK_SLOT_INFO sInfo;
	ret = C_GetSlotInfo(ids[0],&sInfo);
	ASSERT_EQ(ret,CKR_OK);
	ASSERT_TRUE(sInfo.flags&CKF_TOKEN_PRESENT);
	ASSERT_TRUE(~(sInfo.flags&CKF_REMOVABLE_DEVICE));
	ASSERT_TRUE(sInfo.flags&CKF_HW_SLOT);
}

TEST(FooTest, tokeninfo){
	std::string id = "testing";
	CK_RV ret;
	CK_SLOT_ID_PTR ids = NULL;
	CK_ULONG size = 0;
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
	ids = new CK_SLOT_ID[size];
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
	if(size > 0){
		CK_TOKEN_INFO tinfo;
		ret = C_GetTokenInfo(ids[0],&tinfo);
		ASSERT_EQ(ret,CKR_OK);
	}
}


TEST(FooTest, sessioninfo){
	std::string id = "testing";
	CK_RV ret;

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
	CK_SESSION_INFO sess_info;
	ret = C_GetSessionInfo(sess,&sess_info);
		ASSERT_EQ(ret,CKR_OK);
		ASSERT_EQ(sess_info.slotID,ids[0]);
		ASSERT_EQ(sess_info.state,CKS_RW_PUBLIC_SESSION);
}

TEST(FooTest, findobj){
	std::string id = "testing";
	CK_RV ret;

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
	ret = C_FindObjects(sess, &objs, 1,&size);
	ASSERT_EQ(ret,CKR_OK);

	ret = C_FindObjectsFinal(sess);
	ASSERT_EQ(ret,CKR_OK);
	cout << "2nd Find RUN: " << size <<endl;
	ret = C_FindObjectsInit(sess,NULL,0);
		ASSERT_EQ(ret,CKR_OK);
	objs = 0;
	size = 0;
	ret = C_FindObjects(sess, &objs, 1,&size);

	ASSERT_EQ(ret,CKR_OK);
	ret = C_FindObjectsFinal(sess);
	ASSERT_EQ(ret,CKR_OK);
}


TEST(FooTest, createobj){
	std::string name = "testing";
	CK_RV ret;

	CK_SLOT_ID_PTR ids = NULL;
	CK_ULONG size = 0;
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);
	ids = new CK_SLOT_ID[size];
	ret = C_GetSlotList(TRUE,ids,&size);
		ASSERT_EQ(ret,CKR_OK);

	CK_SESSION_HANDLE hSession;
	ret = C_OpenSession(ids[0],CKF_SERIAL_SESSION|CKF_RW_SESSION,(void*)(name.c_str()),NULL,&hSession);
		ASSERT_EQ(ret,CKR_OK);

			CK_OBJECT_HANDLE hData, hCertificate, hKey;
			CK_OBJECT_CLASS
				dataClass = CKO_DATA,
				certificateClass = CKO_CERTIFICATE,
				keyClass = CKO_PUBLIC_KEY;
			CK_KEY_TYPE keyType = CKK_RSA;
			CK_CHAR application[] = {"My Application"};
			CK_BYTE dataValue[] = {"datavalue"};
			CK_BYTE subject[] = {"test"};
			CK_BYTE id[] = {"testID"};
			CK_BYTE certificateValue[] = {""};
			CK_BYTE modulus[] = {""};
			CK_BYTE exponent[] = {""};
			CK_BBOOL trueval = CK_TRUE;
			CK_ATTRIBUTE dataTemplate[] = {
			{CKA_CLASS, &dataClass, sizeof(dataClass)},
			{CKA_TOKEN, &trueval, sizeof(trueval)},
			{CKA_APPLICATION, application, sizeof(application)},
			{CKA_VALUE, dataValue, sizeof(dataValue)}
			};
			CK_ATTRIBUTE certificateTemplate[] = {
			{CKA_CLASS, &certificateClass,sizeof(certificateClass)},
			{CKA_TOKEN, &trueval, sizeof(trueval)},
			{CKA_SUBJECT, subject, sizeof(subject)},
			{CKA_ID, id, sizeof(id)},
			{CKA_VALUE, certificateValue, sizeof(certificateValue)}
			};
			CK_ATTRIBUTE keyTemplate[] = {
			{CKA_CLASS, &keyClass, sizeof(keyClass)},
			{CKA_KEY_TYPE, &keyType, sizeof(keyType)},
			{CKA_WRAP, &trueval, sizeof(trueval)},
			{CKA_MODULUS, modulus, sizeof(modulus)},
			{CKA_PUBLIC_EXPONENT, exponent, sizeof(exponent)}
			};

			/* Create a data object */
			ret = C_CreateObject(hSession, dataTemplate, 4, &hData);
			ASSERT_EQ(ret,CKR_OK);
			/* Create a certificate object */
			ret = C_CreateObject(hSession, certificateTemplate, 5, &hCertificate);
			ASSERT_EQ(ret,CKR_OK);
			/* Create an RSA public key object */
			ret = C_CreateObject(hSession, keyTemplate, 5, &hKey);
			ASSERT_EQ(ret,CKR_OK);
}

