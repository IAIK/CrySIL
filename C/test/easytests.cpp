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

class ObjTest: public FooTest {
private:
	static bool initialized;
protected:
	CK_SESSION_HANDLE hSession;
	// You can remove any or all of the following functions if its body
	// is empty.
	ObjTest() {
		// You can do set-up work for each test here.
	}

	virtual ~ObjTest() {
		// You can do clean-up work that doesn't throw exceptions here.
	}

	// If the constructor and destructor are not enough for setting up
	// and cleaning up each test, you can define the following methods:

	virtual void SetUp() {
		FooTest::SetUp();
		if(initialized)
			return;
		std::string name = "testing";
		CK_RV ret;
		CK_SLOT_ID_PTR ids = NULL;
		CK_ULONG size = 0;
		ret = C_GetSlotList(TRUE,ids,&size);
			ASSERT_EQ(ret,CKR_OK);
			ASSERT_GT(size,0);
		ids = new CK_SLOT_ID[size];
		ret = C_GetSlotList(TRUE,ids,&size);
			ASSERT_EQ(ret,CKR_OK);


		ret = C_OpenSession(ids[0],CKF_SERIAL_SESSION|CKF_RW_SESSION,(void*)(name.c_str()),NULL,&hSession);
			ASSERT_EQ(ret,CKR_OK);

				CK_OBJECT_HANDLE hobj;
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
				CK_BYTE modulus[] = {"modulo"};
				CK_BYTE exponent[] = {"exponent"};
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
				ret = C_CreateObject(hSession, dataTemplate, 4, &hobj);
				ASSERT_EQ(ret,CKR_OK);
				/* Create a certificate object */
				ret = C_CreateObject(hSession, certificateTemplate, 5, &hobj);
				ASSERT_EQ(ret,CKR_OK);
				/* Create an RSA public key object */
				ret = C_CreateObject(hSession, keyTemplate, 5, &hobj);
				ASSERT_EQ(ret,CKR_OK);
				initialized = true;
	}

	virtual void TearDown() {
	}
};
bool ObjTest::initialized = false;


TEST_F(FooTest, getSlots){
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

TEST_F(FooTest, getSlotInfo){
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

TEST_F(FooTest, tokeninfo){
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


TEST_F(FooTest, sessioninfo){
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

TEST_F(FooTest, findobj){
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


TEST_F(FooTest, createobj){
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


TEST_F(FooTest, createfindobj){
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

	CK_OBJECT_CLASS	keyClass = CKO_PUBLIC_KEY;
	CK_KEY_TYPE keyType = CKK_RSA;
	CK_BYTE modulus[] = {"modulo"};
	CK_BYTE exponent[] = {"exponent2"};
	CK_BBOOL trueval = CK_TRUE;

	CK_ATTRIBUTE keyTemplate[] = {
		{CKA_CLASS, &keyClass, sizeof(keyClass)},
		{CKA_KEY_TYPE, &keyType, sizeof(keyType)},
		{CKA_WRAP, &trueval, sizeof(trueval)},
		{CKA_MODULUS, modulus, sizeof(modulus)},
		{CKA_PUBLIC_EXPONENT, exponent, sizeof(exponent)}
		};
	CK_OBJECT_HANDLE cobjs = 0;
	CK_OBJECT_HANDLE fobjs = 0;
	ret = C_CreateObject(sess, keyTemplate, 5, &cobjs);
		ASSERT_EQ(ret,CKR_OK);
		ASSERT_NE(cobjs,0);

	ret = C_FindObjectsInit(sess,NULL,0);
		ASSERT_EQ(ret,CKR_OK);
	size = 0;

	while((ret = C_FindObjects(sess, &fobjs, 1,&size)) == CKR_OK && size != 0 ) {
		cout << "obj found: " <<  fobjs << endl;
		ASSERT_EQ(size,1);
	}
	ASSERT_EQ(ret,CKR_OK);

	ret = C_FindObjectsFinal(sess);
	ASSERT_EQ(ret,CKR_OK);
	cout << "2nd Find RUN: " << size <<endl;

	ret = C_FindObjectsInit(sess,keyTemplate,5);
		ASSERT_EQ(ret,CKR_OK);
	fobjs = 0;
	size = 0;
	while((ret = C_FindObjects(sess, &fobjs, 1,&size)) == CKR_OK && size != 0 ) {
		ASSERT_EQ(cobjs,fobjs);
		ASSERT_EQ(size,1);
	}
	ASSERT_EQ(ret,CKR_OK);

	ret = C_FindObjectsFinal(sess);
	ASSERT_EQ(ret,CKR_OK);
}

TEST_F(FooTest, getattr){
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


	CK_BYTE modulus[] = {"modulo"};
	CK_BYTE exponent[] = {"exponent2"};


	CK_ATTRIBUTE keyTemplate[] = {
		{CKA_MODULUS, modulus, sizeof(modulus)},
		{CKA_PUBLIC_EXPONENT, exponent, sizeof(exponent)}
		};
	CK_OBJECT_HANDLE fobjs = 0;


	ret = C_FindObjectsInit(sess,keyTemplate,2);
		ASSERT_EQ(ret,CKR_OK);
	fobjs = 0;
	size = 0;
	int count = 0;
	while((ret = C_FindObjects(sess, &fobjs, 1,&size)) == CKR_OK && size != 0 ) {
		ASSERT_EQ(size,1);
		count=+size;
	}
	ASSERT_NE(fobjs,0);
	ASSERT_EQ(count,1);
	ASSERT_EQ(ret,CKR_OK);
	ret = C_FindObjectsFinal(sess);
	ASSERT_EQ(ret,CKR_OK);

	CK_BYTE_PTR pModulus, pExponent;
	CK_ATTRIBUTE gettemplate[] = {
			{CKA_MODULUS, NULL_PTR, 0},
			{CKA_PUBLIC_EXPONENT, NULL_PTR, 0}
	};
	CK_RV rv;

	rv = C_GetAttributeValue(sess, fobjs, gettemplate,2);
	ASSERT_EQ(ret,CKR_OK);
	if (rv == CKR_OK) {
		pModulus = (CK_BYTE_PTR) malloc(gettemplate[0].ulValueLen);
		gettemplate[0].pValue = pModulus;
		/* template[0].ulValueLen was set by C_GetAttributeValue */
		pExponent = (CK_BYTE_PTR) malloc(gettemplate[1].ulValueLen);
		gettemplate[1].pValue = pExponent;
		/* template[1].ulValueLen was set by C_GetAttributeValue */
		rv = C_GetAttributeValue(sess, fobjs, gettemplate,2);
		ASSERT_EQ(ret,CKR_OK);

		cout << "modulo: "<< gettemplate[0].pValue << endl;
		cout << "exponent: "<< gettemplate[1].pValue << endl;

		ASSERT_STREQ((const char*)modulus,(const char*)gettemplate[0].pValue);
		ASSERT_STREQ((const char*)exponent,(const char*)gettemplate[1].pValue);

		free(pModulus);
		free(pExponent);
	}

}
TEST_F(ObjTest, getattr2){

	CK_BYTE modulus[] = {"modulo"};
	CK_BYTE exponent[] = {"exponent"};
	CK_BYTE newmodulus[] = {"hallo"};
	CK_BYTE newexponent[] = {"welt"};

	CK_ATTRIBUTE keyTemplate[] = {
		{CKA_MODULUS, modulus, sizeof(modulus)},
		{CKA_PUBLIC_EXPONENT, exponent, sizeof(exponent)}
		};

	CK_RV ret;
	ret = C_FindObjectsInit(hSession,keyTemplate,2);
		ASSERT_EQ(ret,CKR_OK);
	CK_OBJECT_HANDLE fobjs = 0;
	CK_ULONG size = 0;
	int count = 0;
	while((ret = C_FindObjects(hSession, &fobjs, 1,&size)) == CKR_OK && size != 0 ) {
		ASSERT_EQ(size,1);
		count=+size;
	}
	ASSERT_NE(fobjs,0);
	ASSERT_EQ(count,1);
	ASSERT_EQ(ret,CKR_OK);
	ret = C_FindObjectsFinal(hSession);
	ASSERT_EQ(ret,CKR_OK);


	CK_ATTRIBUTE settemplate[] = {
			{CKA_MODULUS, newmodulus, sizeof(newmodulus)},
			{CKA_PUBLIC_EXPONENT, newexponent, sizeof(newexponent)}
	};

	ret = C_SetAttributeValue(hSession,fobjs,settemplate,2);
	ASSERT_EQ(ret,CKR_OK);
	cout << "modulo: " << endl;
	CK_BYTE_PTR pModulus, pExponent;
	CK_ATTRIBUTE gettemplate[] = {
			{CKA_MODULUS, NULL_PTR, 0},
			{CKA_PUBLIC_EXPONENT, NULL_PTR, 0}
	};

	CK_RV rv;
	rv = C_GetAttributeValue(hSession, fobjs, gettemplate,2);
	ASSERT_EQ(ret,CKR_OK);
	if (rv == CKR_OK) {
		pModulus = (CK_BYTE_PTR) malloc(gettemplate[0].ulValueLen);
		gettemplate[0].pValue = pModulus;
		/* template[0].ulValueLen was set by C_GetAttributeValue */
		pExponent = (CK_BYTE_PTR) malloc(gettemplate[1].ulValueLen);
		gettemplate[1].pValue = pExponent;
		/* template[1].ulValueLen was set by C_GetAttributeValue */
		rv = C_GetAttributeValue(hSession, fobjs, gettemplate,2);
		ASSERT_EQ(ret,CKR_OK);

		cout << "modulo: "<< gettemplate[0].pValue << endl;
		cout << "exponent: "<< gettemplate[1].pValue << endl;

		ASSERT_STREQ((const char*)newmodulus,(const char*)gettemplate[0].pValue);
		ASSERT_STREQ((const char*)newexponent,(const char*)gettemplate[1].pValue);

		free(pModulus);
		free(pExponent);
	}

}
