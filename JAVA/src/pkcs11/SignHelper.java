package pkcs11;
import proxys.CK_MECHANISM;


public class SignHelper {

	public final long hSession;
	public final CK_MECHANISM mechanism;
	public final long hkey;
	
	public byte[] pData;
	public byte[] cData;
	
	
	public SignHelper(long hSession, CK_MECHANISM pMechanism, long hKey){
		this.hSession = hSession;
		this.mechanism = pMechanism;
		this.hkey=hKey;
	}

}
