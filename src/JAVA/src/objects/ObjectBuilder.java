package objects;

import obj.*;
import pkcs11.PKCS11Error;

import iaik.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObjectBuilder {
	

	private static ArrayList<CK_ATTRIBUTE> defaultKey_template;
	private static ArrayList<CK_ATTRIBUTE> defaultTemplate_secretKey;
	private static ArrayList<CK_ATTRIBUTE> defaultTemplate_publicKey;
	private static ArrayList<CK_ATTRIBUTE> defaultTemplate_certificate;
	static{
			defaultKey_template = new ArrayList<>();
			defaultKey_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_EXTRACTABLE,true, 1));
			defaultKey_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODIFIABLE,true,1));
			defaultKey_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_KEY_TYPE,CK_KEY_TYPE.CKK_RSA,8));
			defaultKey_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TOKEN,true, 1));
	};
	static {
			defaultTemplate_secretKey = new ArrayList<>();
			defaultTemplate_secretKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS,CK_OBJECT_TYPE.CKO_SECRET_KEY,8));
			defaultTemplate_secretKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODIFIABLE,false,1));
			defaultTemplate_secretKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE,true,1));
			defaultTemplate_secretKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TOKEN,true, 1));
	};
	static {
			defaultTemplate_publicKey = new ArrayList<>();
			defaultTemplate_publicKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS,CK_OBJECT_TYPE.CKO_PUBLIC_KEY,8));
			defaultTemplate_publicKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODIFIABLE,false, 1));
			defaultTemplate_publicKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE,false,1));
			defaultTemplate_publicKey.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TOKEN,true, 1));
	};
	static {
			defaultTemplate_certificate = new ArrayList<>();
			defaultTemplate_certificate.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS,CK_OBJECT_TYPE.CKO_CERTIFICATE, 8));
			defaultTemplate_certificate.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODIFIABLE,false,1));
			defaultTemplate_certificate.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE,true,1));
			defaultTemplate_certificate.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TOKEN,true, 1));
	};
	
	private static Map<Long,CK_ATTRIBUTE> copyToMap(CK_ATTRIBUTE[] template) throws PKCS11Error{
		Map<Long,CK_ATTRIBUTE> res = new HashMap<>();
		for(CK_ATTRIBUTE attr : template){
			res.put(attr.getType(), attr.createClone());
		}
		return res;
	}

	public static PKCS11Object createFromTemplate(CK_ATTRIBUTE[] template) throws PKCS11Error{
		CK_ATTRIBUTE attr_class =CK_ATTRIBUTE.find(template, CK_ATTRIBUTE_TYPE.CKA_CLASS);
		if(attr_class == null){
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_TEMPLATE_INCOMPLETE);
		}
//		CK_OBJECT_TYPE.CKO_obj_class = attr_class.copyToSwigEnum(CK_OBJECT_TYPE.CKO_class);
		

		HashMap<Long,CK_ATTRIBUTE> default_attr = new HashMap<>(copyToMap((CK_ATTRIBUTE[]) defaultKey_template.toArray(new CK_ATTRIBUTE[defaultKey_template.size()])));
		if(attr_class.getpValue().equals(CK_OBJECT_TYPE.CKO_PRIVATE_KEY)){
			default_attr.putAll(copyToMap((CK_ATTRIBUTE[]) defaultTemplate_secretKey.toArray(new CK_ATTRIBUTE[defaultTemplate_secretKey.size()])));
		 }else if(attr_class.getpValue().equals(CK_OBJECT_TYPE.CKO_PUBLIC_KEY)){
			default_attr.putAll(copyToMap((CK_ATTRIBUTE[]) defaultTemplate_publicKey.toArray(new CK_ATTRIBUTE[defaultTemplate_publicKey.size()])));
		 }else if(attr_class.getpValue().equals(CK_OBJECT_TYPE.CKO_CERTIFICATE)){
//			 default_attr.putAll(copyToMap(defaultTemplate_publicKey));
			 default_attr.putAll(copyToMap((CK_ATTRIBUTE[]) defaultTemplate_certificate.toArray(new CK_ATTRIBUTE[defaultTemplate_certificate.size()])));
		 }else if(attr_class.getpValue().equals(CK_OBJECT_TYPE.CKO_SECRET_KEY)){
			 
		 }else if(attr_class.getpValue().equals(CK_OBJECT_TYPE.CKO_DATA)){
			 default_attr.putAll(copyToMap((CK_ATTRIBUTE[]) defaultTemplate_certificate.toArray(new CK_ATTRIBUTE[defaultTemplate_certificate.size()])));
		 }else{
			 throw new PKCS11Error(CK_RETURN_TYPE.CKR_ATTRIBUTE_VALUE_INVALID);
		 }
		
		
//		if(obj_class.equals(CK_OBJECT_TYPE.CKO_PRIVATE_KEY)){
//			 //private key template
//			default_attr.putAll(copyToMap(defaultTemplate_secretKey));
//		 }else if(obj_class.equals(CK_OBJECT_TYPE.CKO_PUBLIC_KEY)){
//			default_attr.putAll(copyToMap(defaultTemplate_publicKey));
//		 }else if(obj_class.equals(CK_OBJECT_TYPE.CKO_CERTIFICATE)){
////			 default_attr.putAll(copyToMap(defaultTemplate_publicKey));
//			 default_attr.putAll(copyToMap(defaultTemplate_certificate));
//		 }else if(obj_class.equals(CK_OBJECT_TYPE.CKO_SECRET_KEY)){
//			 
//		 }else if(obj_class.equals(CK_OBJECT_TYPE.CKO_DATA)){ //			 
//		 }else{
//			 throw new PKCS11Error(CK_RETURN_TYPE.CKR_ATTRIBUTE_VALUE_INVALID);
//		 }
		
		default_attr.putAll(copyToMap(template));
		return new PKCS11Object(default_attr);
	}
}
