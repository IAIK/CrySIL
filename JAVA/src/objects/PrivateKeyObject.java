package objects;

public class PrivateKeyObject extends KeyObject {
	private byte[] CKA_SUBJECT;		//DER-encoding of certificate subject name (default empty)
	private boolean CKA_SENSITIVE;  //CK_TRUE if key is sensitive
	private boolean CKA_DECRYPT;	//CK_TRUE if key supports decryption
	private boolean CKA_SIGN;		//CK_TRUE if key supports signatures where the signature is an appendix to the data
	private boolean CKA_UNWRAP;		//CK_TRUE if key supports unwrapping (i.e., can be used to unwrap other keys)
	private boolean CKA_EXTRACTABLE;//CK_TRUE if key is extractable and can be wrapped
}
