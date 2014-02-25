package pkcs11;

import gui.Server.ServerInfo;

import java.util.ArrayList;
import java.util.Iterator;

import objects.MECHANISM;
import objects.PKCS11Object;
import objects.StructBase;

import proxys.ATTRIBUTE_TYPE;
import proxys.CK_BYTE_ARRAY;
import proxys.CK_INFO;
import proxys.CK_MECHANISM;
import proxys.CK_MECHANISM_INFO;
import proxys.CK_NOTIFY_CALLBACK;
import proxys.CK_SESSION_INFO;
import proxys.CK_SLOT_INFO;
import proxys.CK_TOKEN_INFO;
import proxys.CK_ULONG_ARRAY;
import proxys.CK_ULONG_JPTR;
import proxys.MECHANISM_TYPES;
import proxys.RETURN_TYPE;
import proxys.pkcs11Constants;
import objects.ATTRIBUTE;


public class JAVApkcs11Interface implements pkcs11Constants {
	  static {
		    System.load("/usr/lib/libpkcs11_java_wrap.so");
		  }

	private static ResourceManager getRM() throws PKCS11Error{
		ResourceManager _instance = ResourceManager.getInstance(appID);
		if(_instance == null){
			throw new PKCS11Error(RETURN_TYPE.GENERAL_ERROR);
		}
		return _instance;
	}
	private static String appID;
	
	public static long C_Initialize(CK_BYTE_ARRAY  pInitArgs){
		appID = "newRandomID";
		return RETURN_TYPE.OK.swigValue();
	}
	
  private static void checkNullPtr(Object ...structs) throws PKCS11Error{
	  for(Object s:structs){
		  if(s == null /*|| s.isNullPtr()*/){
			  throw new PKCS11Error(RETURN_TYPE.ARGUMENTS_BAD);
		  }
	  }
  }
	
  public static long C_OpenSession(long slotID, long flags, CK_BYTE_ARRAY pApplication, CK_NOTIFY_CALLBACK Notify, CK_ULONG_JPTR phSession) {
	  System.err.println("C_OpenSession ............start.............");
	  try {
		  checkNullPtr(phSession);
		  if(!Util.isFlagSet(flags, CKF_SERIAL_SESSION)){
			  System.err.println("C_OpenSession ............error.............");
			  return RETURN_TYPE.GENERAL_ERROR.swigValue(); //CKR_PARALLEL_NOT_SUPPORTED
		  }
		  Session.ACCESS_TYPE atype = Session.ACCESS_TYPE.RO;
		  if(Util.isFlagSet(flags, CKF_RW_SESSION)){
			  atype = Session.ACCESS_TYPE.RW;
		  }
		  phSession.assign(getRM().newSession(slotID, atype));
		  System.err.println("C_OpenSession ............end.............");
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  System.err.println("C_OpenSession ............error1.............");
		  e.printStackTrace();
		  return e.getCode();
	  } 
  }

  public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
	  System.err.println("\n slotinfo..start..............................................");
	  Slot slot = null;
	  try {
		  checkNullPtr(pInfo);
		  slot = getRM().getSlotByID(slotID);
	  } catch (PKCS11Error e) {
		  return e.getCode();
	  }

	  long flags = Util.initFlags;

	  flags = Util.setFlag(flags, CKF_TOKEN_PRESENT);
	  flags = Util.setFlag(flags, CKF_HW_SLOT);
	  pInfo.setFlags(flags);

	  pInfo.setManufacturerID("IAIK Skytrust                                                                                       "); //32
	  pInfo.setSlotDescription(slot.getServerInfo().getName()+"                                                                                       "); //32

	  System.err.println("\n slotinfo..ende..............................................");
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetSlotList(short tokenPresent, CK_ULONG_ARRAY pSlotList, CK_ULONG_JPTR pulCount) {
	  System.err.println("\n slotlist..start.............................................."+pulCount.value());
	  try {
		  checkNullPtr(pulCount);
		  ArrayList<Slot> slotlist = null;
		  slotlist = getRM().getSlotList();
		  for(Slot s:slotlist){
			  System.err.println("\n slotlist return slot: "+s.getServerInfo().getName());
		  }
		  if(pSlotList == null){
			  pulCount.assign(slotlist.size());
			  return RETURN_TYPE.OK.swigValue();
		  }else if(pulCount.value() < slotlist.size()){
			  pulCount.assign(slotlist.size());
			  return RETURN_TYPE.BUFFER_TOO_SMALL.swigValue();
		  }else{
			  pulCount.assign(slotlist.size());
			  Iterator<Slot> it = slotlist.iterator();
			  for(int i=0;it.hasNext();i++){
				  pSlotList.setitem(i, it.next().getID());
			  }
			  System.err.println("\n slotlist..ende..............................................");
			  return RETURN_TYPE.OK.swigValue();
		  }
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  System.err.println("\n slotlist..exception..............................................");
		  return e.getCode();
	  } 
  }

  public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
	  System.err.println("\n tokeninfo..start..............................................");
	  try {
		  Slot slot = getRM().getSlotByID(slotID);

		  ServerInfo s = slot.getServerInfo();
		  pInfo.setLabel(Util.fixStringLen(s.getName(),32));//32 char
		  pInfo.setManufacturerID(Util.fixStringLen("IAIK", 32));
		  pInfo.setModel(Util.fixStringLen("", 32));//32
		  pInfo.setSerialNumber(Util.fixStringLen("0.1", 32));

		  long flags = Util.initFlags;
		  flags = Util.setFlag(flags,CKF_WRITE_PROTECTED);
		  flags = Util.setFlag(flags,CKF_PROTECTED_AUTHENTICATION_PATH);
		  flags = Util.setFlag(flags,CKF_TOKEN_INITIALIZED);
		  flags = Util.setFlag(flags, slot.getCapabilities().getAsFlags());
		  pInfo.setFlags(flags);

		  pInfo.setUlRwSessionCount(Long.MAX_VALUE);
		  pInfo.setUlSessionCount(Long.MAX_VALUE);
		  pInfo.setUlMaxSessionCount(Long.MAX_VALUE);
		  pInfo.setUlMaxRwSessionCount(Long.MAX_VALUE);

		  pInfo.setUlFreePrivateMemory(0);
		  pInfo.setUlFreePublicMemory(0);
		  pInfo.setUlTotalPrivateMemory(0);
		  pInfo.setUlTotalPublicMemory(0);

		  pInfo.setUlMaxPinLen(0);
		  pInfo.setUlMinPinLen(0);
		  System.err.println("\n tokeninfo..ende..............................................");
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  return e.getCode();
	  }

  }
  public static long C_Login(long hSession, long userType, String pPin, long ulPinLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_Logout(long hSession) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_CloseSession(long hSession) {
	  try {
		  getRM().delSession(hSession);
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  return e.getCode();
	  }
  }

  public static long C_CloseAllSessions(long slotID) {
	  try {
		  getRM().delAllSessionsToSlot(slotID);
		  return RETURN_TYPE.OK.swigValue();	
	  } catch (PKCS11Error e) {
		  return e.getCode();
	  }
  }

  public static long C_GetAttributeValue(long hSession, long hObject, ATTRIBUTE[] pTemplate, long ulCount) {
	  try {
		  System.err.println("hObject: "+ ulCount);
		  checkNullPtr(pTemplate);//cool
		  if(pTemplate.length != ulCount){
			  throw new PKCS11Error(RETURN_TYPE.ARGUMENTS_BAD);
		  }
		  Session session = getRM().getSessionByHandle(hSession);		  
		  PKCS11Object obj = session.getSlot().objectManager.getObject(hObject);

		  RETURN_TYPE res = RETURN_TYPE.OK;
		  ATTRIBUTE src;
		  for(ATTRIBUTE attr : pTemplate){
			  try{
			  	  src = obj.getAttribute(attr.getTypeEnum());
			  }catch(PKCS11Error e){
				  if(e.getType() == RETURN_TYPE.ATTRIBUTE_TYPE_INVALID){
					  attr.setDataLength(-1);
					  res =  RETURN_TYPE.ATTRIBUTE_TYPE_INVALID;
					  continue;
				  }else{
					  throw e;
				  }
			  }
			  //TODO Attribute die ein array als Daten haben richtig behandeln
			  if(attr.isCDataNULL()){
				  attr.setDataLength(src.getDataLength());
			  }else if(attr.getDataLength() < src.getDataLength()){
				  attr.setDataLength(-1);
				  res = RETURN_TYPE.BUFFER_TOO_SMALL;
			  }else{
				  src.copyDataTo(attr);
			  }
		  }
		  return res.swigValue();
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }
  }
  public static long C_SetAttributeValue(long hSession, long hObject, ATTRIBUTE[]  pTemplate, long ulCount) {

	  try {
		  checkNullPtr(pTemplate);
		  Session session = getRM().getSessionByHandle(hSession);		  
		  PKCS11Object obj = session.getSlot().objectManager.getObject(hObject);
		  
		  for(ATTRIBUTE attr : pTemplate){
			  obj.setAttribute(attr.createClone());
		  }
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }
  }
  
  public static long C_CreateObject(long hSession, ATTRIBUTE[] pTemplate, long ulCount, CK_ULONG_JPTR phObject) {
	  
	try {
		Session session = getRM().getSessionByHandle(hSession);		
		long handle = session.getToken().objectManager.createObject(pTemplate);
		phObject.assign(handle);
	} catch (PKCS11Error e) {
		e.printStackTrace();
		return e.getCode();
	}

	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_DecryptInit(long hSession, MECHANISM pMechanism, long hKey) {
	  try {
		  checkNullPtr(pMechanism);
		  Session session = getRM().getSessionByHandle(hSession);	
		  session.decryptInit(pMechanism, hKey);
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }
  }

  public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart, long ulEncryptedPartLen, CK_BYTE_ARRAY pPart, CK_ULONG_JPTR pulPartLen) {
	  try {
		  checkNullPtr(pulPartLen,pEncryptedPart);
		  Session session = getRM().getSessionByHandle(hSession);
		  session.decrypt(pEncryptedPart);

		  byte[] decryptedPart = session.decryptGetData();
		  if(pPart == null){
			  pulPartLen.assign(decryptedPart.length);
			  return RETURN_TYPE.OK.swigValue();
		  }else if(pulPartLen.value() < decryptedPart.length){
			  pulPartLen.assign(decryptedPart.length);
			  return RETURN_TYPE.BUFFER_TOO_SMALL.swigValue();
		  }else{
			  pulPartLen.assign(decryptedPart.length);
			  Util.copyByteArrayToCData(decryptedPart, pPart);
			  System.err.println("\n slotlist..ende..............................................");
			  return RETURN_TYPE.OK.swigValue();
		  }
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }
  }

  public static long C_DestroyObject(long hSession, long hObject) {
	  try {
		  Session session = getRM().getSessionByHandle(hSession);
		  session.getToken().objectManager.deleteObject(hObject);
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }
  }

  public static long C_Finalize(CK_BYTE_ARRAY pReserved) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjectsInit(long hSession, ATTRIBUTE[] pTemplate, long ulCount) {
	  System.err.println("\nthis is java calling FindobjectsInit");
	  try {
		  Session session = getRM().getSessionByHandle(hSession);
		  session.find(pTemplate);
		  
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }catch (Exception e){
		  e.printStackTrace();
	  }
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjects(long hSession, CK_ULONG_ARRAY phObject, long ulMaxObjectCount, CK_ULONG_JPTR pulObjectCount) {
	  System.err.println("\nthis is java calling Findobjects");
	  try {
		checkNullPtr(phObject,pulObjectCount);
		Session session = getRM().getSessionByHandle(hSession);
		
		ArrayList<Long> handles = session.findGetData();
		
		Iterator<Long> it = handles.iterator();
		int size=0;
		for(;size<ulMaxObjectCount && it.hasNext();size++){
			phObject.setitem(size, it.next());
			it.remove();
		}
		pulObjectCount.assign(size);
		
	} catch (PKCS11Error e) {
		e.printStackTrace();
		System.err.println("findobjects....error1");
		return e.getCode();
	} catch (Exception e){
		e.printStackTrace();
			System.err.println("findobjects.... erro2");
	}
	 return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjectsFinal(long hSession) {
	  System.err.println("\nthis is java calling FindobjectsFinal");
	  try {
		Session session = getRM().getSessionByHandle(hSession);
		session.findFinal();
	} catch (PKCS11Error e) {
		e.printStackTrace();
		return e.getCode();
	}
	  return RETURN_TYPE.OK.swigValue();
  }
	  


  public static long C_GenerateRandom(long hSession, CK_BYTE_ARRAY RandomData, long ulRandomLen) {
	  return RETURN_TYPE.OK.swigValue();
  }


  public static long C_GetInfo(CK_INFO pInfo) {
	  try {
		  System.err.println("\n java is calling, C_GetINFO");
		  checkNullPtr(pInfo);
		  pInfo.getCryptokiVersion().setMajor((short) 0x02);
		  pInfo.getCryptokiVersion().setMinor((short) 0x14);

		  pInfo.getLibraryVersion().setMajor((short) 1);
		  pInfo.getLibraryVersion().setMinor((short) 0);
		  
		  pInfo.setLibraryDescription(Util.fixStringLen("skytrust pkcs11 library", 32));

		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }
  }

  public static long C_GetMechanismInfo(long slotID, long type, CK_MECHANISM_INFO pInfo) {
	  try {
		  checkNullPtr(pInfo);
		  System.err.println("java: C_GetMechanismInfo........start");
		  Slot slot = getRM().getSlotByID(slotID);
		  slot.getMechanismInfo(MECHANISM_TYPES.swigToEnum((int) type), pInfo);
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }
  }

  public static long C_GetMechanismList(long slotID, CK_ULONG_ARRAY pMechanismList, CK_ULONG_JPTR pulCount) {
	  System.err.println("\nC_GetMechanismList...........................");
	  try {
		  checkNullPtr(pulCount);
		  Slot slot=getRM().getSlotByID(slotID);
		  int mech_count = slot.getMechanisms().length;
		  
		  if(pMechanismList==null){
			  System.err.println("\nC_GetMechanismList.........ende0  ...........");
			  pulCount.assign(mech_count);
			  return RETURN_TYPE.OK.swigValue();
		  }else if(pulCount.value() < mech_count){
			  System.err.println("\nC_GetMechanismList.........buffer  ...........");
			  pulCount.assign(mech_count);
			  return RETURN_TYPE.BUFFER_TOO_SMALL.swigValue();
		  }else{
			  pulCount.assign(mech_count);
			  MECHANISM_TYPES[] mechanisms = slot.getMechanisms();
			  for(int i=0; i<mech_count; i++){
				  pMechanismList.setitem(i, mechanisms[i].swigValue());
			  }
			  System.err.println("\nC_GetMechanismList.........ende  ...........");
			  return RETURN_TYPE.OK.swigValue();
		  }
	  } catch (PKCS11Error e) {
		  return e.getCode();
	  } 
  }

  public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
	  System.err.println("\njava: getSessionInfo start.................");
	  try {
		  checkNullPtr(pInfo);
		  Session session = getRM().getSessionByHandle(hSession);
		  pInfo.setSlotID(session.getSlot().getID());

		  long flags = Util.initFlags;
		  flags = Util.setFlag(flags,CKF_SERIAL_SESSION);
		  if(session.isRW()){
			  flags = Util.setFlag(flags,CKF_RW_SESSION);
		  }
		  pInfo.setFlags(flags);
		  pInfo.setState(session.getSessionState().swigValue());
		  System.err.println("java: getSessionInfo end.................");
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  return e.getCode();
	  }
  }

  public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen, String pNewPin, long ulNewLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SignInit(long hSession, MECHANISM pMechanism, long hKey) {
	  try {
		  checkNullPtr(pMechanism);
		  Session session = getRM().getSessionByHandle(hSession);
		  session.signInit(pMechanism, hKey);
		  return RETURN_TYPE.OK.swigValue();
	  } catch (PKCS11Error e) {
		  e.printStackTrace();
		  return e.getCode();
	  }	  
  }

  public static long C_Sign(long hSession, byte[] pData, long ulDataLen, CK_BYTE_ARRAY pSignature, CK_ULONG_JPTR pulSignatureLen) {
	  try {
		checkNullPtr(pulSignatureLen);
		Session session = getRM().getSessionByHandle(hSession);
		session.signAddData(pData);

		if(pSignature == null){
			pulSignatureLen.assign(session.sign().length);
			return RETURN_TYPE.OK.swigValue();
		}else if(pulSignatureLen.value() < session.sign().length){
			pulSignatureLen.assign(session.sign().length);
			return RETURN_TYPE.BUFFER_TOO_SMALL.swigValue();
		}else{
			byte[] signed = session.sign();
			for(int i=0; i<signed.length; i++){
				pSignature.setitem(i, signed[i]);
			}
			session.signFinal();
			return RETURN_TYPE.OK.swigValue();
		}
	} catch (PKCS11Error e) {
		e.printStackTrace();
		return e.getCode();
	}
  }


  
  /**
 	 * unwraps (i.e. decrypts) a wrapped key, creating a new private key or secret key object
 	 * @param   hSession   			current session's handle
 	 * @param	pMechanism 			wrapping mechanism
 	 * @param	hUnwrappingKey		the unwrappingKey
 	 * @param	pWrappedKey			the wrapped key
 	 * @param	ulWrappedKeyLen		length of the wrapped key
 	 * @param	pTemplate			template for the new key
 	 * @param	ulAttributeCount	number of attributes
 	 * @param	phKey				pointer to the key
 	 * 
 	 */
  public static long C_UnwrapKey(long hSession, MECHANISM pMechanism, long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen, ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_JPTR phKey) {
//		Session session;
//		try {
//			session = getRM().getSessionByHandle(hSession);
//			PKCS11Object key = session.getObject(hUnwrappingKey);
//			
//			byte[] unwrappedKey = session.decrypt(pMechanism,pWrappedKey);
//			long hKey = session.newObject(pTemplate);
//			phKey.assign(hKey); 
//			
//			
//			ServerSession sSession =  session.getSlot().getServersession();
//			
//			long hKey = sSession.unwrapKey(pMechanism, hUnwrappingKey, pWrappedKey, ulWrappedKeyLen, pTemplate, ulAttributeCount, phKey);
//			phKey.assign(hKey);
//
//		} catch (PKCS11Error e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return e.getCode();
//		}
//	  
//	  
	  
	  
	  return RETURN_TYPE.OK.swigValue();
	  
  }

  /**
	 * wraps (i.e., encrypts) a private or secret
	 * @param   hSession   			current session's handle
	 * @param	pMechanism 			wrapping mechanism
	 * @param	hWrappingKey 		handle of the wrapping-key
	 * @param	hKey 				handle of the key to be wrapped
	 * @param	pWrappedKey 		buffer for the wrapped key
	 * @param	pulWrappedKeyLen	pointer to the length of the wrapped key
	 * 
	 */
  public static long C_WrapKey(long hSession, MECHANISM pMechanism, long hWrappingKey, long hKey, CK_BYTE_ARRAY pWrappedKey, CK_ULONG_JPTR pulWrappedKeyLen) {
//	  try {
//		Session session = getRM().getSessionByHandle(hSession);
//		
//		
//		ServerSession sSession =  session.getSlot().getServersession();
//		try {
//			sSession.wrapKey(pMechanism, hWrappingKey, hKey);
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new PKCS11Error(RETURN_TYPE.DEVICE_ERROR);
//		}
//		
//	} catch (PKCS11Error e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return e.getCode();
//	}
	  return RETURN_TYPE.OK.swigValue();
  }
  

}
