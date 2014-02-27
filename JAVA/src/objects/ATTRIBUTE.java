package objects;

import java.nio.charset.StandardCharsets;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pkcs11.PKCS11Error;
import pkcs11.Util;
import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.CK_BYTE_ARRAY;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;
import proxys.SWIGTYPE_p_void;


public class ATTRIBUTE extends proxys.CK_ATTRIBUTE {

	private ATTRIBUTE_TYPE type;
	private Class<?> datatype;
	private CK_BYTE_ARRAY cdata;
	
	static Map<ATTRIBUTE_TYPE,Class<?>> attribute_types = new HashMap<>();
	static{
		attribute_types.put(ATTRIBUTE_TYPE.CLASS, OBJECT_CLASS.class);
		attribute_types.put(ATTRIBUTE_TYPE.TOKEN,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.PRIVATE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.LABEL,String.class);
		attribute_types.put(ATTRIBUTE_TYPE.APPLICATION,String.class);
		attribute_types.put(ATTRIBUTE_TYPE.VALUE,CK_BYTE_ARRAY.class);//BER encoding
		attribute_types.put(ATTRIBUTE_TYPE.CERTIFICATE_TYPE,CERT_TYPE.class);
		attribute_types.put(ATTRIBUTE_TYPE.ISSUER,CK_BYTE_ARRAY.class);//WTLS encoding
		attribute_types.put(ATTRIBUTE_TYPE.SERIAL_NUMBER,CK_BYTE_ARRAY.class);//DER encoding
		attribute_types.put(ATTRIBUTE_TYPE.KEY_TYPE,KEY_TYP.class);
		attribute_types.put(ATTRIBUTE_TYPE.SUBJECT,String.class);//WTLS encoding
		attribute_types.put(ATTRIBUTE_TYPE.ID,CK_BYTE_ARRAY.class);
		attribute_types.put(ATTRIBUTE_TYPE.SENSITIVE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.ENCRYPT,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.DECRYPT,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.WRAP,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.UNWRAP,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.SIGN,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.SIGN_RECOVER,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.VERIFY,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.VERIFY_RECOVER,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.DERIVE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.KEY_GEN_MECHANISM,CK_BYTE_ARRAY.class);
		//		  attribute_types.put(ATTRIBUTE_TYPE.START_DATE
		//		  attribute_types.put(ATTRIBUTE_TYPE.END_DATE
		attribute_types.put(ATTRIBUTE_TYPE.MODULUS,CK_BYTE_ARRAY.class);
		attribute_types.put(ATTRIBUTE_TYPE.MODULUS_BITS,long.class);
		attribute_types.put(ATTRIBUTE_TYPE.PUBLIC_EXPONENT,CK_BYTE_ARRAY.class);
		attribute_types.put(ATTRIBUTE_TYPE.PRIVATE_EXPONENT,CK_BYTE_ARRAY.class);
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
		attribute_types.put(ATTRIBUTE_TYPE.EXTRACTABLE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.LOCAL,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.NEVER_EXTRACTABLE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.ALWAYS_SENSITIVE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.ALWAYS_AUTHENTICATE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.MODIFIABLE,boolean.class);
		attribute_types.put(ATTRIBUTE_TYPE.VENDOR_DEFINED,CK_BYTE_ARRAY.class);
	}
	public static ATTRIBUTE find(ArrayList<ATTRIBUTE> template, ATTRIBUTE_TYPE type){
		for(ATTRIBUTE attr : template){
			if(attr.getTypeEnum().equals(ATTRIBUTE_TYPE.CLASS)){
				return attr;
			}
		}
		return null;
	}
	public static ATTRIBUTE[] clone(ATTRIBUTE[] template) throws PKCS11Error{
		ATTRIBUTE[] clone = new ATTRIBUTE[template.length];
		for(int i=0;i<template.length;i++){
			clone[i] = template[i].createClone();
		}
		return clone;
	}
	public static ATTRIBUTE clone(ATTRIBUTE attr) throws PKCS11Error{
		return attr.createClone();
	}
// local Helpers	
	protected Class<?> datatypeof(ATTRIBUTE_TYPE type) throws PKCS11Error{
		Class<?> datatype = attribute_types.get(type);
		if(datatype == null){
			if(type.swigValue() >= ATTRIBUTE_TYPE.VENDOR_DEFINED.swigValue())
				return void.class;
			else
				throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_TYPE_INVALID);
		}
		return datatype;
	}
	protected void setPValue(long CPtr){
		super.setPValue(new SWIGTYPE_p_void(CPtr,false));
	}
	public int getDataLength(){
		return (int) getUlValueLen();
	}
	public void setDataLength(int len){
		setUlValueLen(len);
	}
	protected void setNewCData(long len){
		setCData(new CK_BYTE_ARRAY((int) len),len);
	}
	protected void setCData(CK_BYTE_ARRAY data,long len){
		cdata = data;
		setPValue(data.getCPtr());
		setDataLength((int) len);
	}
	protected void setCData(long CPtr,long len){
		cdata = new CK_BYTE_ARRAY(CPtr,false);
		setPValue(CPtr);
		setDataLength((int) len);
	}
	protected long getCDataPtr(){
		SWIGTYPE_p_void p = getPValue();
		if(p == null){
			return 0;
		}
		return p.getCPtr();
	}
	public boolean isCDataNULL(){
		return (getCDataPtr() == 0);
	}
	protected CK_BYTE_ARRAY getCData(){
		return cdata;
	}
// end	
	
	public ATTRIBUTE(long cPtr, boolean cMemoryOwn) throws PKCS11Error{
		super(cPtr,cMemoryOwn);
		if(cPtr == 0)
			throw new PKCS11Error(RETURN_TYPE.ARGUMENTS_BAD);

		cdata = new CK_BYTE_ARRAY(getCDataPtr(),false);
		System.err.println("Create AttrType: "+getType());
		this.type = ATTRIBUTE_TYPE.swigToEnum((int) getType());
		this.datatype = datatypeof(this.type);
	}

	public ATTRIBUTE(ATTRIBUTE_TYPE type, byte[] val) throws PKCS11Error {
		super();
		this.type = type;
		setType(type.swigValue());
		this.datatype = datatypeof(type);
		setNewCData(val.length);//alloc cmem
		Util.copyByteArrayToCData(val, cdata);//copy to cmem
	}
	public ATTRIBUTE(ATTRIBUTE_TYPE type, boolean val) throws PKCS11Error {
		super();
		this.type = type;
		setType(type.swigValue());
		this.datatype = datatypeof(type);
		setNewCData(1);//alloc cmem
		copyFromBoolean(val);
	}
	public ATTRIBUTE(ATTRIBUTE_TYPE type, long val) throws PKCS11Error {
		super();
		this.type = type;
		setType(type.swigValue());
		this.datatype = datatypeof(type);
		setNewCData(8);//alloc cmem
		copyFromLong(val);
	}
	public ATTRIBUTE(ATTRIBUTE_TYPE type, String val) throws PKCS11Error {
		super();
		this.type = type;
		setType(type.swigValue());
		this.datatype = datatypeof(type);
		setNewCData(val.length());//alloc cmem
		copyFromString(val);
	}

	public <T extends EnumBase> ATTRIBUTE(ATTRIBUTE_TYPE type, T val) throws PKCS11Error {
		super();
		this.type = type;
		setType(type.swigValue());
		this.datatype = datatypeof(type);
		if(val == null || !datatype.equals(val.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		setNewCData(8);
		byte[] enum_value = new byte[8];
		ByteBuffer.wrap(enum_value).order(ByteOrder.LITTLE_ENDIAN).putLong(val.swigValue());
		Util.copyByteArrayToCData(enum_value, cdata);
	}
	public <T extends StructSizeBase> ATTRIBUTE(ATTRIBUTE_TYPE type, T val) throws PKCS11Error {
		super();
		this.type = type;
		setType(type.swigValue());
		this.datatype = datatypeof(type);
		if(val == null || !datatype.equals(val.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		setNewCData(val.getSize());
		Util.copy(new CK_BYTE_ARRAY(val.getCPtr(),false),cdata,(int)val.getSize());
	}
	
	public ATTRIBUTE(ATTRIBUTE_TYPE type) throws PKCS11Error {
		super();
		this.type = type;
		setType(type.swigValue());
		this.datatype = datatypeof(type);
		setCData(0, 0);
	}
	
	public ATTRIBUTE createClone() throws PKCS11Error{
			ATTRIBUTE clone = new ATTRIBUTE(type);
			clone.setNewCData(getDataLength());
			Util.copy(cdata, clone.cdata, getDataLength());
			return clone;
	}
	public void copyDataTo(ATTRIBUTE clone) throws PKCS11Error{
		if(!datatype.equals(clone.datatype) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		if(clone.isCDataNULL() || clone.getDataLength() < getDataLength()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		clone.setDataLength(getDataLength());
		Util.copy(cdata, clone.cdata, getDataLength());
	}
	public ATTRIBUTE_TYPE getTypeEnum(){
		return type;
	}
	public boolean copyToBoolean() throws PKCS11Error{
		if(!datatype.equals(boolean.class) || isCDataNULL() || getDataLength() < 1){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		if(getCData().getitem(0) == 1){
			return true;
		}else{
			return false;
		}
	}
	protected long copyRawToLong() throws PKCS11Error{
		if(isCDataNULL() || getDataLength() < 8){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		byte[] data = Util.copyCDataToByteArray(getCData(), 8);
		ByteBuffer buf = ByteBuffer.wrap(data);
		long ret = buf.order(ByteOrder.LITTLE_ENDIAN).getLong();
		return ret;
	}
	public long copyToLong() throws PKCS11Error{
		if(!datatype.equals(long.class) || isCDataNULL() || getDataLength() < 8){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		byte[] data = Util.copyCDataToByteArray(getCData(), 8);
		ByteBuffer buf = ByteBuffer.wrap(data);
		return buf.order(ByteOrder.LITTLE_ENDIAN).getLong();
	}
	public byte[] copyToByteArray() throws PKCS11Error{
		System.err.println("getting data as byte array, but dyata is: "+ datatype);
		if(!datatype.equals(Byte.class) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		return Util.copyCDataToByteArray(getCData(), getDataLength());
	}
	
	
	
	public <T extends StructBase> T getAsSwigStruct(Class<T> req_type) throws PKCS11Error{
		if(!datatype.equals(req_type) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		Constructor<T> constructor;
		try {
			constructor = req_type.getDeclaredConstructor( long.class, boolean.class );
			return constructor.newInstance(getCDataPtr(),false);
		} catch (NoSuchMethodException|IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
	}
	public <T extends EnumBase> T copyToSwigEnum(Class<T> req_type) throws PKCS11Error{
		if(!datatype.equals(req_type) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		try {
			long id = copyRawToLong();
			return (T) req_type.getMethod("swigToEnum", int.class).invoke(null, (int) id);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
	}
	public <T extends StructSizeBase> T copyToSwigStruct(Class<T> req_type) throws PKCS11Error{
		if(!datatype.equals(req_type) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		Constructor<T> constructor;
		try {
			constructor = req_type.getDeclaredConstructor();
			T copy_dst =  constructor.newInstance();
			Util.copy(cdata, new CK_BYTE_ARRAY(copy_dst.getCPtr(), false), (int) copy_dst.getSize());
			return copy_dst;
		} catch (NoSuchMethodException|IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
	}


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
	
	public <T extends EnumBase> void copyFromSwig(T v) throws PKCS11Error {
		if(v == null || !datatype.equals(v.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		copyFromLong((long) v.swigValue());
	}
	
	public <T extends StructSizeBase> void setSwig(T v) throws PKCS11Error {
		if(v == null || !datatype.equals(v.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}

		setCData(v.getCPtr(), v.getSize());
	}
	public <T extends StructSizeBase> void copyFromSwig(T v) throws PKCS11Error {
		if(v == null || !datatype.equals(v.getClass())){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}

		Util.copy(new CK_BYTE_ARRAY(v.getCPtr(), false), cdata, (int) v.getSize());
	}
	
	public void copyFromBoolean(boolean v) throws PKCS11Error{
		if(!datatype.equals(boolean.class) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		if(v)
			getCData().setitem(0, (short) 1);
		else
			getCData().setitem(0, (short) 0);
	}
	public void copyFromLong(long v) throws PKCS11Error{
		if(!datatype.equals(long.class) || isCDataNULL() || getDataLength() < 8){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		byte[] tmp = new byte[8];
		ByteBuffer buf = ByteBuffer.wrap(tmp);
		buf.putLong(v);
		Util.copyByteArrayToCData(tmp, getCData());
	}
	public void copyFromString(String val) throws PKCS11Error {
		if(!datatype.equals(String.class) || isCDataNULL()){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		byte[] tmp;
		tmp = val.getBytes(StandardCharsets.UTF_8);
		Util.copyByteArrayToCData(tmp, getCData());
	}
	public boolean query(ATTRIBUTE query_attr){		
		if(query_attr == null){
			return true;
		}
		if(!this.type.equals(query_attr.type) || !this.datatype.equals(query_attr.datatype)){
			return false;
		}
		if(query_attr.isCDataNULL()){
			return true; //if query attr has no data it is irrelevant
		}
		//proof data
		if(this.isCDataNULL()){
			return false; 
		}
		if(this.getDataLength() < query_attr.getDataLength()){
			return false; //query cannot specify more information than searched attr has
		}
		CK_BYTE_ARRAY other_cdata = query_attr.getCData();
		boolean data_eq = true;
		for(int i=0;i<query_attr.getDataLength();i++){
			if(other_cdata.getitem(i) != this.cdata.getitem(i)){
				data_eq = false;
				break;
			}
		}
		return 	data_eq;	
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
		if(!this.type.equals(other.type) || !this.datatype.equals(other.datatype)){
			return false;
		}
		//proof data
		CK_BYTE_ARRAY other_cdata = other.getCData();
		if(this.getDataLength() != other.getDataLength()){
			return false;
		}
		if((this.isCDataNULL() && !other.isCDataNULL()) || (!this.isCDataNULL() && other.isCDataNULL())){
			return false; // one of them is NULL the other not
		}
		if(this.isCDataNULL() && other.isCDataNULL()){
			return true; //both NULL
		}
		boolean data_eq = true;
		for(int i=0;i<this.getDataLength();i++){
			if(other_cdata.getitem(i) != this.cdata.getitem(i)){
				data_eq = false;
				break;
			}
		}
		return 	data_eq;	
	}
	@Override
	public int hashCode(){
		return type.swigValue();
	}
}
