package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/16/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SKeyMetaInformation {
    protected String type="genericMetaInformation";
    protected String genericMetaInformation="";

    public void setType(String type) {
        this.type = type;
    }

    public String getGenericMetaInformation() {
        return genericMetaInformation;
    }

    public void setGenericMetaInformation(String genericMetaInformation) {
        this.genericMetaInformation = genericMetaInformation;
    }
}
