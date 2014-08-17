package objects;

import java.util.HashMap;

import obj.CK_ATTRIBUTE;
import obj.CK_RETURN_TYPE;
import pkcs11.PKCS11Error;

public class PKCS11Object {
	
	private HashMap<Long,CK_ATTRIBUTE> attributes = new HashMap<>();

	private Object tag;
	
	public void setTag(Object tag){
		this.tag = tag;
	}
	public Object getTag(){
		return tag;
	}
	
	public PKCS11Object(CK_ATTRIBUTE[] template){
		for(CK_ATTRIBUTE attr: template){
			attributes.put(attr.getType(), attr);
		}
	}
	public PKCS11Object(HashMap<Long,CK_ATTRIBUTE> template){
		attributes = template;
	}
	public void setAttribute(CK_ATTRIBUTE val) throws PKCS11Error{
		attributes.put(val.getType(), val);
	}
	public boolean hasAttribute(long type){
		return attributes.containsKey(type);
	}
	public CK_ATTRIBUTE getAttribute(long type) throws PKCS11Error{
		CK_ATTRIBUTE res = attributes.get(type);
		if(res == null){
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_ATTRIBUTE_TYPE_INVALID);
		}
		return res;
	}
	public boolean query(CK_ATTRIBUTE[] attributes){
		if(attributes == null){
			return true;
		}
		for(CK_ATTRIBUTE query_attr : attributes){
			CK_ATTRIBUTE attr  = this.attributes.get(query_attr.getType());
			if(attr != null && attr.query(query_attr)){
				continue;
			}else{
				return false;
			}
		}
		return true;
	}	
}
