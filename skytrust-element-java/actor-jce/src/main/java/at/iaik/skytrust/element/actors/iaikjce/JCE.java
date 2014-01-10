package at.iaik.skytrust.element.actors.iaikjce;

import iaik.x509.X509Certificate;

/**
 * The iaikjce interface contains all methods to communicate with a software key
 * store
 */
public interface JCE {


    /**
     * Get certificate from given alias
     *
     * @param userId user name
     * @return a certificate if user exits null otherwise
     */
    public X509Certificate getCertificate(String keyId, String subKeyId, String userId);

    /**
     * Decrypt a file
     *
     * @param data      encrypted file to decrypt
     * @param keyId     user name
     * @param algorithm which algorithm should be used
     * @return decrypted file
     */
    public byte[] decrypt(byte[] data, String keyId, String subKeyId, String userId, String algorithm);

    /**
     * Signs a file
     *
     * @param data      encrypted file to decrypt
     * @param keyId     user name
     * @param algorithm which algorithm should be used
     * @return signed file
     */
    public byte[] sign(byte[] data, String keyId, String subKeyId, String userId, String algorithm);

    /**
     * Encrypt a file
     *
     * @param data      file to encrypt
     * @param userId    user name
     * @param algorithm which algorithm should be used
     * @return encrypted file
     */
    public byte[] encrypt(byte[] data, String keyId, String subKeyId, String userId, String algorithm);





}
