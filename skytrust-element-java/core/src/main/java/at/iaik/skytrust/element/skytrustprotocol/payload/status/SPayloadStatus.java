package at.iaik.skytrust.element.skytrustprotocol.payload.status;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("status")
public class SPayloadStatus extends SPayloadResponse {
    protected int code;

    @JsonIgnore
    @Override
    public String getType() {
        return "status";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
