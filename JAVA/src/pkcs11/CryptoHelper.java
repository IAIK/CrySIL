package pkcs11;
import objects.Mechanism;
import objects.PKCS11Object;


public class CryptoHelper {

	public final Mechanism mechanism;
	public final PKCS11Object key;
	
	public byte[] pData;
	public byte[] cData;
	
	
	public CryptoHelper(long hSession, Mechanism pMechanism, PKCS11Object Key){
		this.mechanism = pMechanism;
		this.key=Key;
	}

}
