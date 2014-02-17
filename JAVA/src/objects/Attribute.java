package objects;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import pkcs11.PKCS11Error;
import pkcs11.Util;
import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;


/** 2 möglichkeiten 
 * 		Attribute hält c Daten sofern vorhanden immer auf aktuellem stand
 * 		eigene methode to CK_ATTRIBUTE in der ein neues CK_ATTRIBUTE aus cdata ptr wenn vorhanden zusammengebaut wird
 * **/
public class Attribute {

	private ATTRIBUTE_TYPE type;
	private Class<?> datatype;
	private byte[] data;
	
	private boolean ro = false;
	private boolean sensitive = false;
	
	
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
	
	public static Attribute[] toAttributeArray(CK_ATTRIBUTE[] template){
		Attribute[] res = new Attribute[template.length];
		for(int i=0;i<template.length;i++){
			res[i] = new Attribute(template[i]);
		}
		return res;
	}
	
	public Attribute(CK_ATTRIBUTE attr){
		this.type = ATTRIBUTE_TYPE.swigToEnum((int) attr.getType());
		this.datatype = Attribute.attribute_types.get(this.type);
		CK_BYTE_ARRAY cdata = new CK_BYTE_ARRAY(attr.getPValue().getCPtr(), false);
		long length = attr.getUlValueLen();
		this.data = Util.getCDataAsByteArray(cdata,(int) length);
	}
	
	public Attribute(ATTRIBUTE_TYPE type, byte[] val) {
		this.type = type;
		if(val == null)
			this.data = new byte[0];
		else
			this.data = val;
		this.datatype = Attribute.attribute_types.get(type);
	}
	public <T extends EnumBase> Attribute(ATTRIBUTE_TYPE type, T val) {
		this.type = type;
		this.datatype = Attribute.attribute_types.get(type);
		byte[] enum_value = new byte[8];
		ByteBuffer.wrap(enum_value).putLong(val.swigValue());
		this.data = enum_value;
	}

	public ATTRIBUTE_TYPE getType(){
		return type;
	}
	public byte[] getRawData(){
		return data;
	}
	public void setRawData(byte[] data){
		this.data = data;

	}
	public long getCData(CK_BYTE_ARRAY out_cdata){
		return Util.getByteArrayAsCData(data, out_cdata);
	}
	public void writeInto(CK_ATTRIBUTE out_cdata) throws PKCS11Error{
		if(out_cdata.isNullPtr() || out_cdata.getUlValueLen() < data.length || out_cdata.getPValue().isNullPtr()){
			throw new PKCS11Error(RETURN_TYPE.ARGUMENTS_BAD);
		}
		out_cdata.setType(type.swigValue());
		out_cdata.setUlValueLen(data.length);
		CK_BYTE_ARRAY out_payload = new CK_BYTE_ARRAY(out_cdata.getPValue().getCPtr(), false);
		Util.getByteArrayAsCData(data, out_payload);
	}

	public boolean getAsBoolean() throws PKCS11Error{
		if(!datatype.equals(boolean.class) || data.length < 1){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		if(data[0] == 1){
			return true;
		}else{
			return false;
		}
	}
	public long getAsLong() throws PKCS11Error{
		if(!datatype.equals(long.class) || data.length < 8){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		ByteBuffer buf = ByteBuffer.wrap(data);
		return buf.getLong();
	}
	public byte[] getAsByteArray() throws PKCS11Error{
		System.out.println("getting data as byte array, but dyata is: "+ datatype);
		if(!datatype.equals(Byte.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		return data;
	}
	
//	private <T extends StructBase> T getAsSwigStruct(Class<T> req_type) throws PKCS11Error{
//		if(!datatype.equals(req_type)){
//			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
//		}
//		Constructor<T> constructor;
//		try {
//			constructor = req_type.getDeclaredConstructor( long.class, boolean.class );
//			return constructor.newInstance(cdata.getCPtr(),false);
//		} catch (NoSuchMethodException|IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//			return null;
//		}
//	}
	public <T extends EnumBase> T getAsSwig(Class<T> req_type) throws PKCS11Error{
		if(!datatype.equals(req_type)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		try {
			return (T) req_type.getMethod("swigToEnum", int.class).invoke(null, getAsLong());
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			return null;
		}
	}
//	public <T> T getAsSwig(Class<T> req_type) throws PKCS11Error{
//		if(!datatype.equals(req_type)){
//			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
//		}
//		if(req_type.equals(CK_BYTE_ARRAY.class)){
//			return (T) cdata;
//		}else if(EnumBase.class.isAssignableFrom(req_type)){
//			return (T) getAsSwigEnum((Class<? extends EnumBase>) req_type);
//		}else if(StructBase.class.isAssignableFrom(req_type)){
//			return (T) getAsSwigStruct((Class<? extends StructBase>) req_type);
//		}else{
//			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
////			throw new PKCS11Warning();
//		}
//	}
//	public Object getAsObject() throws PKCS11Error {
//		if(datatype.equals(CK_BYTE_ARRAY.class)){
//			return cdata;
//		}else if(EnumBase.class.isAssignableFrom(datatype)){
//			try {
//				return datatype.getMethod("swigToEnum", int.class).invoke(null, getAsLong());
//			} catch (IllegalAccessException | IllegalArgumentException
//					| InvocationTargetException | NoSuchMethodException
//					| SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return null;
//			}
//		}else if(StructBase.class.isAssignableFrom(datatype)){
//			Constructor<?> constructor;
//			try {
//				constructor = datatype.getDeclaredConstructor( long.class, boolean.class );
//				return constructor.newInstance(cdata.getCPtr(),false);
//			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return null;
//			}
//		}else{
//			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
////			throw new PKCS11Warning();
//		}
//	}
	
	public <T extends EnumBase> void setSwig(T v) throws PKCS11Error {
		if(v == null || !datatype.equals(v.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		setLong((long) v.swigValue());
	}
//	public <T extends StructBase> void setSwig(T v) throws PKCS11Error {
//		if(v == null || !datatype.equals(v.getClass())){
//			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
//		}
//		CK_BYTE_ARRAY cdata = new CK_BYTE_ARRAY(v.getCPtr(), false);
//		if(cdata.isNullPtr()){
//			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
//		}
//		this.data = Util.getCDataAsByteArray(cdata, sizeof(Struct));
//	}
	public void setBoolean(boolean v) throws PKCS11Error{
		if(!datatype.equals(boolean.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		if(v)
			data[0] = 1;
		else
			data[1] = 0;
	}
	public void setLong(long v) throws PKCS11Error{
		if(!datatype.equals(long.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		ByteBuffer buf = ByteBuffer.wrap(data);
		buf.putLong(v);
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












