package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:28 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("cryptoOperationRequest")
public class SPayloadCryptoOperationRequest extends SPayloadRequest {
    protected SCryptoParams cryptoParams;
    protected String load="";

    @JsonIgnore
    @Override
    public String getType() {
        return "cryptoOperationRequest";
    }


    public SCryptoParams getCryptoParams() {
        return cryptoParams;
    }

    public void setCryptoParams(SCryptoParams cryptoParams) {
        this.cryptoParams = cryptoParams;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }
}
