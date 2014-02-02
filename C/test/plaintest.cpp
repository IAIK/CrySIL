/*
 * easytests.cpp
 *
 *  Created on: Jan 24, 2014
 *      Author: thomas malcher
 */

#include "../src/pkcs11.h"
#include <cassert>
#include <string>
#include <iostream>

using namespace std;

typedef CK_ULONG_PTR CK_ULONG_JPTR;
typedef CK_ULONG_PTR CK_ULONG_ARRAY;

int main(int argc, char **argv) {
	string id = "testing";
	CK_RV ret;
	ret = C_Initialize((void*)(id.c_str()));
		assert(ret==CKR_OK);
	CK_SLOT_ID_PTR ids = 0;
	CK_ULONG size = 0;
	ret = C_GetSlotList(TRUE,ids,&size);
	assert(ret==CKR_OK);
		cout << "Slotcount: " << size<<endl;
	ids = new CK_SLOT_ID[size];
	ret = C_GetSlotList(TRUE,ids,&size);
	assert(ret==CKR_OK);

	if(size > 0){
		CK_TOKEN_INFO tinfo;
		ret = C_GetTokenInfo(ids[0],&tinfo);
		assert(ret==CKR_OK);
		tinfo.label[31] = '\0';
		cout << "Label: " << tinfo.label << endl;
	}

	CK_SESSION_HANDLE sess;
	ret = C_OpenSession(ids[0],4,(void*)(id.c_str()),0,&sess);
	assert(ret==CKR_OK);
	ret = C_FindObjectsInit(sess,0,0);
	assert(ret==CKR_OK);
	CK_OBJECT_HANDLE objs = 0;
	size = 0;
	ret = C_FindObjects(sess,&objs,1,&size);
	assert(ret==CKR_OK);
	ret = C_FindObjectsFinal(sess);
	assert(ret==CKR_OK);
	ret = C_Finalize(0);
	assert(ret==CKR_OK);
}

