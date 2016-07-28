package org.crysil.actor.pkcs11.model;

public class KeyPairRepresentation {
    protected String encodedKey;
    protected String encodedX509Certificate;

    public String getEncodedKey() {
        return encodedKey;
    }

    public void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }

    public String getEncodedX509Certificate() {
        return encodedX509Certificate;
    }

    public void setEncodedX509Certificate(String encodedX509Certificate) {
        this.encodedX509Certificate = encodedX509Certificate;
    }
}
