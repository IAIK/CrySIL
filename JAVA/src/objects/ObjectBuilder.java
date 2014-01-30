package objects;

import java.util.HashMap;
import java.util.Map;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import proxys.ATTRIBUTE_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.OBJECT_CLASS;

public class ObjectBuilder {

	private static Map<ATTRIBUTE_TYPE,Class<?>> attribute_types = new HashMap<>();

	public static PKCS11Object createFromTemplate(CK_ATTRIBUTE[] template){
		for(CK_ATTRIBUTE tmp : template){
			ATTRIBUTE_TYPE type = ATTRIBUTE_TYPE.swigToEnum((int) tmp.getType());
			if(type.equals(ATTRIBUTE_TYPE.CLASS)){
				Attribute obj_class_attr = new Attribute(tmp,attribute_types.get(type));
				
				OBJECT_CLASS obj_class = OBJECT_CLASS.swigToEnum(obj_class_attr.getValueAsLong());
				
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

	public static KeyObject createKeyFromTemplate(CK_ATTRIBUTE[] template){
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
	public static KeyObject createPublicKeyFromTemplate(CK_ATTRIBUTE[] template){
		 for(CK_ATTRIBUTE tmp: template){
			 long type = tmp.getType();
			 if(type == ATTRIBUTE_TYPE.KEY_TYPE.swigValue()){
				 //key_type
			 }else if(type == ATTRIBUTE_TYPE.DERIVE.swigValue()){
				 //bool
			 }else if(type == ATTRIBUTE_TYPE.LOCAL.swigValue()){
				 
			 }else if(type == ATTRIBUTE_TYPE.ID.swigValue()){
				 //byte array
			 }else if(type == ATTRIBUTE_TYPE.ENCRYPT.swigValue()){
				 //bool
			 }else if(type == ATTRIBUTE_TYPE.WRAP.swigValue()){
				 //bool
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
