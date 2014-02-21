package objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import pkcs11.PKCS11Error;
import pkcs11.Util;
import proxys.ATTRIBUTE_TYPE;
import proxys.CK_BYTE_ARRAY;
import proxys.CK_MECHANISM;
import proxys.CK_MECHANISM_INFO;
import proxys.CK_RSA_PKCS_PSS_PARAMS;
import proxys.MECHANISM_TYPES;
import proxys.RETURN_TYPE;
import proxys.SWIGTYPE_p_void;
import proxys.pkcs11Constants;

public class MECHANISM extends CK_MECHANISM{
	public static class MechanismInfo extends Util.Capabilities{

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
		public boolean isHw(){
			return Util.isFlagSet(flags, pkcs11Constants.CKF_HW);
		}
		public void writeInto(CK_MECHANISM_INFO info){
			info.setUlMaxKeySize(maxKeyLen);
			info.setUlMinKeySize(minKeyLen);
			info.setFlags(flags);
		}
	}
	/*** type of Mechanism parameter for all Mechanisms in PKCS11 ***/
	private static Map<MECHANISM_TYPES,Class<?>> mechanism_types = new HashMap<>();
	static{
		mechanism_types.put(MECHANISM_TYPES.RSA_PKCS, void.class);//PKCS #1 v1.5 RSA mechanism
//		mechanism_types.put(MECHANISM_TYPE.RSA_PKCS_OAEP,class);
		mechanism_types.put(MECHANISM_TYPES.SHA1_RSA_PKCS, void.class);//PKCS #1 v1.5
		mechanism_types.put(MECHANISM_TYPES.SHA1_RSA_PKCS_PSS,CK_RSA_PKCS_PSS_PARAMS.class);
		mechanism_types.put(MECHANISM_TYPES.SHA256_RSA_PKCS_PSS,CK_RSA_PKCS_PSS_PARAMS.class);
	}
	
	
	private MECHANISM_TYPES type;
	private Class<?> datatype;
	private CK_BYTE_ARRAY cdata;
	
	protected Class<?> datatypeof(MECHANISM_TYPES type) throws PKCS11Error{
		Class<?> datatype = mechanism_types.get(type);
		if(datatype == null){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
		}
		return datatype;
	}
	protected void setPParameter(long CPtr){
		setPParameter(new SWIGTYPE_p_void(CPtr, false));
	}
	protected void newCData(int len){
		cdata = new CK_BYTE_ARRAY(len);
		setPParameter(cdata.getCPtr());
		setUlParameterLen(len);
	}
	public boolean isCDataNULL(){
		SWIGTYPE_p_void p = getPParameter();
		if(p == null){
			return true;
		}
		return false;
	}
	public MECHANISM(long cPtr, boolean cMemoryOwn) throws PKCS11Error{
		super(cPtr,cMemoryOwn);
		if(cPtr == 0)
			return;
		this.type = MECHANISM_TYPES.swigToEnum((int) super.getMechanism());		
		this.cdata = new CK_BYTE_ARRAY(super.getPParameter().getCPtr(),false);
		this.datatype = datatypeof(type);
	}
	public <T extends StructSizeBase> MECHANISM(MECHANISM_TYPES type, T val) throws PKCS11Error {
		super();
		this.type = type;
		setMechanism(type.swigValue());
		this.datatype = datatypeof(type);
		if(val == null || !datatype.equals(val.getClass())){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_PARAM_INVALID);
		}
		newCData((int) val.getSize());
		Util.copy(new CK_BYTE_ARRAY(val.getCPtr(),false),cdata,(int)val.getSize());
	}
	
	public MECHANISM_TYPES getType(){
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
		if(!datatype.equals(req_type) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_PARAM_INVALID);
		}
		Constructor<T> constructor;
		try {
			constructor = req_type.getDeclaredConstructor( long.class, boolean.class );
			return constructor.newInstance(cdata.getCPtr(),false);
		} catch (NoSuchMethodException|IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_PARAM_INVALID);
		}
	}
}
