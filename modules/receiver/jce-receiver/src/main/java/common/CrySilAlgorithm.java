/*
 * CrySil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package common;

/**
 * The Enum CrySilAlgorithm.
 */
public enum CrySilAlgorithm {
    //basic assymetric algorithms
    /** The rsaes raw. */
    RSAES_RAW("RSAES-RAW"),
    
    /** The RSAE s_ pkc s1_ v1_5. */
    RSAES_PKCS1_V1_5("RSAES-PKCS1-v1_5"),
    
    /** The rsa oaep. */
    RSA_OAEP("RSA-OAEP"),
    
    /** The rsa pss. */
    RSA_PSS("RSA-PSS"),
    
    /** The RSASS a_ pkc s1_ v1_5_ sh a_1. */
    RSASSA_PKCS1_V1_5_SHA_1("RSASSA-PKCS1-v1_5-SHA-1"),
    
    /** The RSASS a_ pkc s1_ v1_5_ sh a_224. */
    RSASSA_PKCS1_V1_5_SHA_224("RSASSA-PKCS1-v1_5-SHA-224"),
    
    /** The RSASS a_ pkc s1_ v1_5_ sh a_256. */
    RSASSA_PKCS1_V1_5_SHA_256("RSASSA-PKCS1-v1_5-SHA-256"),
    
    /** The RSASS a_ pkc s1_ v1_5_ sh a_512. */
    RSASSA_PKCS1_V1_5_SHA_512("RSASSA-PKCS1-v1_5-SHA-512"),

    //key types
    /** The KEYTYP e_ rs a_2048. */
    KEYTYPE_RSA_2048("RSA-2048"),
    
    /** The KEYTYP e_ rs a_4096. */
    KEYTYPE_RSA_4096("RSA-4096"),

    //high level protocols


    //the SMIME algorithms, encrypt the message according to the CMS standard, with the given AES algorithm, the CMS result is embedded in a MIME message, resulting in an S/MIME email
    /** The SMIM e_ ae s_128. */
    SMIME_AES_128("SMIME-AES-128"), //use default algorithm for asymmetric key, use AES 128 CBC for symmetric encryption,
    /** The SMIM e_ ae s_192. */
 SMIME_AES_192("SMIME-AES-192"), //use default algorithm for asymmetric key, use AES 192 CBC for symmetric encryption
    /** The SMIM e_ ae s_256. */
 SMIME_AES_256("SMIME-AES-256"), //use default algorithm for asymmetric key, use AES 256 CBC for symmetric encryption
    /** The smime decrypt. */
 SMIME_DECRYPT("SMIME-DECRYPT"), //decrypt S-MIME container, algorithms are defined by the metainformation in the container

    // same as the S/MIME encryption options, however here the raw CMS container is returned
    // use default algorithm for asymmetric key, use AES for symmetric encryption
    /** The CM s_ ae s_128_ cbc. */
 CMS_AES_128_CBC("CMS-AES-128-CBC"),
    
    /** The CM s_ ae s_128_ ccm. */
    CMS_AES_128_CCM("CMS-AES-128-CCM"),
    
    /** The CM s_ ae s_128_ gcm. */
    CMS_AES_128_GCM("CMS-AES-128-GCM"),
    
    /** The CM s_ ae s_192_ cbc. */
    CMS_AES_192_CBC("CMS-AES-192-CBC"),
    
    /** The CM s_ ae s_192_ ccm. */
    CMS_AES_192_CCM("CMS-AES-192-CCM"),
    
    /** The CM s_ ae s_192_ gcm. */
    CMS_AES_192_GCM("CMS-AES-192-GCM"),
    
    /** The CM s_ ae s_256_ cbc. */
    CMS_AES_256_CBC("CMS-AES-256-CBC"),
    
    /** The CM s_ ae s_256_ ccm. */
    CMS_AES_256_CCM("CMS-AES-256-CCM"),
    
    /** The CM s_ ae s_256_ gcm. */
    CMS_AES_256_GCM("CMS-AES-256-GCM");

    /**
     * Instantiates a new crysil algorithm.
     *
     * @param algorithmName the algorithm name
     */
    private CrySilAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    /** The algorithm name. */
    protected String algorithmName;

    /**
     * Gets the algorithm name.
     *
     * @return the algorithm name
     */
    public String getAlgorithmName() {
        return algorithmName;
    }
}
