package pkcs11;
import objects.Mechanism;
import objects.PKCS11Object;
import pkcs11.Slot.CryptoOperationParams;


public class CryptoHelper {

//	private sMechanism mechanism;
//	private PKCS11Object key;
	private CryptoOperationParams params;
	private byte[] pData = new byte[0];
	private byte[] cData = null;
	
	private int parts = 0;
	
	public CryptoHelper(CryptoOperationParams p){
		this.params = p;
	}
	public CryptoHelper(Mechanism pMechanism, PKCS11Object Key){
		this.params = new CryptoOperationParams(pMechanism, Key);
	}
	public PKCS11Object getKey(){
		return params.key;
	}
	public Mechanism getMechanism(){
		return params.mechanism;
	}
	public CryptoOperationParams getParams(){
		return params;
	}
	public void addData(byte[] data){
		byte[] tmp = new byte[pData.length+data.length];
		int i = 0;
		for(byte b:pData){
			tmp[i] = b;
			i++;
		}
		for(byte b:data){
			tmp[i] = b;
			i++;
		}
		pData = tmp;
		parts++;
	}
	public void setData(byte[] data){
		pData = data;
		parts = 1;
	}
	public boolean isMultiPart(){
		return (parts > 1);
	}
	public byte[] getData(){
		return  pData;
	}
	public boolean hasProcessedData(){
		return (cData != null);
	}
	public void setProcessedData(byte[] data){
		cData = data;
	}
	public byte[] getProcessedData(){
		return cData;
	}
}
