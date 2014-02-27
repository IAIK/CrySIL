package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pkcs11.PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.KEY_TYP;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;

public class ObjectBuilder {
	

	private static ArrayList<ATTRIBUTE> defaultKey_template;
	private static ArrayList<ATTRIBUTE> defaultTemplate_secretKey;
	private static ArrayList<ATTRIBUTE> defaultTemplate_publicKey;
	static{
		try {
			defaultKey_template = new ArrayList<>();
			defaultKey_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.EXTRACTABLE,true));
			defaultKey_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,true));
			defaultKey_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.TOKEN,false));
			defaultKey_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.KEY_TYPE,KEY_TYP.RSA_KEY));
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	static {
		try {
			defaultTemplate_secretKey = new ArrayList<>();
			defaultTemplate_secretKey.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.SECRET_KEY));
			defaultTemplate_secretKey.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,false));
			defaultTemplate_secretKey.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,true));
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	static {
		try {
			defaultTemplate_publicKey = new ArrayList<>();
			defaultTemplate_publicKey.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.PUBLIC_KEY));
			defaultTemplate_publicKey.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,false));
			defaultTemplate_publicKey.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,false));
			
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	private static Map<ATTRIBUTE_TYPE,ATTRIBUTE> copyToMap(ArrayList<ATTRIBUTE> template) throws PKCS11Error{
		Map<ATTRIBUTE_TYPE,ATTRIBUTE> res = new HashMap<>();
		for(ATTRIBUTE attr : template){
			res.put(attr.getTypeEnum(), attr.createClone());
		}
		return res;
	}

	public static PKCS11Object createFromTemplate(ArrayList<ATTRIBUTE> template) throws PKCS11Error{
		ATTRIBUTE attr_class = ATTRIBUTE.find(template, ATTRIBUTE_TYPE.CLASS);
		if(attr_class == null){
			throw new PKCS11Error(RETURN_TYPE.TEMPLATE_INCOMPLETE);
		}
		OBJECT_CLASS obj_class = attr_class.copyToSwigEnum(OBJECT_CLASS.class);

		HashMap<ATTRIBUTE_TYPE,ATTRIBUTE> default_attr = new HashMap<>(copyToMap(defaultKey_template));
		if(obj_class.equals(OBJECT_CLASS.PRIVATE_KEY)){
			 //private key template
			default_attr.putAll(copyToMap(defaultTemplate_secretKey));
		 }else if(obj_class.equals(OBJECT_CLASS.PUBLIC_KEY)){
			default_attr.putAll(copyToMap(defaultTemplate_publicKey));
		 }else if(obj_class.equals(OBJECT_CLASS.CERTIFICATE)){
			 default_attr.putAll(copyToMap(defaultTemplate_publicKey));
		 }else if(obj_class.equals(OBJECT_CLASS.SECRET_KEY)){
			 
		 }else if(obj_class.equals(OBJECT_CLASS.DATA)){
			 
		 }else{
			 throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_VALUE_INVALID);
		 }
		
		default_attr.putAll(copyToMap(template));
		return new PKCS11Object(default_attr);
	}
}
