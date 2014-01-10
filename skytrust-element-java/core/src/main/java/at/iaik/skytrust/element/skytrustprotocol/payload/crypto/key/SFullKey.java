package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/16/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("fullKey")
public class SFullKey extends SKey {
    protected String encodedKey="";

    @JsonIgnore
    @Override
    public String getRepresentation() {
        return "fullKey";
    }

    public String getEncodedKey() {
        return encodedKey;
    }

    public void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }
}
