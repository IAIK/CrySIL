package objects;


import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import pkcs11.PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;

public class Attribute {
	public static byte[] getDataAsByteArray(CK_ATTRIBUTE attribute){
		CK_BYTE_ARRAY array = new CK_BYTE_ARRAY(attribute.getPValue().getCPtr(), false); //TODO: geht das? 
		byte[] a = new byte[ (int) attribute.getUlValueLen()];
		for(int i =0; i< attribute.getUlValueLen(); i++){
			a[i] = (byte) array.getitem(i);
		}
		return a;
	}
	public static byte[] getDataAsByteArray(CK_BYTE_ARRAY data, int len){
		byte[] a = new byte[ len];
		for(int i =0; i< len; i++){
			a[i] = (byte) data.getitem(i);
		}
		return a;
	}
	
	private ATTRIBUTE_TYPE type;
	private Class<?> datatype;
	private long length;
	private CK_BYTE_ARRAY cdata;
	private byte[] data;
	
	
	private static Map<ATTRIBUTE_TYPE,Class<?>> attribute_types = new HashMap<>();
	static{
		attribute_types.put(ATTRIBUTE_TYPE.CLASS, OBJECT_CLASS.class);
		attribute_types.put(ATTRIBUTE_TYPE.TOKEN,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.PRIVATE,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.LABEL,String.class);
		attribute_types.put(ATTRIBUTE_TYPE.APPLICATION,String.class);
		attribute_types.put(ATTRIBUTE_TYPE.VALUE,CK_BYTE_ARRAY.class);//BER encoding
		attribute_types.put(ATTRIBUTE_TYPE.CERTIFICATE_TYPE,CERT_TYPE.class);
		attribute_types.put(ATTRIBUTE_TYPE.ISSUER,CK_BYTE_ARRAY.class);//WTLS encoding
		attribute_types.put(ATTRIBUTE_TYPE.SERIAL_NUMBER,CK_BYTE_ARRAY.class);//DER encoding
		attribute_types.put(ATTRIBUTE_TYPE.KEY_TYPE,KEY_TYP.class);
		attribute_types.put(ATTRIBUTE_TYPE.SUBJECT,CK_BYTE_ARRAY.class);//WTLS encoding
		attribute_types.put(ATTRIBUTE_TYPE.ID,CK_BYTE_ARRAY.class);
		attribute_types.put(ATTRIBUTE_TYPE.SENSITIVE,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.ENCRYPT,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.DECRYPT,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.WRAP,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.UNWRAP,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.SIGN,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.SIGN_RECOVER,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.VERIFY,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.VERIFY_RECOVER,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.DERIVE,Boolean.class);
		//		  attribute_types.put(ATTRIBUTE_TYPE.START_DATE
		//		  attribute_types.put(ATTRIBUTE_TYPE.END_DATE
		//		  attribute_types.put(ATTRIBUTE_TYPE.MODULUS
		//		  attribute_types.put(ATTRIBUTE_TYPE.MODULUS_BITS
		//		  attribute_types.put(ATTRIBUTE_TYPE.PUBLIC_EXPONENT
		//		  attribute_types.put(ATTRIBUTE_TYPE.PRIVATE_EXPONENT
		//		  attribute_types.put(ATTRIBUTE_TYPE.PRIME_1
		//		  attribute_types.put(ATTRIBUTE_TYPE.PRIME_2
		//		  attribute_types.put(ATTRIBUTE_TYPE.EXPONENT_1
		//		  attribute_types.put(ATTRIBUTE_TYPE.EXPONENT_2
		//		  attribute_types.put(ATTRIBUTE_TYPE.COEFFICIENT
		//		  attribute_types.put(ATTRIBUTE_TYPE.PRIME
		//		  attribute_types.put(ATTRIBUTE_TYPE.SUBPRIME
		//		  attribute_types.put(ATTRIBUTE_TYPE.BASE
		//		  attribute_types.put(ATTRIBUTE_TYPE.VALUE_BITS
		//		  attribute_types.put(ATTRIBUTE_TYPE.VALUE_LEN
		attribute_types.put(ATTRIBUTE_TYPE.EXTRACTABLE,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.LOCAL,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.NEVER_EXTRACTABLE,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.ALWAYS_SENSITIVE,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.MODIFIABLE,Boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.VENDOR_DEFINED,CK_BYTE_ARRAY.class);
	}
	
	public Attribute(CK_ATTRIBUTE attr){
		this.type = ATTRIBUTE_TYPE.swigToEnum((int) attr.getType());
		this.datatype = Attribute.attribute_types.get(this.type);
		this.cdata = new CK_BYTE_ARRAY(attr.getPValue().getCPtr(), false);
		this.length = attr.getUlValueLen();
		this.data = getDataAsByteArray(cdata,(int) length);
	}
	
	public Attribute(ATTRIBUTE_TYPE type, byte[] val) {
		this.type = type;
		this.data = val;
		this.datatype = Attribute.attribute_types.get(type);
	}

	public ATTRIBUTE_TYPE getType(){
		return type;
	}

	public boolean getAsBoolean() throws PKCS11Error{
		if(!datatype.equals(boolean.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		Boolean res = null;
		if(cdata.getitem(0) == 1){
			res = new Boolean(true);
		}else{
			res = new Boolean(false);
		}
		return res;
	}
	public long getAsLong() throws PKCS11Error{
		if(!datatype.equals(long.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		ByteBuffer buf = ByteBuffer.wrap(data);
		return buf.getLong();
	}
	public byte[] getAsByteArray() throws PKCS11Error{
		if(!datatype.equals(Byte.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		return data;
	}
	public <T> T getAsPKCS11Struct() throws PKCS11Error, InstantiationException, IllegalAccessException{
		Class<T> req_type;
		if(!datatype.equals(req_type)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		return (T) datatype.newInstance(cptr,false);
	}
	public <T> T getAsSwigObject() throws PKCS11Error, InstantiationException, IllegalAccessException{
		if(datatype.equals(CK_BYTE_ARRAY.class)){
			return cdata;
		}else if(datatype.equals(EnumBase.class)){
			return datatype.newInstance(getAsLong());
		}else if(datatype.equals(StructBase.class)){
			return datatype.newInstance(cptr,false);
		}else{
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
//			throw new PKCS11Warning();
		}
	}
	public Object getAsObject() throws PKCS11Error, InstantiationException, IllegalAccessException{
		if(datatype.equals(CK_BYTE_ARRAY.class)){
			return cdata;
		}else if(datatype.equals(EnumBase.class)){
			return datatype.newInstance(getAsLong());
		}else if(datatype.equals(StructBase.class)){
			return datatype.newInstance(cptr,false);
		}else{
			throw new PKCS11Warning();
		}
	}
	
	public void set(boolean v) throws PKCS11Error{
		if(!datatype.equals(boolean.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		if(v)
			data[0] = 1;
		else
			data[1] = 0;
	}
	public void set(long v) throws PKCS11Error{
		if(!datatype.equals(long.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		ByteBuffer buf = ByteBuffer.wrap(data);
		buf.putLong(v);
	}
	public void set(byte[] v) throws PKCS11Error{
		if(!datatype.equals(long.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		data = v;
	}
	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null) {
			return false;
		}
		if (!(obj instanceof Attribute)) {
			return false; // different class
		}
		Attribute other = (Attribute) obj;
		return (this.type.equals(other.type) && this.data.equals(other.data) && this.datatype.equals(other.datatype));
	}
	@Override
	public int hashCode(){
		return type.swigValue();
	}
}












