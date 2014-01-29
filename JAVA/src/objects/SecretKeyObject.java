package objects;

public class SecretKeyObject extends KeyObject {
	private boolean CKA_SENSITIVE = false; 	//CK_TRUE if object is sensitive (default CK_FALSE)
	private boolean CKA_ENCRYPT; 	//CK_TRUE if key supports encryption
	private boolean CKA_DECRYPT; 	//CK_TRUE if key supports decryption
	private boolean CKA_SIGN; 		//CK_TRUE if key supports signatures (i.e., authentication codes) where the signature is an appendix to the data
	private boolean CKA_VERIFY; 	//CK_TRUE if key supports verification (i.e., of authentication codes) where the signature is an appendix to the data
	private boolean CKA_WRAP; 		//CK_TRUE if key supports wrapping (i.e., can be used to wrap other keys)
	private boolean CKA_UNWRAP; 	//CK_TRUE if key supports unwrapping (i.e., can be used to unwrap other keys)
	private boolean CKA_EXTRACTABLE; //CK_TRUE if key is extractable and can be wrapped
}
