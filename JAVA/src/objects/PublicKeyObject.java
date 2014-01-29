package objects;



public class PublicKeyObject extends KeyObject {
	private byte[] CKA_SUBJECT; //DER-encoding of the key subject name(default empty)
	private boolean CKA_ENCRYPT; //CK_TRUE if key supports encryption
	private boolean CKA_VERIFY; //CK_TRUE if key supports verification where the signature is an appendix to the data
	private boolean CKA_WRAP;  //CK_TRUE if key supports wrapping
	private boolean CKA_TRUSTED; //The key can be trusted for the application that it was created. The wrapping key can be used to wrap keys with CKA_WRAP_WITH_TRUSTED set to CK_TRUE
}
