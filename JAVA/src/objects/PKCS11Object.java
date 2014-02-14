package objects;

import java.util.HashMap;

import pkcs11.PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.RETURN_TYPE;

public class PKCS11Object {
	
	public long id;
	private HashMap<ATTRIBUTE_TYPE,Attribute> attributes = new HashMap<>();

	public PKCS11Object(Attribute[] template){
		for(Attribute attr: template){
			attributes.put(attr.getType(), attr);
		}
	}
	public PKCS11Object(HashMap<ATTRIBUTE_TYPE,Attribute> template){
		attributes = template;
	}
	public void setAttribute(Attribute val) throws PKCS11Error{ //TODO ist die Unterscheidung zw set und add nötig oder ist es eh immer add
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
	public Attribute getAttribute(ATTRIBUTE_TYPE type) throws PKCS11Error{
		Attribute res = attributes.get(type);
		if(res == null){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_TYPE_INVALID);
		}
		return res;
	}
	
}
