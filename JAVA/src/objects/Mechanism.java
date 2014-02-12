package objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import pkcs11.PKCS11Error;
import pkcs11.Util;
import proxys.CK_BYTE_ARRAY;
import proxys.CK_MECHANISM;
import proxys.CK_MECHANISM_INFO;
import proxys.MECHANISM_TYPE;
import proxys.RETURN_TYPE;
import proxys.pkcs11Constants;

//public class Mechanism extends CK_MECHANISM{
public class Mechanism {
	public static class MechanismInfo{
		private long flags = Util.initFlags;
		private long minKeyLen = 0;
		private long maxKeyLen = 0;
		
		public MechanismInfo(long minkeylen,long maxkeylen){
			minKeyLen = minkeylen;
			maxKeyLen = maxkeylen;	
		}
		public MechanismInfo(){
			minKeyLen = Long.MIN_VALUE;
			maxKeyLen = Long.MAX_VALUE;	
		}
		public MechanismInfo hw(){
			flags = Util.setFlag(flags, pkcs11Constants.CKF_HW);
			return this;
		}
		public MechanismInfo sign_verify(){
			flags = Util.setFlag(flags, pkcs11Constants.CKF_SIGN);
			flags = Util.setFlag(flags, pkcs11Constants.CKF_VERIFY);
			return this;
		}
		public MechanismInfo encrypt_decrypt(){
			flags = Util.setFlag(flags, pkcs11Constants.CKF_ENCRYPT);
			flags = Util.setFlag(flags, pkcs11Constants.CKF_DECRYPT);
			return this;
		}
		public MechanismInfo wrap(){
			flags = Util.setFlag(flags, pkcs11Constants.CKF_WRAP);
			return this;
		}
		public MechanismInfo unwrap(){
			flags = Util.setFlag(flags, pkcs11Constants.CKF_UNWRAP);
			return this;
		}
		public void writeInto(CK_MECHANISM_INFO info){
			info.setUlMaxKeySize(maxKeyLen);
			info.setUlMinKeySize(minKeyLen);
			info.setFlags(flags);
		}
	}
	/*** type of Mechanism parameter for all Mechanisms in PKCS11 ***/
	private static Map<MECHANISM_TYPE,Class<?>> mechanism_types = new HashMap<>();
	static{
		mechanism_types.put(MECHANISM_TYPE.RSA_PKCS, void.class);//PKCS #1 v1.5 RSA mechanism
//		mechanism_types.put(MECHANISM_TYPE.RSA_PKCS_OAEP,class);
		mechanism_types.put(MECHANISM_TYPE.SHA1_RSA_PKCS, void.class);//PKCS #1 v1.5
//		mechanism_types.put(MECHANISM_TYPE.SHA1_RSA_PKCS_PSS,CK_RSA_PKCS_PSS_PARAMS.class);
//		mechanism_types.put(MECHANISM_TYPE.SHA256_RSA_PKCS_PSS,CK_RSA_PKCS_PSS_PARAMS.class);
	}
	
	
	private MECHANISM_TYPE type;
	private Class<?> datatype;
	private long length;
	private CK_BYTE_ARRAY cdata;
	private byte[] data;
	
	public Mechanism(CK_MECHANISM mech) throws PKCS11Error{
		this.type = MECHANISM_TYPE.swigToEnum((int) mech.getMechanism());
		this.length = mech.getUlParameterLen();
		this.cdata = new CK_BYTE_ARRAY(mech.getPParameter().getCPtr(),false);
		this.data = Util.getDataAsByteArray(cdata, (int) length);
		this.datatype = mechanism_types.get(type);
		if(this.datatype == null){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
		}
	}
	public MECHANISM_TYPE getType(){
		return type;
	}
	public boolean hasParameters(){
		if(datatype.equals(void.class)){
			return false;
		}else{
			return true;
		}
	}
	private <T extends StructBase> T getAsSwigStruct(Class<T> req_type) throws PKCS11Error{
		if(!datatype.equals(req_type)){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_PARAM_INVALID);
		}
		Constructor<T> constructor;
		try {
			constructor = req_type.getDeclaredConstructor( long.class, boolean.class );
			return constructor.newInstance(cdata.getCPtr(),false);
		} catch (NoSuchMethodException|IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
}
