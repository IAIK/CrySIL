package objects;

import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;

/**
 * 
 * MKey is an encapsulation of the Skytrust-SKey. In this way it is possible to
 * seperate Skytrust and the PKCS#11 library. Changes in the Skytrust protocol
 * can easily be adopted.
 */
public class MKey {

	protected Key key;
	protected byte[] certificate;
	protected String id;
	protected String subId;
	protected String type = "certificate";
	public boolean isCertificate = true;

	private MKey() {

	}

	// public static MKey fromBase64String(String label, String base64) {
	// MKey mKey = new MKey();
	// mKey.isCertificate = false;
	// mKey.id = label;
	// mKey.encodedCertificate = base64;
	// return mKey;
	// }

	public static MKey fromKey(Key key) {
		try {
			if (key.getType().compareTo("externalCertificate") == 0) {
				InternalCertificate certKey = (InternalCertificate) key;
				MKey mKey = new MKey();
				mKey.key = key;
				mKey.id = certKey.getId();
				mKey.subId = certKey.getSubId();
				mKey.certificate = certKey.getCertificate().getEncoded();
				return mKey;
			}

			if (key.getType().compareTo("internalCertificate") == 0) {
				InternalCertificate certKey = (InternalCertificate) key;
				MKey mKey = new MKey();
				mKey.key = key;
				mKey.id = certKey.getId();
				mKey.subId = certKey.getSubId();
				mKey.certificate = certKey.getCertificate().getEncoded();
				return mKey;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Key getKey() {
		return key;
	}

	public byte[] getCertificate() {
		return certificate;
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
