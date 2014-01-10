package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/16/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("certificate")
public class SKeyCertificate extends SKey {
    protected String encodedCertificate="";
    protected List<SKeyMetaInformation> metaInformation = new ArrayList<SKeyMetaInformation>();

    public String getEncodedCertificate() {
        return encodedCertificate;
    }

    @JsonIgnore
    @Override
    public String getRepresentation() {
        return "certificate";
    }

    public void setEncodedCertificate(String encodedCertificate) {
        this.encodedCertificate = encodedCertificate;
    }

    public List<SKeyMetaInformation> getMetaInformation() {
        return metaInformation;
    }

    public void setMetaInformation(List<SKeyMetaInformation> metaInformation) {
        this.metaInformation = metaInformation;
    }
}
