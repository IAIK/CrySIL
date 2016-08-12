/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysil;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;
import org.crysil.protocol.payload.crypto.decryptCMS.PayloadDecryptCMSRequest;
import org.crysil.protocol.payload.crypto.decryptCMS.PayloadDecryptCMSResponse;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptResponse;
import org.crysil.protocol.payload.crypto.encryptCMS.PayloadEncryptCMSRequest;
import org.crysil.protocol.payload.crypto.encryptCMS.PayloadEncryptCMSResponse;
import org.crysil.protocol.payload.crypto.exportWrappedKey.PayloadExportWrappedKeyRequest;
import org.crysil.protocol.payload.crypto.exportWrappedKey.PayloadExportWrappedKeyResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateWrappedKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateWrappedKeyResponse;
import org.crysil.protocol.payload.crypto.key.ExternalCertificate;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadGetKeyRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadGetKeyResponse;
import org.crysil.protocol.payload.crypto.modifyWrappedKey.PayloadModifyWrappedKeyRequest;
import org.crysil.protocol.payload.crypto.modifyWrappedKey.PayloadModifyWrappedKeyResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;
import org.crysil.protocol.payload.status.PayloadStatus;

import iaik.utils.Base64Exception;
import iaik.utils.Util;
import iaik.x509.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import element.CrysilElement;

/**
 * Crysil low level API, for internal use only
 * The API directly uses the POJOs which are used for modelling the JSON based protocol.
 */
public class CrysilAPI extends OneToOneInterlink{
	
    /** The receiver. */
    private APIReceiver receiver = (APIReceiver) CrysilElement.get().getReceiver("APIReceiver");

    /** The last request. */
    private Request lastRequest;

    /** The last response. */
    private Response lastResponse;

    /** The current command id. */
    private String currentCommandID;

    /**
     * Instantiates a new crysil api.
     */
    protected CrysilAPI() {
    }

    /**
     * The Class APIHolder.
     */
    private static class APIHolder {

        /** The Constant INSTANCE. */
        private static final CrysilAPI INSTANCE = new CrysilAPI();
    }

    /**
     * Gets the single instance of CrysilAPI.
     *
     * @return single instance of CrysilAPI
     */
    public static CrysilAPI getInstance() {
        return APIHolder.INSTANCE;
    }

    /**
     * Forward request.
     *
     * @param crysilRequest the crysil request
     * @return the response
     * @throws CrysilException the crysil exception
     */
    private Response forwardRequest(Request crysilRequest) throws CrySILException {
        lastRequest = crysilRequest;
        Response crysilResponse = receiver.take(crysilRequest);
        lastResponse = crysilResponse;

        if (crysilResponse != null && crysilResponse.getPayload() instanceof PayloadStatus) {
            throw new UnknownErrorException();
        }

        return crysilResponse;
    }

    /**
     * Creates the basic request.
     *
     * @return the request
     */
    private Request createBasicRequest() {
        Request request = new Request();

        StandardHeader header = new StandardHeader();
        header.setSessionId("sessionid");
        if (currentCommandID!=null) {
            header.setCommandId(currentCommandID);
            currentCommandID=null;
        }
        request.setHeader(header);

        return request;
    }

    /**
     * Base64 encode data.
     *
     * @param dataList the data list
     * @return the list
     */
    private List<String> base64EncodeData(List<byte[]> dataList) {
        List<String> base64Loads = new ArrayList<>();
        for (byte[] data : dataList) {
            base64Loads.add(cleanBase64Load(Util.toBase64String(data)));
        }

        return base64Loads;
    }

    /**
     * Base64 decode data.
     *
     * @param base64DataList the base64 data list
     * @return the list
     * @throws CrySILException the crysil exception
     */
    private List<byte[]> base64DecodeData(List<String> base64DataList) throws CrySILException {
        List<byte[]> rawDataList = new ArrayList<>();

        try {
            for (String base64Data : base64DataList) {
                rawDataList.add(Util.fromBase64String(base64Data));
            }
        } catch (Base64Exception e) {
        	 throw new UnknownErrorException();
        }

        return rawDataList;
    }

    /**
     * Base64 decode data package.
     *
     * @param base64DataPackage the base64 data package
     * @return the list
     * @throws CrySILException the crysil exception
     */
    private List<List<byte[]>> base64DecodeDataPackage(List<List<String>> base64DataPackage) throws CrySILException {
        List<List<byte[]>> rawDataPackage = new ArrayList<>();

        for (List<String> base64DataList : base64DataPackage) {
            rawDataPackage.add(base64DecodeData(base64DataList));
        }

        return rawDataPackage;
    }

    /**
     * Convert certificates.
     *
     * @param certificates the certificates
     * @return the list
     */
    private List<Key> convertCertificates(List<Certificate> certificates) {
        List<Key> encryptionKeys = new ArrayList<>();

        for (Certificate certificate : certificates) {
            ExternalCertificate ExternalCertificate = new ExternalCertificate();
            try {
            	ExternalCertificate.setCertificate((java.security.cert.X509Certificate) certificate);
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
            encryptionKeys.add(ExternalCertificate);
        }

        return encryptionKeys;
    }

    /**
     * Gets the last request.
     *
     * @return the last request
     */
    public Request getLastRequest() {
        return lastRequest;
    }

    /**
     * Gets the last response.
     *
     * @return the last response
     */
    public Response getLastResponse() {
        return lastResponse;
    }

    /**
     * Discover keys.
     *
     * @param representation the representation
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<Key> discoverKeys(String representation) throws CrySILException {
        Request request = createBasicRequest();

        PayloadDiscoverKeysRequest payload = new PayloadDiscoverKeysRequest();
        
        KeyRepresentation temp = null;
        switch(representation){
        	case "certificate":
        		temp = KeyRepresentation.CERTIFICATE;
        		break;
        	case "handle":
        		temp = KeyRepresentation.HANDLE;
        		break;
        	case "unknown":
        		temp = KeyRepresentation.UNKNOWN;
        		break;
        	case "wrapped":
        		temp = KeyRepresentation.WRAPPED;
        		break;
        	default:
			try {
				throw new Exception("key representation not supported: " + representation);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        payload.setRepresentation(temp);
        request.setPayload(payload);

        Response res = forwardRequest(request);
        if (res == null) {
            return new ArrayList<>();
        }

        return ((PayloadDiscoverKeysResponse) res.getPayload()).getKey();
    }

    /**
     * Gets the certificate.
     *
     * @param keyIdentifier the key identifier
     * @return the certificate
     * @throws CrySILException the crysil exception
     */
    public X509Certificate getCertificate(KeyHandle keyIdentifier) throws CrySILException {
        InternalCertificate certificate = (InternalCertificate) getKey("certificate", keyIdentifier);
        if (certificate == null) {
            return null;
        }

        try {
        	return new X509Certificate(certificate.getCertificate().getEncoded());
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the key.
     *
     * @param representation the representation
     * @param keyIdentifier the key identifier
     * @return the key
     * @throws CrySILException the crysil exception
     */
    public Key getKey(String representation, KeyHandle keyIdentifier) throws CrySILException {
        Request request = createBasicRequest();

        PayloadGetKeyRequest payload = new PayloadGetKeyRequest();
        payload.setRepresentation(representation);
        payload.setKey(keyIdentifier);
        request.setPayload(payload);

        Response res = forwardRequest(request);
        if (res == null) {
            return null;
        }

        return ((PayloadGetKeyResponse) res.getPayload()).getKey();
    }

    // Encrypt/decrypt base raw
    /**
     * Encrypt data request.
     *
     * @param algorithm the algorithm
     * @param inputDataList the input data list
     * @param encryptionKeys the encryption keys
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<List<byte[]>> encryptDataRequest(String algorithm, List<byte[]> inputDataList, List<Key> encryptionKeys)
            throws CrySILException {
        Request request = createBasicRequest();

        PayloadEncryptRequest PayloadEncryptRequest = new PayloadEncryptRequest();
        PayloadEncryptRequest.setAlgorithm(algorithm);
        PayloadEncryptRequest.setEncryptionKeys(encryptionKeys);
        PayloadEncryptRequest.setPlainData(inputDataList);
        request.setPayload(PayloadEncryptRequest);

        Response res = forwardRequest(request);
        if (res == null) {
            return new ArrayList<>();
        }

        return ((PayloadEncryptResponse) res.getPayload()).getEncryptedData();
    }

    /**
     * Encrypt data request with certificates.
     *
     * @param algorithm the algorithm
     * @param inputDataList the input data list
     * @param certificates the certificates
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<List<byte[]>> encryptDataRequestWithCertificates(String algorithm, List<byte[]> inputDataList,
                                                                 List<Certificate> certificates) throws CrySILException {
        List<Key> encryptionKeys = convertCertificates(certificates);

        return encryptDataRequest(algorithm, inputDataList, encryptionKeys);
    }

    /**
     * Decrypt data request.
     *
     * @param algorithm the algorithm
     * @param inputDataList the input data list
     * @param decryptionKey the decryption key
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> decryptDataRequest(String algorithm, List<byte[]> inputDataList, Key decryptionKey)
            throws CrySILException {
        Request request = createBasicRequest();

        PayloadDecryptRequest PayloadDecryptRequest = new PayloadDecryptRequest();
        PayloadDecryptRequest.setAlgorithm(algorithm);
        PayloadDecryptRequest.setEncryptedData(inputDataList);
        PayloadDecryptRequest.setDecryptionKey(decryptionKey);
        request.setPayload(PayloadDecryptRequest);

        Response res = forwardRequest(request);

        if (res == null) {
            return new ArrayList<>();
        }

        return ((PayloadDecryptResponse) res.getPayload()).getPlainData();
    }

    // CMS
    /**
     * Encrypt cms data request.
     *
     * @param algorithm the algorithm
     * @param inputDataList the input data list
     * @param encryptionKeys the encryption keys
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> encryptCMSDataRequest(String algorithm, List<byte[]> inputDataList, List<Key> encryptionKeys)
            throws CrySILException {
        Request request = createBasicRequest();

        PayloadEncryptCMSRequest PayloadEncryptCMSRequest = new PayloadEncryptCMSRequest();
        PayloadEncryptCMSRequest.setAlgorithm(algorithm);
        PayloadEncryptCMSRequest.setEncryptionKeys(encryptionKeys);
        PayloadEncryptCMSRequest.setPlainData(inputDataList);
        request.setPayload(PayloadEncryptCMSRequest);

        Response res = forwardRequest(request);
        if (res == null)
            return new ArrayList<>();

        return ((PayloadEncryptCMSResponse) res.getPayload()).getEncryptedCMSData();
    }

    /**
     * Encrypt cms data request with certificates.
     *
     * @param algorithm the algorithm
     * @param inputDataList the input data list
     * @param certificates the certificates
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> encryptCMSDataRequestWithCertificates(String algorithm, List<byte[]> inputDataList,
                                                              List<Certificate> certificates) throws CrySILException {
        List<Key> encryptionKeys = convertCertificates(certificates);

        return encryptCMSDataRequest(algorithm, inputDataList, encryptionKeys);
    }

    /**
     * Decrypt cms data request.
     *
     * @param inputDataList the input data list
     * @param decryptionKey the decryption key
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> decryptCMSDataRequest(List<byte[]> inputDataList, Key decryptionKey)
            throws CrySILException {
        Request request = createBasicRequest();

        PayloadDecryptCMSRequest PayloadDecryptCMSRequest = new PayloadDecryptCMSRequest();
        PayloadDecryptCMSRequest.setEncryptedCMSData(inputDataList);
        PayloadDecryptCMSRequest.setDecryptionKey(decryptionKey);
        request.setPayload(PayloadDecryptCMSRequest);

        Response res = forwardRequest(request);
        if (res == null) {
            return new ArrayList<>();
        }

        return ((PayloadDecryptCMSResponse) res.getPayload()).getPlainData();
    }

    // Signatures
    /**
     * Sign hash request.
     *
     * @param algorithm the algorithm
     * @param hashesToBeSigned the hashes to be signed
     * @param signatureKey the signature key
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> signHashRequest(String algorithm, List<byte[]> hashesToBeSigned, Key signatureKey)
            throws CrySILException {
        Request request = createBasicRequest();

        PayloadSignRequest PayloadSignRequest = new PayloadSignRequest();
        PayloadSignRequest.setAlgorithm(algorithm);
        PayloadSignRequest.setHashesToBeSigned(hashesToBeSigned);
        PayloadSignRequest.setSignatureKey(signatureKey);
        request.setPayload(PayloadSignRequest);

        Response res = forwardRequest(request);
        if (res == null) {
            return new ArrayList<>();
        }

        return ((PayloadSignResponse) res.getPayload()).getSignedHashes();
    }

    /**
     * Generate wrapped key.
     *
     * @param keyType the key type
     * @param encryptionKeys the encryption keys
     * @param certificateSubject the certificate subject
     * @param optionalSigningKey the optional signing key
     * @return the wrapped key
     * @throws CrySILException the crysil exception
     */
    public WrappedKey generateWrappedKey(String keyType, List<Key> encryptionKeys, String certificateSubject,
                                         Key optionalSigningKey) throws CrySILException {
        Request request = createBasicRequest();

        PayloadGenerateWrappedKeyRequest PayloadGenerateWrappedKeyRequest = new PayloadGenerateWrappedKeyRequest();
        PayloadGenerateWrappedKeyRequest.setEncryptionKeys(encryptionKeys);
        PayloadGenerateWrappedKeyRequest.setKeyType(keyType);
        PayloadGenerateWrappedKeyRequest.setSigningKey(optionalSigningKey);
        PayloadGenerateWrappedKeyRequest.setCertificateSubject(certificateSubject);
        request.setPayload(PayloadGenerateWrappedKeyRequest);

        Response res = forwardRequest(request);
        if (res == null) {
            return null;
        }

        PayloadGenerateWrappedKeyResponse PayloadGenerateWrappedKeyResponse = (PayloadGenerateWrappedKeyResponse)res.getPayload();
        try {
            WrappedKey wrappedKey = new WrappedKey();
            wrappedKey.setWrappedKey(Util.fromBase64String(PayloadGenerateWrappedKeyResponse.getEncodedWrappedKey()));
            wrappedKey.setWrappedKeyCertificate(new X509Certificate(new ByteArrayInputStream(PayloadGenerateWrappedKeyResponse.getCertificate().getEncoded())));
            
            return wrappedKey;
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Modify wrapped key.
     *
     * @param wrappedKey the wrapped key
     * @param encryptionKeys the encryption keys
     * @param optionalDecryptionKey the optional decryption key
     * @param optionalSigningKey the optional signing key
     * @return the wrapped key
     * @throws CrySILException the crysil exception
     */
    public WrappedKey modifyWrappedKey(WrappedKey wrappedKey, List<Key> encryptionKeys, Key optionalDecryptionKey,
                                       Key optionalSigningKey) throws CrySILException {
        Request request = createBasicRequest();

        PayloadModifyWrappedKeyRequest PayloadModifyWrappedKeyRequest = new PayloadModifyWrappedKeyRequest();
        PayloadModifyWrappedKeyRequest.setEncodedWrappedKey(cleanBase64Load(Util.toBase64String(wrappedKey.getWrappedKey())));
        PayloadModifyWrappedKeyRequest.setEncryptionKeys(encryptionKeys);
        PayloadModifyWrappedKeyRequest.setDecryptionKey(optionalDecryptionKey);
        PayloadModifyWrappedKeyRequest.setSigningKey(optionalSigningKey);
        request.setPayload(PayloadModifyWrappedKeyRequest);

        Response res = forwardRequest(request);
        if (res == null) {
            return null;
        }

        PayloadModifyWrappedKeyResponse PayloadModifyWrappedKeyResponse = (PayloadModifyWrappedKeyResponse) res.getPayload();
        try {
            WrappedKey modifiedWrappedKey = new WrappedKey();
            modifiedWrappedKey.setWrappedKey(Util.fromBase64String(PayloadModifyWrappedKeyResponse.getEncodedWrappedKey()));
            modifiedWrappedKey.setWrappedKeyCertificate(new X509Certificate(new ByteArrayInputStream(PayloadModifyWrappedKeyResponse.getCertificate().getEncoded())));
            
            return modifiedWrappedKey;
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Export wrapped key.
     *
     * @param wrappedKey the wrapped key
     * @param optionalDecryptionKey the optional decryption key
     * @return the exported key
     * @throws javax.security.cert.CertificateException 
     * @throws javax.security.cert.CertificateEncodingException 
     * @throws CrySILException the crysil exception
     */
    public ExportedKey exportWrappedKey(WrappedKey wrappedKey, Key optionalDecryptionKey) throws CrySILException {
        Request request = createBasicRequest();

        PayloadExportWrappedKeyRequest PayloadExportWrappedKeyRequest = new PayloadExportWrappedKeyRequest();
        PayloadExportWrappedKeyRequest.setEncodedWrappedKey(cleanBase64Load(Util.toBase64String(wrappedKey.getWrappedKey())));
        PayloadExportWrappedKeyRequest.setDecryptionKey(optionalDecryptionKey);
        request.setPayload(PayloadExportWrappedKeyRequest);

        Response res = forwardRequest(request);
        if (res == null) {
            return null;
        }

        PayloadExportWrappedKeyResponse PayloadExportWrappedKeyResponse = (PayloadExportWrappedKeyResponse) res.getPayload();
        try {
            ExportedKey exportedKey = new ExportedKey();
            exportedKey.setPrivateKey(new PKCS8EncodedKeySpec(Util.fromBase64String(PayloadExportWrappedKeyResponse.getEncodedPrivateKey())));
            exportedKey.setX509Certificate(new X509Certificate(new ByteArrayInputStream(PayloadExportWrappedKeyResponse.getCertificate().getEncoded())));
            
            return exportedKey;
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets the current command id.
     *
     * @param currentCommandID the new current command id
     */
    public void setCurrentCommandID(String currentCommandID) {
        this.currentCommandID = currentCommandID;
    }

    /**
     * Clean base64 load.
     *
     * @param response the response
     * @return the string
     */
    private static String cleanBase64Load(String response) {
        return response.replace("\r", "").replace("\n", "");
    }
}
