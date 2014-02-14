package pkcs11;
import java.util.ArrayList;

import objects.PKCS11Object;

import proxys.CK_ATTRIBUTE;


public class FindObjectsHelper {
	
	public final CK_ATTRIBUTE[] pTemplate;
	public final long ulCount;
	public long actualCount=0;
	public ArrayList<PKCS11Object> foundObjects;
	public int index;

	public FindObjectsHelper(CK_ATTRIBUTE[] pTemplate, long ulCount, Session session) throws PKCS11Error {
		this.pTemplate = pTemplate;
		this.ulCount = ulCount;
		foundObjects = session.objectManager.findObjects(pTemplate);
	}

	
	
	

}
