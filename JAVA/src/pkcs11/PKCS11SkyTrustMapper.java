package pkcs11;

import java.util.ArrayList;
import java.util.HashMap;
import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import objects.ATTRIBUTE;
import objects.MECHANISM;
import objects.PKCS11Object;
import proxys.ATTRIBUTE_TYPE;
import proxys.KEY_TYP;
import proxys.MECHANISM_TYPES;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;

public class PKCS11SkyTrustMapper {
	
	private static HashMap<ATTRIBUTE_TYPE,ATTRIBUTE> skytrust_template = new HashMap<>();
	private static HashMap<MECHANISM_TYPES,SkyTrustAlgorithm> mechanism_map = new HashMap<>();

	static{
		try {
		skytrust_template.put(ATTRIBUTE_TYPE.EXTRACTABLE, new ATTRIBUTE(ATTRIBUTE_TYPE.EXTRACTABLE,false));
		skytrust_template.put(ATTRIBUTE_TYPE.SENSITIVE, new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,true));
		skytrust_template.put(ATTRIBUTE_TYPE.NEVER_EXTRACTABLE, new ATTRIBUTE(ATTRIBUTE_TYPE.NEVER_EXTRACTABLE,true));
		skytrust_template.put(ATTRIBUTE_TYPE.ALWAYS_SENSITIVE, new ATTRIBUTE(ATTRIBUTE_TYPE.ALWAYS_SENSITIVE,true));
		skytrust_template.put(ATTRIBUTE_TYPE.TOKEN, new ATTRIBUTE(ATTRIBUTE_TYPE.TOKEN,true));
		skytrust_template.put(ATTRIBUTE_TYPE.MODIFIABLE, new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,false));
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static{	
		mechanism_map.put(MECHANISM_TYPES.RSA_PKCS,SkyTrustAlgorithm.RSAES_PKCS1_V1_5);
		mechanism_map.put(MECHANISM_TYPES.RSA_PKCS_PSS,SkyTrustAlgorithm.RSA_PSS);
		mechanism_map.put(MECHANISM_TYPES.RSA_PKCS_OAEP,SkyTrustAlgorithm.RSA_OAEP);
		mechanism_map.put(MECHANISM_TYPES.SHA1_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_1);
		mechanism_map.put(MECHANISM_TYPES.SHA256_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_256);
		mechanism_map.put(MECHANISM_TYPES.SHA512_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_512);
		mechanism_map.put(MECHANISM_TYPES.SHA224_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_224);
	}
	public static SKey mapKey(PKCS11Object key) throws PKCS11Error{
		//TODO dummy
		if(key == null /*|| key is not a keyObj || key is not a SkytrustKey*/){
			throw new PKCS11Error(RETURN_TYPE.KEY_HANDLE_INVALID);
		}
		return null;
	}
	public static SkyTrustAlgorithm mapMechanism(MECHANISM mech) throws PKCS11Error{
		SkyTrustAlgorithm algo = mechanism_map.get(mech.getType());
		if(algo == null){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
		}
		return algo;
	}
	public static ATTRIBUTE[] mapKey(SKey key) throws PKCS11Error{
		//TODO dummy
		ArrayList<ATTRIBUTE> template = new ArrayList<>(skytrust_template.values());
		template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ID,key.getId().getBytes()));
		
		 switch(key.getRepresentation()){
		 case "fullKey":
			 template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.KEY_TYPE,KEY_TYP.RSA_KEY));
			 template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.PRIVATE,true));
			 template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,true));
			 template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.PRIVATE_KEY));
			 break;
		 case "certificate":
			template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.CERTIFICATE));
			 break;
		 case "handle":
			 break;
		 case "keyIdentifier":
			 break;
		 }
		return null;
	}
}
