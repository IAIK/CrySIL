package objects;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SInternalCertificate;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

/**
 * 
 * MKey is an encapsulation of the Skytrust-SKey. In this way it is possible to
 * seperate Skytrust and the PKCS#11 library. Changes in the Skytrust protocol
 * can easily be adopted.
 */
public class MKey {

	protected SKey key;
	protected String encodedCertificate = "";
	protected String id;
	protected String subId;
	protected String type = "certificate";
	public boolean isCertificate = true;

	private MKey() {

	}

	public static MKey fromBase64String(String label, String base64) {
		MKey mKey = new MKey();
		mKey.isCertificate = false;
		mKey.id = label;
		mKey.encodedCertificate = base64;
		return mKey;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

}
