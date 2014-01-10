package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/16/13
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("keyIdentifier")
public class SKeyIdentifier extends SKey {

    protected List<SKeyMetaInformation> metaInformation = new ArrayList<SKeyMetaInformation>();

    @JsonIgnore
    @Override
    public String getRepresentation() {
        return "keyIdentifier";
    }

    public List<SKeyMetaInformation> getMetaInformation() {
        return metaInformation;
    }

    public void setMetaInformation(List<SKeyMetaInformation> metaInformation) {
        this.metaInformation = metaInformation;
    }
}
