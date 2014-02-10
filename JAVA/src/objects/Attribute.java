package objects;


import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import pkcs11.PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
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
	
	private ATTRIBUTE_TYPE type;
	private byte[] data;
	private long length;
	private CK_BYTE_ARRAY cdata;
	private Class<?> datatype;
	private static Map<ATTRIBUTE_TYPE,Class<?>> attribute_types = new HashMap<>();

	public ATTRIBUTE_TYPE getType(){
		return type;
	}
	
	public Attribute(CK_ATTRIBUTE attr,Class<?> datatype){
		this.type = ATTRIBUTE_TYPE.swigToEnum((int) attr.getType());
		this.datatype = Attribute.attribute_types.get(this.type);
		
		this.data = getDataAsByteArray(attr);
		this.cdata = new CK_BYTE_ARRAY(attr.getPValue().getCPtr(), false); //TODO: geht das? 
		this.length = attr.getUlValueLen();
	}
	
	public Attribute(ATTRIBUTE_TYPE type,Class<?> datatype, byte[] val) {
		this.type = type;
		this.data = val;
		this.datatype = datatype;
	}
	
	public boolean getAsBoolean() throws PKCS11Error{
		if(!datatype.equals(Boolean.class)){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		}
		Boolean res = null;
		if(data[0] == 1){
			res = new Boolean(true);
		}else{
			res = new Boolean(false);
		}
		return res;
	}
	public long getAsLong() throws PKCS11Error{
		if(!datatype.equals(Long.class)){
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
	public Object getAsObject() throws PKCS11Error, InstantiationException, IllegalAccessException{
		if(datatype.equals(CK_BYTE_ARRAY.class)){
			return cdata;
		}else if(datatype.equals(EnumBase.class)){
			return datatype.newInstance(getAsLong());
		}else if(datatype.equals(StructBase.class)){
			return datatype.newInstance(cptr,false);
		}else{
			return null;
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












