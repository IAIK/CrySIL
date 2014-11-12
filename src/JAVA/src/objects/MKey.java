package objects;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SInternalCertificate;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

public class MKey {

	protected SKey key;
	protected String encodedCertificate = "";
	protected String id;
	protected String subId;
	protected String type="certificate";

	private MKey() {

	}

	public static MKey fromSKey(SKey key) {

		if (key.getType().compareTo("externalCertificate") == 0) {
			SInternalCertificate certKey = (SInternalCertificate) key;
			MKey mKey = new MKey();
			mKey.key = key;
			mKey.id = certKey.getId();
			mKey.subId = certKey.getSubId();
			mKey.encodedCertificate = certKey.getEncodedCertificate();
			return mKey;
		}

		if (key.getType().compareTo("internalCertificate") == 0) {
			SInternalCertificate certKey = (SInternalCertificate) key;
			MKey mKey = new MKey();
			mKey.key = key;
			mKey.id = certKey.getId();
			mKey.subId = certKey.getSubId();
			mKey.encodedCertificate = certKey.getEncodedCertificate();
			return mKey;
		}
		return null;
	}

	public SKey getSKey() {
		return key;
	}

	public String getEncodedCertificate() {
		return encodedCertificate;
	}
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getType(){
		return type;
	}

}
