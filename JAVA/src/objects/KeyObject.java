package objects;

import proxys.KEY_TYP;
import proxys.MECHANISM_TYPE;

public class KeyObject extends StorageObject{
	private KEY_TYP CKA_KEY_TYPE;
	private byte[] CKA_ID;
	private MECHANISM_TYPE[] CKA_ALLOWED_MECHANISMS;
}
