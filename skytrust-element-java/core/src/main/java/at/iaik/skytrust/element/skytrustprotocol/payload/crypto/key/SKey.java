package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/16/13
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "representation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SKeyHandle.class, name = "handle"),
        @JsonSubTypes.Type(value = SKeyCertificate.class, name = "certificate"),
        @JsonSubTypes.Type(value = SFullKey.class, name = "fullKey"),
        @JsonSubTypes.Type(value = SKey.class, name = "keyIdentifier")
})
@JsonTypeName("keyIdentifier")
public  class SKey {
    protected String id="";
    protected String subId="";
    protected String representation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    @JsonIgnore
    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

}
