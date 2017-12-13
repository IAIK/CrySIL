package pkcs11;
import obj.CK_MECHANISM;
import objects.PKCS11Object;
import pkcs11.Slot.CryptoOperationParams;


public class CryptoHelper {

	private CryptoOperationParams params;
	private byte[] pData = new byte[0];
	private byte[] cData = null;
	
	private int parts = 0;
	
	public CryptoHelper(CryptoOperationParams p){
		this.params = p;
	}
	public CryptoHelper(CK_MECHANISM pMechanism, PKCS11Object Key){
		this.params = new CryptoOperationParams(pMechanism, Key);
	}
	public PKCS11Object getKey(){
		return params.key;
	}
	public CK_MECHANISM getMechanism(){
		return params.mechanism;
	}
	public CryptoOperationParams getParams(){
		return params;
	}
	public void addData(byte[] data){
//		byte[] tmp = new byte[pData.length+data.length];
//		int i = 0;
//		for(byte b:pData){
//			tmp[i] = b;
//			i++;
//		}
//		for(byte c:data){
//			tmp[i] = c;
//			i++;
//		}
//		pData = tmp;
//		parts++;
		
		byte[] conc = new byte[pData.length+data.length];
		System.arraycopy(pData, 0, conc, 0, pData.length);
		System.arraycopy(data, 0, conc, pData.length,data.length);
		pData = conc;
		parts++;
	}
	public void setData(byte[] data){
		pData = data;
		cData = null;
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
