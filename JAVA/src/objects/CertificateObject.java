package objects;

import proxys.CERT_TYPE;
import proxys.CK_DATE;

public class CertificateObject extends StorageObject {
	private CERT_TYPE CKA_CERTIFICATE_TYPE;
	private long CKA_CERTIFICATE_CATEGORY;
	private byte[] CKA_CHECK_VALUE;
	private CK_DATE CKA_START_DATE;
	private CK_DATE CKA_END_DATE;
	private boolean CKA_TRUSTED; //can only be set by SO; trusted cert not modifyable
}
