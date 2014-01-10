package at.iaik.skytrust.common;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/17/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public enum SkyTrustAlgorithm {
    RSAES_RAW("RSAES-RAW"),
    RSAES_PKCS1_V1_5("RSAES-PKCS1-v1_5"),
    RSA_OAEP("RSA-OAEP"),
    RSASSA_PKCS1_V1_5_SHA_1("RSASSA-PKCS1-v1_5-SHA-1"),
    RSASSA_PKCS1_V1_5_SHA_224("RSASSA-PKCS1-v1_5-SHA-224"),
    RSASSA_PKCS1_V1_5_SHA_256("RSASSA-PKCS1-v1_5-SHA-256"),
    RSASSA_PKCS1_V1_5_SHA_512("RSASSA-PKCS1-v1_5-SHA-512"),

    RSA_PSS("RSA-PSS");


    private SkyTrustAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    protected String algorithmName;

    public String getAlgorithmName() {
        return algorithmName;
    }


}
