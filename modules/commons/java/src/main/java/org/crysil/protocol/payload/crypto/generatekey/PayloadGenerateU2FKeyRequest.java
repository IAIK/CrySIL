package org.crysil.protocol.payload.crypto.generatekey;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;

import com.google.common.io.BaseEncoding;

public class PayloadGenerateU2FKeyRequest extends PayloadRequest {

	protected String appParam;
	protected String clientParam;
	protected String encodedRandom;

	/** The key type. */
	protected String keyType;

	/** The certificate subject. */
	protected String certificateSubject;

	@Override
	public String getType() {
		return "generateU2FKeyRequest";
	}

	public void setAppParam(byte[] appParam) {
		this.appParam = BaseEncoding.base64().encode(appParam);
	}

	public byte[] getAppParam() {
		return BaseEncoding.base64().decode(appParam);
	}

	public void setClientParam(byte[] clientParam) {
		this.clientParam = clientParam != null ? BaseEncoding.base64().encode(clientParam) : null;
	}

	public byte[] getClientParam() {
		return clientParam != null ? BaseEncoding.base64().decode(clientParam) : null;
	}

	public void setEncodedRandom(byte[] encodedRandom) {
		this.encodedRandom = encodedRandom != null ? BaseEncoding.base64().encode(encodedRandom) : null;
	}

	public byte[] getEncodedRandom() {
		return encodedRandom != null ? BaseEncoding.base64().decode(encodedRandom) : null;
	}

	/**
	 * Gets the key type.
	 *
	 * @return the key type
	 */
	public String getKeyType() {
		return keyType;
	}

	/**
	 * Sets the key type.
	 *
	 * @param keyType
	 *            the new key type
	 */
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	/**
	 * Gets the certificate subject.
	 *
	 * @return the certificate subject
	 */
	public String getCertificateSubject() {
		return certificateSubject;
	}

	/**
	 * Sets the certificate subject.
	 *
	 * @param certificateSubject
	 *            the new certificate subject
	 */
	public void setCertificateSubject(String certificateSubject) {
		this.certificateSubject = certificateSubject;
	}

	@Override
	public PayloadRequest getBlankedClone() {
		PayloadGenerateU2FKeyRequest result = new PayloadGenerateU2FKeyRequest();
		result.keyType = Logger.isDebugEnabled() ? keyType : "*****";
		result.certificateSubject = Logger.isDebugEnabled() ? certificateSubject : "*****";
		result.appParam = Logger.isDebugEnabled() ? appParam : "*****";
		result.clientParam = Logger.isDebugEnabled() ? clientParam : "*****";
		result.encodedRandom = Logger.isDebugEnabled() ? encodedRandom : "*****";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((certificateSubject == null) ? 0 : certificateSubject.hashCode());
		result = prime * result + ((appParam == null) ? 0 : appParam.hashCode());
		result = prime * result + ((clientParam == null) ? 0 : clientParam.hashCode());
		result = prime * result + ((encodedRandom == null) ? 0 : encodedRandom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayloadGenerateU2FKeyRequest other = (PayloadGenerateU2FKeyRequest) obj;
		if (certificateSubject == null) {
			if (other.certificateSubject != null)
				return false;
		} else if (!certificateSubject.equals(other.certificateSubject))
			return false;
		if (keyType == null) {
			if (other.keyType != null)
				return false;
		} else if (!keyType.equals(other.keyType))
			return false;
		if (appParam == null) {
			if (other.appParam != null)
				return false;
		} else if (!appParam.equals(other.appParam))
			return false;
		if (clientParam == null) {
			if (other.clientParam != null)
				return false;
		} else if (!clientParam.equals(other.clientParam))
			return false;
		if (encodedRandom == null) {
			if (other.encodedRandom != null)
				return false;
		} else if (!encodedRandom.equals(other.encodedRandom))
			return false;
		return true;
	}
}
