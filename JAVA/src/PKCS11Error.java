import proxys.RETURN_TYPE;


public class PKCS11Error extends Exception {
	private RETURN_TYPE error;
	public PKCS11Error(RETURN_TYPE er){
		error = er;
	}
}
