package pkcs11;
import java.util.ArrayList;

import objects.ATTRIBUTE;
import objects.PKCS11Object;

import proxys.CK_ATTRIBUTE;


public class FindObjectsHelper {
	
	public final ATTRIBUTE[] pTemplate;
	public long actualCount=0;
	public ArrayList<PKCS11Object> foundObjects;
	public int index;

	public FindObjectsHelper(ATTRIBUTE[] pTemplate){
		this.pTemplate = pTemplate;
	}
	public void setFoundObj(ArrayList<PKCS11Object> objs){
		foundObjects = objs;
	}
}
