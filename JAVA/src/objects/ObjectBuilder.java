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
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;

public class ObjectBuilder {
	
	private static Map<ATTRIBUTE_TYPE,Attribute> skytrust_template = new HashMap<>();
	private static Map<ATTRIBUTE_TYPE,Attribute> defaultKey_template = new HashMap<>();
	static{		
		byte[] enum_value = new byte[4];
		byte[] bool_value = new byte[1];
		ByteBuffer.wrap(enum_value).putLong(OBJECT_CLASS.PRIVATE_KEY.swigValue());
		skytrust_template.put(ATTRIBUTE_TYPE.CLASS, new Attribute(ATTRIBUTE_TYPE.CLASS,enum_value));
		bool_value[0] = 0;
		skytrust_template.put(ATTRIBUTE_TYPE.EXTRACTABLE, new Attribute(ATTRIBUTE_TYPE.EXTRACTABLE,bool_value));
		bool_value[0] = 1;
		skytrust_template.put(ATTRIBUTE_TYPE.SENSITIVE, new Attribute(ATTRIBUTE_TYPE.SENSITIVE,bool_value));
		bool_value[0] = 1;
		skytrust_template.put(ATTRIBUTE_TYPE.NEVER_EXTRACTABLE, new Attribute(ATTRIBUTE_TYPE.NEVER_EXTRACTABLE,bool_value));
		bool_value[0] = 1;
		skytrust_template.put(ATTRIBUTE_TYPE.ALWAYS_SENSITIVE, new Attribute(ATTRIBUTE_TYPE.ALWAYS_SENSITIVE,bool_value));
		bool_value[0] = 1;
		skytrust_template.put(ATTRIBUTE_TYPE.TOKEN, new Attribute(ATTRIBUTE_TYPE.TOKEN,bool_value));
		bool_value[0] = 0;
		skytrust_template.put(ATTRIBUTE_TYPE.MODIFIABLE, new Attribute(ATTRIBUTE_TYPE.MODIFIABLE,bool_value));
	}
	static{		
		byte[] enum_value = new byte[4];
		byte[] bool_value = new byte[1];
		ByteBuffer.wrap(enum_value).putLong(OBJECT_CLASS.PUBLIC_KEY.swigValue());
		defaultKey_template.put(ATTRIBUTE_TYPE.CLASS, new Attribute(ATTRIBUTE_TYPE.CLASS,enum_value));
		bool_value[0] = 1;
		defaultKey_template.put(ATTRIBUTE_TYPE.EXTRACTABLE, new Attribute(ATTRIBUTE_TYPE.EXTRACTABLE,bool_value));
		bool_value[0] = 0;
		defaultKey_template.put(ATTRIBUTE_TYPE.SENSITIVE, new Attribute(ATTRIBUTE_TYPE.SENSITIVE,bool_value));
		bool_value[0] = 0;
		defaultKey_template.put(ATTRIBUTE_TYPE.TOKEN, new Attribute(ATTRIBUTE_TYPE.TOKEN,bool_value));
		bool_value[0] = 1;
		defaultKey_template.put(ATTRIBUTE_TYPE.MODIFIABLE, new Attribute(ATTRIBUTE_TYPE.MODIFIABLE,bool_value));
	}
	
	private static Attribute[] toAttributeArray(CK_ATTRIBUTE[] template){
		Attribute[] res = new Attribute[template.length];
		for(int i=0;i<template.length;i++){
			res[i] = new Attribute(template[i]);
		}
		return res;
	}
	public static PKCS11Object createFromTemplate(CK_ATTRIBUTE[] template) throws PKCS11Error{
		Attribute[] attributes = toAttributeArray(template);
		
		for(Attribute attr : attributes){
			if(attr.getType().equals(ATTRIBUTE_TYPE.CLASS)){
				OBJECT_CLASS obj_class = null;
				obj_class = attr.getAsSwig(OBJECT_CLASS.class);

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

	public static PKCS11Object createFromSkyTrust(SKey key){
		try {
		 switch(key.getRepresentation()){
		 case "fullKey":
			 break;
		 case "certificate":
				skytrust_template.get(ATTRIBUTE_TYPE.CLASS).setSwig(OBJECT_CLASS.CERTIFICATE);
			 break;
		 case "handle":
			 break;
		 case "keyIdentifier":
			 break;
		 }
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
