
import gui.Server.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.org.apache.bcel.internal.generic.RET;

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
import proxys.CK_VERSION;
import proxys.RETURN_TYPE;
import proxys.SESSION_STATE;
import proxys.pkcs11Constants;
import proxys.CK_ATTRIBUTE;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.action.GetBooleanAction;

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
	

	
	
  public static long C_OpenSession(long slotID, long flags, CK_BYTE_ARRAY pApplication, CK_NOTIFY_CALLBACK Notify, CK_ULONG_JPTR phSession) {
		  System.out.println("C_OpenSession ............start.............");
	  /* v0.1 */
	  //public session erstellen
	  /* v0.2 */	  
	  //public session erstellen
	  //in proxy gui: 	create Eintrag f. application
	  //				show to which user-server pair the application wants to connect
	  //				create one time pass
	  
	  
	//	  #define CKF_EXCLUSIVE_SESSION   0x00000001  /* session is exclusive */
	//	  #define CKF_RW_SESSION          0x00000002  /* session is read/write */
	//	  #define CKF_SERIAL_SESSION      0x00000004  /* doesn't support parallel */
	//	  /* CKF_INSERTION_CALLBACK is new for v2.0.  If it is set in the
	//	   * flags supplied to a C_OpenSession call, then instead of actually
	//	   * opening a session, the call is a request to get a callback when
	//	   * the token is inserted. */
	//	  #define CKF_INSERTION_CALLBACK  0x00000008  /* app. gets insertion notice */

	  if(! Util.isFlagSet(flags, CKF_SERIAL_SESSION)){
		  System.out.println("C_OpenSession ............error.............");
		 return RETURN_TYPE.GENERAL_ERROR.swigValue(); //CKR_PARALLEL_NOT_SUPPORTED
	  }
//	  Util.isFlagSet(flags, CKF_INSERTION_CALLBACK);
//	  Util.isFlagSet(flags, CKF_EXCLUSIVE_SESSION);
	  Session.ACCESS_TYPE atype = Session.ACCESS_TYPE.RO;
	  if(Util.isFlagSet(flags, CKF_RW_SESSION)){
		  atype = Session.ACCESS_TYPE.RW;
	  }
	  try {
		  phSession.assign(getRM().newSession(slotID, atype));
	  } catch (PKCS11Error e) {
		  System.out.println("C_OpenSession ............error1.............");
		  e.printStackTrace();
		  return e.getCode();
	  } 
		  System.out.println("C_OpenSession ............end.............");
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetSlotInfo(long slotID, CK_SLOT_INFO pInfo) {
	  System.out.println("\n slotinfo..start..............................................");
      Slot slot = null;
      try {
              slot = getRM().getSlotByID(slotID);
      } catch (PKCS11Error e) {
              return e.getCode();
      }

      long flags = Util.initFlags;

      flags = Util.setFlag(flags, CKF_TOKEN_PRESENT);
      pInfo.setFlags(flags);
      
      pInfo.setManufacturerID("IAIK Skytrust"); //32
	  try{
      pInfo.setSlotDescription(slot.getServerInfo().getName().substring(0, 30)); //32
	  }catch (NullPointerException e){
		  pInfo.setSlotDescription("example description");
	  }
      
	  System.out.println("\n slotinfo..ende..............................................");
      return RETURN_TYPE.OK.swigValue();
  }


  public static long C_GetSlotList(short tokenPresent, CK_ULONG_ARRAY pSlotList, CK_ULONG_JPTR pulCount) {
	  System.out.println("\n slotlist..start.............................................."+pulCount.value());
	  		try {
			ArrayList<Slot> slotlist = null;
			slotlist = getRM().getSlotList();			

			int buffersize = (int) pulCount.value();
			pulCount.assign(slotlist.size());

			if(pSlotList.getCPtr() != 0){
				if(buffersize < slotlist.size()){
					return RETURN_TYPE.BUFFER_TOO_SMALL.swigValue();
				}else{
					Iterator<Slot> it = slotlist.iterator();
					for(int i=0;it.hasNext();i++){
						pSlotList.setitem(i, it.next().getID());
					}
				}
			}
		} catch (PKCS11Error e) {
			e.printStackTrace();
	  System.out.println("\n slotlist..exception..............................................");
			return e.getCode();
		}  
	  System.out.println("\n slotlist..ende..............................................");
		return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetTokenInfo(long slotID, CK_TOKEN_INFO pInfo) {
	  System.out.println("\n tokeninfo..start..............................................");
		Slot slot = null;
		try {
			slot = getRM().getSlotByID(slotID);
		} catch (PKCS11Error e) {
			return e.getCode();
		}

	  try{
		  String label="skytrust_test";
		  ServerInfo s = slot.getServerInfo();
		  if(s!=null){
			 label= s.getName(); 
		  }
		  
		pInfo.setLabel(label);//32 char
		pInfo.setManufacturerID("IAIK");
		pInfo.setModel("");//32

		long flags = Util.initFlags;
		
		flags = Util.setFlag(flags,CKF_WRITE_PROTECTED);
		flags = Util.setFlag(flags,CKF_PROTECTED_AUTHENTICATION_PATH);
		flags = Util.setFlag(flags, CKF_SERIAL_SESSION);
		flags = Util.setFlag(flags, CKF_VERIFY);
		flags = Util.setFlag(flags, CKF_SIGN);
		flags = Util.setFlag(flags, CKF_ENCRYPT);
		flags = Util.setFlag(flags, CKF_DECRYPT);
		flags = Util.setFlag(flags, 0x00000400);

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
	  }catch (Exception e){
		  e.printStackTrace();
	  }

		
	  System.out.println("\n tokeninfo..ende..............................................");
		return RETURN_TYPE.OK.swigValue();
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
		} catch (PKCS11Error e) {
			return e.getCode();
		}
		return RETURN_TYPE.OK.swigValue();
  }
  
  public static long C_CloseAllSessions(long slotID) {
		try {
			getRM().delAllSessionsToSlot(slotID);
		} catch (PKCS11Error e) {
			return e.getCode();
		}
		return RETURN_TYPE.OK.swigValue();	
  }

  public static long C_SetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[]  pTemplate, long ulCount) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static short[] getByteArrayAsShort(CK_ATTRIBUTE attribute){
	  
			CK_BYTE_ARRAY array = new CK_BYTE_ARRAY(attribute.getPValue().getCPtr(), false); //TODO: geht das? 
			short[] a = new short[ (int) attribute.getUlValueLen()];
			for(int i =0; i< attribute.getUlValueLen(); i++){
				a[i] =  array.getitem(i);
			}
			
			return a;
  }
  
  public static long C_CreateObject(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount, CK_ULONG_JPTR phObject) {
	  
	try {
	 Session session = getRM().getSessionByHandle(hSession);
	 ServerSession sSession = session.getSlot().getServersession();
	 
	 for(CK_ATTRIBUTE tmp: pTemplate){
		 tmp.getType();
		 if(tmp.getType() == ATTRIBUTE_TYPE.CLASS.swigValue()){
//			 short[] array = getByteArray(tmp);
			 
			 
		 }
		 
		 
		 
	 }
	 
	 
	 
	} catch (PKCS11Error e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	  
	  
	  
	  
	  
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_DecryptInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_DecryptUpdate(long hSession, byte[] pEncryptedPart, long ulEncryptedPartLen, CK_BYTE_ARRAY pPart, CK_ULONG_JPTR pulPartLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_DestroyObject(long hSession, long hObject) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_Finalize(CK_BYTE_ARRAY pReserved) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjects(long hSession, CK_ULONG_JPTR phObject, long ulMaxObjectCount, CK_ULONG_JPTR pulObjectCount) {
	  System.out.println("\nthis is java calling Findobjects");
	pulObjectCount.assign(0);
	  

//
//	  try {
//		Session session = getRM().getSessionByHandle(hSession);
//		ServerSession sSession = session.getSlot().getServersession();
//		if(session.findObjectsHelper == null){
//			return RETURN_TYPE.OPERATION_NOT_INITIALIZED.swigValue();
//		}
//		
//		sSession.findObjects(session.findObjectsHelper, phObject, ulMaxObjectCount, pulObjectCount);
//		
//		
//		
//	} catch (PKCS11Error e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return e.getCode();
//	} catch (Exception e){
//		e.printStackTrace();
//	}
	 return RETURN_TYPE.OK.swigValue();
  }

  public static long C_FindObjectsFinal(long hSession) {
//	  System.out.println("\nthis is java calling FindobjectsFinal");
//	  try {
//		Session session = getRM().getSessionByHandle(hSession);
//		session.findObjectsHelper = null; 
//	} catch (PKCS11Error e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return e.getCode();
//	}
	  return RETURN_TYPE.OK.swigValue();
  }
	  

  public static long C_FindObjectsInit(long hSession, CK_ATTRIBUTE[] pTemplate, long ulCount) {
	  System.out.println("\nthis is java calling FindobjectsInit");
//	  try {
//		Session session = getRM().getSessionByHandle(hSession);
//		ServerSession sSession = session.getSlot().getServersession();
//		session.findObjectsHelper = new FindObjectsHelper(pTemplate, ulCount);
//	} catch (PKCS11Error e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return e.getCode();
//	}catch (Exception e){
//		e.printStackTrace();
//	}
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GenerateRandom(long hSession, CK_BYTE_ARRAY RandomData, long ulRandomLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetAttributeValue(long hSession, long hObject, CK_ATTRIBUTE[] pTemplate, long ulCount) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetInfo(CK_INFO pInfo) {
	  System.out.println("\n java is calling, C_GetINFO");
	  
	  pInfo.getCryptokiVersion().setMajor((short) 0x02);
	  pInfo.getCryptokiVersion().setMinor((short) 0x14);
	  
	  pInfo.getLibraryVersion().setMajor((short) 1);
	  pInfo.getLibraryVersion().setMinor((short) 0);
	  
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetMechanismInfo(long slotID, long type, CK_MECHANISM_INFO pInfo) {
	  System.out.println("java: C_GetMechanismInfo........start");
	  
	  
	  
	  
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetMechanismList(long slotID, CK_ULONG_ARRAY pMechanismList, CK_ULONG_JPTR pulCount) {
	  System.out.println("\nC_GetMechanismList...........................");
		try {
			Slot slot=getRM().getSlotByID(slotID);
			
			if(pMechanismList.getCPtr()==0L){
				pulCount.assign(slot.getMechanisms().size());
		  System.out.println("\nC_GetMechanismList.........ende0  ...........");
				return RETURN_TYPE.OK.swigValue();
			}
			if(pulCount.value() < slot.getMechanisms().size()){
				pulCount.assign(slot.getMechanisms().size());
		  System.out.println("\nC_GetMechanismList.........buffer  ...........");
				return RETURN_TYPE.BUFFER_TOO_SMALL.swigValue();
			}
			
			int range = slot.getMechanisms().size();
			pulCount.assign(range);
			for(int i=0; i<range; i++){
				pMechanismList.setitem(i, slot.getMechanisms().get(i));
			}
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1){
			e1.printStackTrace();
		}
	  System.out.println("\nC_GetMechanismList.........ende  ...........");
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_GetSessionInfo(long hSession, CK_SESSION_INFO pInfo) {
	  System.out.println("\njava: getSessionInfo start.................");
	  if(pInfo.getCPtr()==0L){
	  System.out.println("\njava: getSessionInfo error.................");
		  return RETURN_TYPE.GENERAL_ERROR.swigValue();
	  }
	  try {
		
		pInfo.setSlotID(1);
		long flags = Util.initFlags;
		
		flags = Util.setFlag(flags,CKF_RW_SESSION);
		flags = Util.setFlag(flags,CKF_SERIAL_SESSION);
		
		pInfo.setFlags(flags);
		
		flags = Util.initFlags;
		
		flags = Util.setFlag(flags,SESSION_STATE.RW_USER_FUNCTIONS.swigValue());
		pInfo.setState(flags);
		
		
	  }catch (Exception e){
		  e.printStackTrace();
	  }
	  
	  System.out.println("java: getSessionInfo end.................");
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SeedRandom(long hSession, String pSeed, long ulSeedLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_SetPIN(long hSession, String pOldPin, long ulOldLen, String pNewPin, long ulNewLen) {
	  return RETURN_TYPE.OK.swigValue();
  }

  public static long C_Sign(long hSession, byte[] pData, long ulDataLen, CK_BYTE_ARRAY pSignature, CK_ULONG_JPTR pulSignatureLen) {
	  try {
		Session session = getRM().getSessionByHandle(hSession);
		if(session.signHelper==null){
			throw new PKCS11Error(RETURN_TYPE.OPERATION_NOT_INITIALIZED);
		}
		if(session.signHelper.pData==null){
			session.signHelper.pData=pData;
			try {
				session.getSlot().getServersession().sign(pData, session.signHelper.hkey);
			} catch (IOException e) {
				e.printStackTrace();
				throw new PKCS11Error(RETURN_TYPE.DEVICE_ERROR);
			}
		}
		
		if(pSignature.getCPtr() == 0L){
			pulSignatureLen.assign(session.signHelper.cData.length);
			throw new PKCS11Error(RETURN_TYPE.OK);
		}else if(pulSignatureLen.value() >= session.signHelper.cData.length){
			for(int i=0; i<session.signHelper.cData.length; i++){
				pSignature.setitem(i, session.signHelper.cData[i]);
			}
			session.signHelper = null;// signing finished, so let's flush the signhelper
			throw new PKCS11Error(RETURN_TYPE.OK);
		}else{
			throw new PKCS11Error(RETURN_TYPE.BUFFER_TOO_SMALL);
		}
		
	} catch (PKCS11Error e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getCode();
	}
  }

  public static long C_SignInit(long hSession, CK_MECHANISM pMechanism, long hKey) {
	  try {
		Session session = getRM().getSessionByHandle(hSession);
		if(session==null){
			throw new PKCS11Error(RETURN_TYPE.SESSION_HANDLE_INVALID);
		}
		if(session.signHelper==null){
			session.signHelper = new SignHelper(hSession, pMechanism, hKey);
		}else{
			throw new PKCS11Error(RETURN_TYPE.GENERAL_ERROR);
		}
	} catch (PKCS11Error e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getCode();
	}
	  
	  return RETURN_TYPE.OK.swigValue();
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
  public static long C_UnwrapKey(long hSession, CK_MECHANISM pMechanism, long hUnwrappingKey, byte[] pWrappedKey, long ulWrappedKeyLen, CK_ATTRIBUTE[] pTemplate, long ulAttributeCount, CK_ULONG_JPTR phKey) {
		Session session;
		try {
			session = getRM().getSessionByHandle(hSession);
			ServerSession sSession =  session.getSlot().getServersession();
			
			long hKey = sSession.unwrapKey(pMechanism, hUnwrappingKey, pWrappedKey, ulWrappedKeyLen, pTemplate, ulAttributeCount, phKey);
			phKey.assign(hKey);

		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getCode();
		}
	  
	  
	  
	  
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
  public static long C_WrapKey(long hSession, CK_MECHANISM pMechanism, long hWrappingKey, long hKey, CK_BYTE_ARRAY pWrappedKey, CK_ULONG_JPTR pulWrappedKeyLen) {
	  try {
		Session session = getRM().getSessionByHandle(hSession);
		
		
		ServerSession sSession =  session.getSlot().getServersession();
		try {
			sSession.wrapKey(pMechanism, hWrappingKey, hKey);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PKCS11Error(RETURN_TYPE.DEVICE_ERROR);
		}
		
	} catch (PKCS11Error e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getCode();
	}
	  
	  
	  
	  
	  return RETURN_TYPE.OK.swigValue();
  }
  

}
