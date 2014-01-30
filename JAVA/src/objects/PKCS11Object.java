package objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.RETURN_TYPE;

public class PKCS11Object {
	private Map<ATTRIBUTE_TYPE,Attribute> attributes = new HashMap<>();


	
	public void setAttribute(Attribute val){
		if(!attributes.containsKey(val.getType())){
			throw new PKCS11Error(RETURN_TYPE.TEMPLATE_INCONSISTENT);
		}
		attributes.put(val.getType(), val);
	}
	public void addAttribute(Attribute val){
		attributes.put(val.getType(),val);
	}
	public boolean hasAttribute(ATTRIBUTE_TYPE type){
		return attributes.containsKey(type);
	}
	public Attribute getAttribute(ATTRIBUTE_TYPE type){
		Attribute res = attributes.get(type);
		if(res == null){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_TYPE_INVALID);
		}
		return res;
	}
	
}
