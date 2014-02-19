package objects;

import java.util.HashMap;
import objects.ATTRIBUTE;
import pkcs11.PKCS11Error;
import proxys.ATTRIBUTE_TYPE;
import proxys.RETURN_TYPE;

public class PKCS11Object {
	
	private HashMap<ATTRIBUTE_TYPE,ATTRIBUTE> attributes = new HashMap<>();

	public PKCS11Object(ATTRIBUTE[] template){
		for(ATTRIBUTE attr: template){
			attributes.put(attr.getTypeEnum(), attr);
		}
	}
	public PKCS11Object(HashMap<ATTRIBUTE_TYPE,ATTRIBUTE> template){
		attributes = template;
	}
	public void setAttribute(ATTRIBUTE val) throws PKCS11Error{
		attributes.put(val.getTypeEnum(), val);
	}
	public boolean hasAttribute(ATTRIBUTE_TYPE type){
		return attributes.containsKey(type);
	}
	public ATTRIBUTE getAttribute(ATTRIBUTE_TYPE type) throws PKCS11Error{
		ATTRIBUTE res = attributes.get(type);
		if(res == null){
			throw new PKCS11Error(RETURN_TYPE.ATTRIBUTE_TYPE_INVALID);
		}
		return res;
	}
	
	
	public boolean query(ATTRIBUTE[] attributes){
		for(ATTRIBUTE tmp : attributes){
			if(tmp.query(this.attributes.get(tmp.getType()))){
				
			}else{
				return false;
			}
		}
		return true;
	}
	
	
}
