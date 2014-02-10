package objects;

import java.util.HashMap;
import java.util.Map;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import pkcs11.PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;

public class ObjectBuilder {
	
	private static Map<ATTRIBUTE_TYPE,Attribute> skytrust_template = new HashMap<>();
	private static Map<ATTRIBUTE_TYPE,Attribute> defaultKey_template = new HashMap<>();

	public ObjectBuilder(){
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
	
	private static Attribute[] toAttributeArray(CK_ATTRIBUTE[] template){
		Attribute[] res = new Attribute[template.length];
		for(CK_ATTRIBUTE attr:template){
			res[] = new Attribute(attr, attribute_types.get(ATTRIBUTE_TYPE.swigToEnum((int) tmp.getType());));
		}
		return res;
	}
	public static PKCS11Object createFromTemplate(CK_ATTRIBUTE[] template){
		for(CK_ATTRIBUTE tmp : template){
			ATTRIBUTE_TYPE type = ATTRIBUTE_TYPE.swigToEnum((int) tmp.getType());
			if(type.equals(ATTRIBUTE_TYPE.CLASS)){
				Attribute obj_class_attr = new Attribute(tmp,attribute_types.get(type));
				OBJECT_CLASS obj_class = null;
				try {
					obj_class = OBJECT_CLASS.swigToEnum((int) obj_class_attr.getAsLong());
				} catch (PKCS11Error e) {

				}
				
				if(obj_class.equals(OBJECT_CLASS.PRIVATE_KEY)){
					 //private key template
				 }else if(obj_class.equals(OBJECT_CLASS.PUBLIC_KEY)){
					 //bool
				 }else if(obj_class.equals(OBJECT_CLASS.CERTIFICATE)){
					 
				 }else if(obj_class.equals(OBJECT_CLASS.SECRET_KEY)){
					 //byte array
				 }
			}
		}
		return null;
	}

	public static PKCS11Object createFromSkyTrust(CK_ATTRIBUTE[] template){
		 for(CK_ATTRIBUTE tmp: template){
			 long type = tmp.getType();
			 if(type == ATTRIBUTE_TYPE.KEY_TYPE.swigValue()){
				 //key_type
			 }else if(type == ATTRIBUTE_TYPE.DERIVE.swigValue()){
				 //bool
			 }else if(type == ATTRIBUTE_TYPE.LOCAL.swigValue()){
				 
			 }else if(type == ATTRIBUTE_TYPE.ID.swigValue()){
				 //byte array
			 }
		 }
		return null;
	}

	public static PKCS11Object createFrom(SKey key){
		switch(key.getRepresentation()){
		case "fullKey":
			break;
		case "certificate":
			break;
		case "handle":
			break;
		case "keyIdentifier":
			break;
		}
		return null;
	}
}
