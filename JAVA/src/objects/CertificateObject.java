package objects;

import proxys.CERT_TYPE;
import proxys.CK_DATE;



public class CertificateObject extends StorageObject {
	private CERT_TYPE CKA_CERTIFICATE_TYPE;
	private long CKA_CERTIFICATE_CATEGORY; //Categorization of the certificate: 0 = unspecified (defaultvalue), 1 = token user, 2 =authority, 3 = other entity
	private byte[] CKA_CHECK_VALUE;
	private byte[] CKA_VALUE; //BER-encoding of the certificate
	private byte[] CKA_SUBJECT;//DER-encoding of the certificate subject name
	private CK_DATE CKA_START_DATE;
	private CK_DATE CKA_END_DATE;
	private boolean CKA_TRUSTED; //can only be set by SO; trusted cert not modifyable
	
	CertificateObject(byte[] subject,byte[] value){
		CKA_CERTIFICATE_TYPE = CERT_TYPE.X_509;
		CKA_VALUE = value;
		CKA_SUBJECT = subject;
	}
}
