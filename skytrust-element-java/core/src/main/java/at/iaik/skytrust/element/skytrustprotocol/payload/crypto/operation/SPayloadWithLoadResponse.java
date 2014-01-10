package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("responseWithLoad")
public class SPayloadWithLoadResponse extends SPayloadResponse {
    protected String load="";

    @JsonIgnore
    @Override
    public String getType() {
        return "responseWithLoad";
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

}
