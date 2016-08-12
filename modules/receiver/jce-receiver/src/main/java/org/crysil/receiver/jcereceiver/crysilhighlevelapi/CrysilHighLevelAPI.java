/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.payload.crypto.key.ExternalCertificate;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.receiver.jcereceiver.crysil.CrysilAPI;
import org.crysil.receiver.jcereceiver.crysil.ExportedKey;
import org.crysil.receiver.jcereceiver.crysil.WrappedKey;

/** This API provides the Crysil functionality. The API should be used for functions that are not available
 *  within the Crysil provider.
 *  Initialization of the CrysilHighLevelAPI is executed as follows
 *  <pre>
 *  {@code
 *  CrysilAPIFactory.initialize("http://crysil-instance.example.com/rest/json");
 *  CrysilHighLevelAPI crysilHighLevelAPI = CrysilHighLevelAPI.getInstance();}
 *  </pre>
 *  The returned CrysilHighLevelAPI is a singleton.
 */
public class CrysilHighLevelAPI {
    
    /** The crysil api. */
    protected CrysilAPI crysilAPI;

    /**
     * Hidden constructor.
     */
    protected CrysilHighLevelAPI() {
    }

    /**
     * Constructor that is used for initializing the high-level API when the low-level API is already available.
     * @param crysilAPI
     * A low-level API instance.
     */
    public CrysilHighLevelAPI(CrysilAPI crysilAPI) {
        this.crysilAPI = crysilAPI;
    }

    /**
     * Internal class for holding the CrysilHighLevelAPI singleton.
     */
    private static class APIHolder {
        
        /** The Constant INSTANCE. */
        private static final CrysilHighLevelAPI INSTANCE = new CrysilHighLevelAPI(CrysilAPI.getInstance());
    }

    /**
     * Returns a singleton of the CrysilHighLevelAPI.
     * The call assumes that the API has already been initialized via
     * CrysilAPIFactory.initialize("http://crysil-instance.example.com/json");
     *
     * @return
     * The CrysilHighLevelAPI singleton is returned
     */
    public static CrysilHighLevelAPI getInstance() {
        return APIHolder.INSTANCE;
    }

    /**
     * Helper method that removes \r\n from BASE64 strings.
     *
     * @param base64String The BASE64 string containing \r\n chars.
     * @return The cleaned BASE64 string.
     */
    private static String cleanBase64Load(String base64String) {
        return base64String.replace("\r", "").replace("\n", "");
    }

    /**
     * This method retrieves the available keys from the Crysil node.
     * {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.KeyRepresentation CERTIFICATE} instructs Crysil to return keys of the type {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyInternalCertificate}, which includes
     * the key id, key subId and the {@link java.security.cert.X509Certificate} associated with the key.
     * {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.KeyRepresentation HANDLE} instructs Crysil to return keys of the type {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle}, which includes
     * the key id, subID. The certificate is not included.
     *
     * @param keyRepresentation This parameter defines the keyRepresentation of the returned high-level API key models.
     * @return The high-level API key models in the given keyRepresentation.
     * @throws CrySILException the crysil exception
     */
    public List<CrysilKey> discoverKeys(KeyRepresentation keyRepresentation) throws CrySILException {
        List<Key> keys = crysilAPI.discoverKeys(keyRepresentation.getHandleAsString());

        List<CrysilKey> crysilKeys = new ArrayList<>();
        for (Key Key : keys) {
            crysilKeys.add(convertKeyToCrysilKey(Key));
        }

        return crysilKeys;
    }

    /**
     * This method allows the retrieval of a specific key from the Crysil server.
     * In the current implementation this is used when a key is available in the {@link CrysilKeyHandle} representation - meaning the
     * key ID and key subID are known - and the certificate for the key should be returned {@link CrysilKeyInternalCertificate}).
     * Thus (currently), the only reasonable use case for this method is by setting keyRepresentation to {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.KeyRepresentation} CERTIFICATE.
     *
     * @param keyRepresentation This parameter defines the keyRepresentation of the returned high-level API key model.
     * @param crysilKeyHandle high-level API key model, for which the selected keyRepresentation should be returned.
     * @return A list of high-level API models available on the Crysil instance.
     * @throws CrySILException the crysil exception
     */
    public CrysilKeyHandle getKey(KeyRepresentation keyRepresentation, CrysilKeyHandle crysilKeyHandle) throws CrySILException {
        Key Key = crysilAPI.getKey(keyRepresentation.getHandleAsString(), (KeyHandle) convertCrysilKeyToKey(crysilKeyHandle, false));
        return (CrysilKeyHandle) convertKeyToCrysilKey(Key);
    }

    /**
     * Specific call to the generic encryption method (see below).
     *
     * @param algorithm the algorithm
     * @param plainData the plain data
     * @param x509Certificate the x509 certificate
     * @return the byte[]
     * @throws CrySILException the crysil exception
     */
    public byte[] encryptDataRequestWithCertificates(String algorithm, byte[] plainData, X509Certificate x509Certificate) throws CrySILException {
        List<byte[]> plainDataList = new ArrayList<>();
        plainDataList.add(plainData);

        List<X509Certificate> x509CertificateList = new ArrayList<>();
        x509CertificateList.add(x509Certificate);

        return encryptDataRequestWithCertificates(algorithm, plainDataList, x509CertificateList).get(0).get(0);
    }

    /**
     * Specific call to the generic encryption method (see below).
     *
     * @param algorithm the algorithm
     * @param plainData the plain data
     * @param x509CertificateList the x509 certificate list
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> encryptDataRequestWithCertificates(String algorithm, byte[] plainData, List<X509Certificate> x509CertificateList) throws CrySILException {
        List<byte[]> plainDataList = new ArrayList<>();
        plainDataList.add(plainData);

        return encryptDataRequestWithCertificates(algorithm, plainDataList, x509CertificateList).get(0);
    }

    /**
     * Specific call to the generic encryption method (see below).
     *
     * @param algorithm the algorithm
     * @param plainDataList the plain data list
     * @param x509Certificate the x509 certificate
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> encryptDataRequestWithCertificates(String algorithm, List<byte[]> plainDataList, X509Certificate x509Certificate) throws CrySILException {
        List<X509Certificate> x509CertificateList = new ArrayList<>();
        x509CertificateList.add(x509Certificate);

        return encryptDataRequestWithCertificates(algorithm, plainDataList, x509CertificateList).get(0);
    }

    /**
     * This method encrypts the given data for the given recipients.
     *
     * @param algorithm The {@link common.CrysilAlgorithm} as string for encrypting the given plain data.
     * @param plainDataList The raw plainData that shall be encrypted
     * @param x509CertificateList The List of {@link X509Certificate} which represent the recipients for whom the data will be encrypted.
     * The key type within the certificate needs to correspond to the selected algorithm.
     * @return A matrix (List of List)
     * The outer list represents the encrypted plain texts (same order as given in plainDataList).
     * The inner list represents the different recipients (same order as given in x509CertificateList).
     * @throws CrySILException the crysil exception
     */
    public List<List<byte[]>> encryptDataRequestWithCertificates(String algorithm, List<byte[]> plainDataList, List<X509Certificate> x509CertificateList) throws CrySILException {
        List<CrysilKey> encryptionKeys = new ArrayList<>();
        for (X509Certificate x509Certificate : x509CertificateList) {
            CrysilKeyExternalCertificate crysilKeyExternalCertificate = new CrysilKeyExternalCertificate(x509Certificate);
            encryptionKeys.add(crysilKeyExternalCertificate);
        }

        return encryptDataRequest(algorithm, plainDataList, encryptionKeys);
    }


    /**
     * Specific call to the generic encryption method (see below).
     *
     * @param algorithm the algorithm
     * @param plainData the plain data
     * @param encryptionKey the encryption key
     * @return the byte[]
     * @throws CrySILException the crysil exception
     */
    public byte[] encryptDataRequest(String algorithm, byte[] plainData, CrysilKey encryptionKey) throws CrySILException {
        List<byte[]> plainDataList = new ArrayList<>();
        plainDataList.add(plainData);
        List<Key> KeyEncryptionkeys = new ArrayList<>();
        KeyEncryptionkeys.add(convertCrysilKeyToKey(encryptionKey, true));

        return crysilAPI.encryptDataRequest(algorithm, plainDataList, KeyEncryptionkeys).get(0).get(0);
    }

    /**
     * Specific call to the generic encryption method (see below).
     *
     * @param algorithm the algorithm
     * @param plainDataList the plain data list
     * @param encryptionKey the encryption key
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> encryptDataRequest(String algorithm, List<byte[]> plainDataList, CrysilKey encryptionKey) throws CrySILException {
        List<Key> KeyEncryptionkeys = new ArrayList<>();
        KeyEncryptionkeys.add(convertCrysilKeyToKey(encryptionKey, true));

        return crysilAPI.encryptDataRequest(algorithm, plainDataList, KeyEncryptionkeys).get(0);
    }

    /**
     * Specific call to the generic encryption method (see below).
     *
     * @param algorithm the algorithm
     * @param plainData the plain data
     * @param encryptionKeyList the encryption key list
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<List<byte[]>> encryptDataRequest(String algorithm, byte[] plainData, List<CrysilKey> encryptionKeyList) throws CrySILException {
        List<byte[]> plainDataList = new ArrayList<>();
        plainDataList.add(plainData);

        return crysilAPI.encryptDataRequest(algorithm, plainDataList, convertCrysilKeyToKey(encryptionKeyList, true));
    }

    /**
     * This method encrypts the given data for the given recipients.
     *
     * @param algorithm
     *            The {@link common.CrysilAlgorithm} as string for encrypting the given plain data.
     * @param plainDataList
     *            The raw plainData that will be encrypted
     * @param encryptionKeyList
     *            The list of high-level API Crysil key models that represent the recipients <br>
     * <br>
     *            Possible types: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle} <br>
     *            {@link org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilInternalCertificate} <br>
     *            {@link org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilExternalCertificate} <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilWrappedKey} <br>
     * @return A matrix (List of List)
     *         The outer list represents the encrypted plain texts (same order as given in plainDataList).
     *         The inner list represents the different recipients (same order as given in x509CertificateList).
     * @throws CrySILException
     *             the crysil exception
     */
    public List<List<byte[]>> encryptDataRequest(String algorithm, List<byte[]> plainDataList, List<CrysilKey> encryptionKeyList) throws CrySILException {
        List<Key> encryptionKeys = convertCrysilKeyToKey(encryptionKeyList, true);

        return crysilAPI.encryptDataRequest(algorithm, plainDataList, encryptionKeys);
    }

    /**
     * specific call to the generic decryption method (see below).
     *
     * @param algorithm the algorithm
     * @param encryptedData the encrypted data
     * @param decryptionKey the decryption key
     * @return the byte[]
     * @throws CrySILException the crysil exception
     */
    public byte[] decryptDataRequest(String algorithm, byte[] encryptedData, CrysilKey decryptionKey) throws CrySILException {
        List<byte[]> encryptedDataList = new ArrayList<>();
        encryptedDataList.add(encryptedData);

        return crysilAPI.decryptDataRequest(algorithm, encryptedDataList, convertCrysilKeyToKey(decryptionKey, false)).get(0);
    }

    /**
     * This method decrypts the given encrypted data with the selected high-level API key model.
     *
     * @param algorithm
     *            The {@link common.CrysilAlgorithm} as string for decrypting the given encrypted data.
     * @param encryptedDataList
     *            The raw encrypted data.
     * @param decryptionKey
     *            A high-level API key model that specifies the Crysil decryption key for decrypting the data. <br>
     * <br>
     *            Possible types: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle} <br>
     *            {@link org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilInternalCertificate} <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilWrappedKey} <br>
     * <br>
     *            Not possible: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyExternalCertificate} <br>
     * @return A list of the plain data elements that were decrypted. The order of the elements is the same as in the encrypted input data list.
     * @throws CrySILException
     *             the crysil exception
     */
    public List<byte[]> decryptDataRequest(String algorithm, List<byte[]> encryptedDataList, CrysilKey decryptionKey) throws CrySILException {
        return crysilAPI.decryptDataRequest(algorithm, encryptedDataList, convertCrysilKeyToKey(decryptionKey, false));
    }


    /**
     * specific call to the generic signature method (see below).
     *
     * @param algorithm the algorithm
     * @param hashToBeSigned the hash to be signed
     * @param signatureKey the signature key
     * @return the byte[]
     * @throws CrySILException the crysil exception
     */
    public byte[] signHashRequest(String algorithm, byte[] hashToBeSigned, CrysilKey signatureKey) throws CrySILException {
        List<byte[]> hashesToBeSigned = new ArrayList<>();
        hashesToBeSigned.add(hashToBeSigned);

        return crysilAPI.signHashRequest(algorithm, hashesToBeSigned, convertCrysilKeyToKey(signatureKey, false)).get(0);
    }

    /**
     * Sign hash request.
     *
     * @param algorithm
     *            The {@link common.CrysilAlgorithm} as string for signing the given hash values.
     * @param hashesToBeSignedList
     *            The hash values that will be signed by the selected signatureKey.
     * @param signatureKey
     *            The high-level API key model which is used for signing the hash values. <br>
     * <br>
     *            Possible types: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle} <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyInternalCertificate} <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilWrappedKey} <br>
     * <br>
     *            Not possible: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyExternalCertificate} <br>
     * @return A list of signed hash values. The order of the elements is the same as in the input hash list.
     * @throws CrySILException
     *             the crysil exception
     */
    public List<byte[]> signHashRequest(String algorithm, List<byte[]> hashesToBeSignedList, CrysilKey signatureKey) throws CrySILException {
        return crysilAPI.signHashRequest(algorithm, hashesToBeSignedList, convertCrysilKeyToKey(signatureKey, false));
    }

    /**
     * For internal/future use (CMS encryption). Method most likely will change.
     *
     * @param algorithm the algorithm
     * @param plainDataList the plain data list
     * @param encryptionKeyList the encryption key list
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> encryptCMSDataRequest(String algorithm, List<byte[]> plainDataList, List<CrysilKey> encryptionKeyList) throws CrySILException {
        return crysilAPI.encryptCMSDataRequest(algorithm, plainDataList, convertCrysilKeyToKey(encryptionKeyList, true));
    }

    /**
     * For internal/future use (CMS encryption). Method most likely will change.
     *
     * @param algorithm the algorithm
     * @param plainData the plain data
     * @param encryptionKey the encryption key
     * @return the byte[]
     * @throws CrySILException the crysil exception
     */
    public byte[] encryptCMSDataRequest(String algorithm, byte[] plainData, CrysilKey encryptionKey) throws CrySILException {
        List<byte[]> plainDataList = new ArrayList<>();
        plainDataList.add(plainData);
        List<CrysilKey> encryptionKeyList = new ArrayList<>();
        encryptionKeyList.add(encryptionKey);

        return encryptCMSDataRequest(algorithm, plainDataList, encryptionKeyList).get(0);
    }

    /**
     * For internal/future use (CMS encryption). Method most likely will change.
     *
     * @param encryptedDataList the encrypted data list
     * @param decryptionKey the decryption key
     * @return the list
     * @throws CrySILException the crysil exception
     */
    public List<byte[]> decryptCMSDataRequest(List<byte[]> encryptedDataList, CrysilKey decryptionKey) throws CrySILException {
        return crysilAPI.decryptCMSDataRequest(encryptedDataList, convertCrysilKeyToKey(decryptionKey, false));
    }

    /**
     * For internal/future use (CMS encryption). Method most likely will change.
     *
     * @param encryptedData the encrypted data
     * @param decryptionKey the decryption key
     * @return the byte[]
     * @throws CrySILException the crysil exception
     */
    public byte[] decryptCMSDataRequest(byte[] encryptedData, CrysilKey decryptionKey) throws CrySILException {
        List<byte[]> encryptedDataList = new ArrayList<>();
        encryptedDataList.add(encryptedData);

        return decryptCMSDataRequest(encryptedDataList, decryptionKey).get(0);
    }

    /**
     * Generate an assymetric key pair (including the X509Certificate)
     * The key pair and the certificate are stored in a container (the
     * {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilWrappedKey}) that
     * is signed by the default signing key specified in the Crysil configuration and
     * is encrypted by the high-level API key models handed over in the encryptionKeyList.
     * The subject of the certificate is defined by the certificateSubject parameter according to
     * http://tools.ietf.org/html/rfc4514. e.g., CN=My Subject name/O=IAIK
     *
     * @param keyType
     *            The key that will be generated (according to the key types in @link{common.CrysilAlgorithm}
     *            RSA-2048, RSA-4096
     * @param encryptionKeyList
     *            The list of high-level API key models for whom the wrapped key will be encrypted. The certificates represented
     *            by these key models will be validated according to the trust/cert stores defined in the crysil configuration <br>
     * <bR>
     *            Possible types: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle} <br>
     *            {@link org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilInternalCertificate} <br>
     *            {@link org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilExternalCertificate} <br>
     * <br>
     *            Not possible: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilWrappedKey} <br>
     * @param certificateSubject
     *            The subject name encoded according to http://tools.ietf.org/html/rfc4514. e.g., CN=My Subject name/O=IAIK
     * @return The generated wrapped key.
     * @throws CrySILException
     *             the crysil exception
     */
    public CrysilWrappedKey generateWrappedKey(String keyType, List<CrysilKey> encryptionKeyList, String certificateSubject) throws CrySILException {
        WrappedKey wrappedKey = crysilAPI.generateWrappedKey(keyType, convertCrysilKeyToKey(encryptionKeyList, true), certificateSubject, null);

        return (CrysilWrappedKey) convertKeyToCrysilKey(wrappedKey);
    }

    /**
     * This method modifies the recipients for whom the given wrapped key is encrypted. Except for the missing certificateSubject
     * parameter the method corresponds to the generateWrappedKey method.
     *
     * @param wrappedKey
     *            The existing wrappedKey
     * @param encryptionKeyList
     *            The list of high-level API key models for whom the wrapped key will be encrypted. The certificates represented
     *            by these key models will be validated according to the trust/cert stores defined in the crysil configuration
     * 
     * <br>
     * <br>
     *            Possible types: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle} <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyInternalCertificate} <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyExternalCertificate} <br>
     * <br>
     *            Not possible: <br>
     *            {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilWrappedKey} <br>
     * @return The modified wrapped key.
     * @throws CrySILException
     *             the crysil exception
     */
    public CrysilWrappedKey modifyWrappedKey(CrysilWrappedKey wrappedKey, List<CrysilKey> encryptionKeyList) throws CrySILException {
        WrappedKey convertedWrappedKey = wrappedKey.getInternalRepresentation();
        WrappedKey modifiedWrappedKey = crysilAPI.modifyWrappedKey(convertedWrappedKey, convertCrysilKeyToKey(encryptionKeyList, true), null, null);

        return (CrysilWrappedKey) convertKeyToCrysilKey(modifiedWrappedKey);
    }

    /**
     * This method instructs Crysil to decrypt the given wrapped key and return the plain
     * key pair. This method is typically tied to strong authentication mechanisms to avoid mis-use.
     *
     * @param wrappedKey The wrapped key that should be exported.
     * @return The plain key pair.
     * @throws CrySILException the crysil exception
     */
    public CrysilExportedKey exportWrappedKey(CrysilWrappedKey wrappedKey) throws CrySILException {
        WrappedKey convertedWrappedKey = wrappedKey.getInternalRepresentation();
        ExportedKey exportedKey = crysilAPI.exportWrappedKey(convertedWrappedKey, null);

        return (CrysilExportedKey) convertKeyToCrysilKey(exportedKey);
    }

    /**
     * internal helper method that converts low-level API key models to high-level API key models.
     *
     * @param KeyList the key list
     * @return the list
     */
    protected List<CrysilKey> convertKeyToCrysilKey(List<Key> KeyList) {
        List<CrysilKey> crysilKeys = new ArrayList<>();
        for (Key Key : KeyList) {
            crysilKeys.add(convertKeyToCrysilKey(Key));
        }

        return crysilKeys;
    }

    /**
     * internal helper method that converts high level key models to low-level API key models.
     *
     * @param Key the key
     * @return the crysil key
     */
    protected CrysilKey convertKeyToCrysilKey(Key Key) {
        if (Key instanceof InternalCertificate) {
            return new CrysilKeyInternalCertificate((InternalCertificate) Key);
        } else if (Key instanceof KeyHandle) {
            return new CrysilKeyHandle((KeyHandle) Key);
        } else if (Key instanceof ExternalCertificate) {
            return new CrysilKeyExternalCertificate((ExternalCertificate) Key);
        } else if (Key instanceof WrappedKey) {
            return new CrysilWrappedKey((WrappedKey) Key);
        } else if (Key instanceof ExportedKey) {
            return new CrysilExportedKey((ExportedKey) Key);
        }
        return null;
    }

    /**
     * Internal helper method that converts high-level API key models to low-level API key models.
     * The boolean parameter specifies whether a high-level {@link CrysilWrappedKey} should be converted to
     * a low-level {@link ExternalCertificate} (only the {@link X509Certificate} of the wrapped key is included) or
     * a low-level {@link WrappedKey} (only the encoded wrapped key is included)
     *
     * @param crysilKeyList the crysil key list
     * @param representWrappedKeyAsCertificates the represent wrapped key as certificates
     * @return the list
     */
    protected List<Key> convertCrysilKeyToKey(List<CrysilKey> crysilKeyList, boolean representWrappedKeyAsCertificates) {
        List<Key> Keys = new ArrayList<>();
        for (CrysilKey crysilKey : crysilKeyList) {
            Keys.add(convertCrysilKeyToKey(crysilKey, representWrappedKeyAsCertificates));
        }

        return Keys;
    }

    /**
     * Internal helper method that converts high-level API key models to low-level API key models.
     * The boolean parameter specifies whether a high-level {@link CrysilWrappedKey} should be converted to
     * a low-level {@link ExternalCertificate} (only the {@link X509Certificate} of the wrapped key is included) or
     * a low-level {@link WrappedKey} (only the encoded wrapped key is included)
     *
     * @param crysilKey the crysil key
     * @param representWrappedKeyAsCertificates the represent wrapped key as certificates
     * @return the key
     */
    protected Key convertCrysilKeyToKey(CrysilKey crysilKey, boolean representWrappedKeyAsCertificates) {
        if (crysilKey == null) {
            return null;
        }

        if (crysilKey instanceof CrysilWrappedKey) {
            CrysilWrappedKey crysilWrappedKey = (CrysilWrappedKey) crysilKey;
            if (representWrappedKeyAsCertificates) {
                ExternalCertificate externalCertificate = new ExternalCertificate();
                try {
                	externalCertificate.setCertificate(crysilWrappedKey.getX509Certificate());
                    return externalCertificate;
                } catch (CertificateEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            } else {
            	org.crysil.protocol.payload.crypto.key.WrappedKey WrappedKey = new org.crysil.protocol.payload.crypto.key.WrappedKey();
            	WrappedKey.setEncodedWrappedKey(crysilWrappedKey.getEncodedWrappedKey());
                return WrappedKey;
            }
        } else {
            return crysilKey.getInternalRepresentation();
        }
    }

    /**
     * Sets the current command id.
     *
     * @param currentCommandID the new current command id
     */
    public void setCurrentCommandID(String currentCommandID) {
        crysilAPI.setCurrentCommandID(currentCommandID);
    }
}
