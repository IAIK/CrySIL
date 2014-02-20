package objects;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import pkcs11.PKCS11Error;
import pkcs11.Util;
import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_BYTE_ARRAY;
import proxys.CK_KEY_WRAP_SET_OAEP_PARAMS;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;

public class ObjectBuilder {
	

	private static ATTRIBUTE[] defaultKey_template;
	static{
		try {
		defaultKey_template[1] = new ATTRIBUTE(ATTRIBUTE_TYPE.EXTRACTABLE,true);
		defaultKey_template[2] = new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,true);
		defaultKey_template[3] = new ATTRIBUTE(ATTRIBUTE_TYPE.TOKEN,false);
		defaultKey_template[4] = new ATTRIBUTE(ATTRIBUTE_TYPE.KEY_TYPE,KEY_TYP.RSA_KEY);
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	private static ATTRIBUTE[] defaultTemplate_secretKey;
	static {
		try {
		defaultTemplate_secretKey[1] = new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.SECRET_KEY);
		defaultTemplate_secretKey[2] = new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,false);
		defaultTemplate_secretKey[3] = new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,true);
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	

	private static Map<ATTRIBUTE_TYPE,ATTRIBUTE> copyToMap(ATTRIBUTE[] template){
		Map<ATTRIBUTE_TYPE,ATTRIBUTE> res = new HashMap<>();
		for(ATTRIBUTE attr : template){
			res.put(attr.getTypeEnum(), attr.clone());
		}
		return res;
	}

	public static PKCS11Object createFromTemplate(ATTRIBUTE[] template) throws PKCS11Error{
		
		
		ATTRIBUTE attr_class = ATTRIBUTE.find(template, ATTRIBUTE_TYPE.CLASS);
		if(attr_class == null){
			throw new PKCS11Error(RETURN_TYPE.TEMPLATE_INCOMPLETE);
		}
		OBJECT_CLASS obj_class = attr_class.copyToSwigEnum(OBJECT_CLASS.class);

		HashMap<ATTRIBUTE_TYPE,ATTRIBUTE> object_attr = null;
		if(obj_class.equals(OBJECT_CLASS.PRIVATE_KEY)){
			 //private key template
			object_attr = new HashMap<>(copyToMap(defaultTemplate_secretKey));
		 }else if(obj_class.equals(OBJECT_CLASS.PUBLIC_KEY)){
			 
		 }else if(obj_class.equals(OBJECT_CLASS.CERTIFICATE)){
			 
		 }else if(obj_class.equals(OBJECT_CLASS.SECRET_KEY)){
			 
		 }
		object_attr.putAll(copyToMap(template));
		return new PKCS11Object(object_attr);
	}
}
