package pkcs11;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import objects.Attribute;
import objects.Mechanism;
import objects.PKCS11Object;
import proxys.ATTRIBUTE_TYPE;
import proxys.OBJECT_CLASS;

public class PKCS11SkyTrustMapper {
	
	private static Map<ATTRIBUTE_TYPE,Attribute> skytrust_template = new HashMap<>();
	static{		
		byte[] bool_value = new byte[1];
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
	public static SKey mapKey(PKCS11Object key){
		//TODO dummy
		return null;
	}
	public static SkyTrustAlgorithm mapMechanism(Mechanism key){
		//TODO dummy
		return null;
	}
	public static Attribute[] mapKey(SKey key){
		//TODO dummy
		ArrayList<Attribute> template = new ArrayList<>(skytrust_template.values());
		template.add(new Attribute(ATTRIBUTE_TYPE.ID,key.getId().getBytes()));
		
		 switch(key.getRepresentation()){
		 case "fullKey":
			 break;
		 case "certificate":
			template.add(new Attribute(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.CERTIFICATE));
			 break;
		 case "handle":
			 break;
		 case "keyIdentifier":
			 break;
		 }
		return null;
	}
}
