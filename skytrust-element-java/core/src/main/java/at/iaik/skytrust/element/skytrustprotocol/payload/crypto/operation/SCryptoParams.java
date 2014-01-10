package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 10:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SCryptoParams {
    protected SKey key;
    protected String algorithm="";

    public SKey getKey() {
        return key;
    }

    public void setKey(SKey key) {
        this.key = key;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
