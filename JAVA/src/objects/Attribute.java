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
import objects.ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;

public class ATTRIBUTE {

	private ATTRIBUTE_TYPE type;
	private Class<?> datatype;
	private byte[] data;
	
	private boolean ro = false;
	private boolean sensitive = false;
	
	

	
//	public static ATTRIBUTE[] toAttributeArray(CK_ATTRIBUTE[] template){
//		ATTRIBUTE[] res = new ATTRIBUTE[template.length];
//		for(int i=0;i<template.length;i++){
//			res[i] = new ATTRIBUTE(template[i]);
//		}
//		return res;
//	}
//	
//	public ATTRIBUTE(CK_ATTRIBUTE attr){
//		this.type = ATTRIBUTE_TYPE.swigToEnum((int) attr.getType());
//		this.datatype = ATTRIBUTE.attribute_types.get(this.type);
//		CK_BYTE_ARRAY cdata = new CK_BYTE_ARRAY(attr.getPValue().getCPtr(), false);
//		long length = attr.getLength();
//		this.data = Util.getCDataAsByteArray(cdata,(int) length);
//	}
	

	
	public <T extends EnumBase> ATTRIBUTE(ATTRIBUTE_TYPE type, T val) throws PKCS11Error {
		this.type = type;
		this.datatype = ATTRIBUTE.attribute_types.get(type);
		if(!datatype.equals(val.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		byte[] enum_value = new byte[8];
		ByteBuffer.wrap(enum_value).putLong(val.swigValue());
		this.data = enum_value;
	}
	public <T extends StructBase> ATTRIBUTE(ATTRIBUTE_TYPE type, T val) throws PKCS11Error {
		this.type = type;
		this.datatype = ATTRIBUTE.attribute_types.get(type);
		if(!datatype.equals(val.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		this.data = Util.getCDataAsByteArray(val.getCPtr(), (int) val.getSize());
	}
	public ATTRIBUTE_TYPE getType(){
		return type;
	}
	public byte[] getRawData(){
		return data;
	}

	public long getCData(CK_BYTE_ARRAY out_cdata){
		return Util.copyByteArrayToCData(data, out_cdata);
	}
	public void writeInto(ATTRIBUTE out_cdata) throws PKCS11Error{
		if(out_cdata.isNullPtr() || out_cdata.getLength() < data.length || out_cdata.getPValue().isNullPtr()){
			throw new PKCS11Error(RETURN_TYPE.ARGUMENTS_BAD);
		}
		out_cdata.setType(type.swigValue());
		out_cdata.setUlValueLen(data.length);
		CK_BYTE_ARRAY out_payload = new CK_BYTE_ARRAY(out_cdata.getPValue().getCPtr(), false);
		Util.copyByteArrayToCData(data, out_payload);
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
	
	private <T extends StructBase> T getAsSwigStruct(Class<T> req_type) throws PKCS11Error{
		if(!datatype.equals(req_type)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		Constructor<T> constructor;
		try {
			constructor = req_type.getDeclaredConstructor( long.class, boolean.class );
			return constructor.newInstance(cdata.getCPtr(),false);
		} catch (NoSuchMethodException|IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
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
	public <T extends StructBase> void setSwig(T v) throws PKCS11Error {
		if(v == null || !datatype.equals(v.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		CK_BYTE_ARRAY cdata = new CK_BYTE_ARRAY(v.getCPtr(), false);
		if(cdata.isNullPtr()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		this.data = Util.getCDataAsByteArray(cdata,(int) v.getSize());
	}
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
		if (!(obj instanceof ATTRIBUTE)) {
			return false; // different class
		}
		ATTRIBUTE other = (ATTRIBUTE) obj;
		return (this.type.equals(other.type) && this.data.equals(other.data) && this.datatype.equals(other.datatype));
	}
	@Override
	public int hashCode(){
		return type.swigValue();
	}
}












