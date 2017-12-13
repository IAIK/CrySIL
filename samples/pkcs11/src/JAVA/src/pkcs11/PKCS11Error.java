package pkcs11;

public class PKCS11Error extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8385653535225460037L;
	private Long error;

	public PKCS11Error(Long er) {
		error = er;
	}

	public long getCode() {
		return error;
	}
}
