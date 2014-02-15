package pkcs11;
import java.util.ArrayList;

import objects.Attribute;
import objects.PKCS11Object;

import proxys.CK_ATTRIBUTE;


public class FindObjectsHelper {
	
	public final Attribute[] pTemplate;
	public long actualCount=0;
	public ArrayList<PKCS11Object> foundObjects;
	public int index;

	public FindObjectsHelper(Attribute[] pTemplate){
		this.pTemplate = pTemplate;
	}
	public void setFoundObj(ArrayList<PKCS11Object> objs){
		foundObjects = objs;
	}
}
