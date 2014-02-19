package objects;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import pkcs11.PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.CK_KEY_WRAP_SET_OAEP_PARAMS;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;

public class ObjectBuilder {
	

	private static Map<ATTRIBUTE_TYPE,ATTRIBUTE> defaultKey_template = new HashMap<>();

	static{		
		byte[] enum_value = new byte[8];
		byte[] bool_value = new byte[1];
		ByteBuffer.wrap(enum_value).putLong(OBJECT_CLASS.PUBLIC_KEY.swigValue());
		defaultKey_template.put(ATTRIBUTE_TYPE.CLASS, new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,enum_value));
		bool_value[0] = 1;
		defaultKey_template.put(ATTRIBUTE_TYPE.EXTRACTABLE, new ATTRIBUTE(ATTRIBUTE_TYPE.EXTRACTABLE,bool_value));
		bool_value[0] = 0;
		defaultKey_template.put(ATTRIBUTE_TYPE.SENSITIVE, new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,bool_value));
		bool_value[0] = 0;
		defaultKey_template.put(ATTRIBUTE_TYPE.TOKEN, new ATTRIBUTE(ATTRIBUTE_TYPE.TOKEN,bool_value));
		bool_value[0] = 1;
		defaultKey_template.put(ATTRIBUTE_TYPE.MODIFIABLE, new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,bool_value));
		bool_value[0] = 0;
		defaultKey_template.put(ATTRIBUTE_TYPE.DERIVE, new ATTRIBUTE(ATTRIBUTE_TYPE.DERIVE,bool_value));

		defaultKey_template.put(ATTRIBUTE_TYPE.KEY_TYPE, new ATTRIBUTE(ATTRIBUTE_TYPE.KEY_TYPE,bool_value));
	}
	

	public static PKCS11Object createFromTemplate(ATTRIBUTE[] template) throws PKCS11Error{
		return new PKCS11Object(template);
		
		
		//set default attribute values?
		
//		for(Attribute attr : attributes){
//			if(attr.getType().equals(ATTRIBUTE_TYPE.CLASS)){
//				OBJECT_CLASS obj_class = null;
//				obj_class = attr.getAsSwig(OBJECT_CLASS.class);
//
//				if(obj_class.equals(OBJECT_CLASS.PRIVATE_KEY)){
//					 //private key template
//				 }else if(obj_class.equals(OBJECT_CLASS.PUBLIC_KEY)){
//					 //bool
//				 }else if(obj_class.equals(OBJECT_CLASS.CERTIFICATE)){
//					 
//				 }else if(obj_class.equals(OBJECT_CLASS.SECRET_KEY)){
//					 //byte array
//				 }
//			}
//		}
//		return null;
	}
	public static PKCS11Object createFromTemplate(CK_ATTRIBUTE[] template) throws PKCS11Error{
		ATTRIBUTE[] attributes = ATTRIBUTE.toAttributeArray(template);
		return createFromTemplate(attributes);
	}
}
