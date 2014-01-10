package at.iaik.skytrust.element.receiver.http;

import at.iaik.skytrust.element.SkytrustElement;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


@Controller
public class SkyTrustProtocolHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private SkytrustElement element = SkytrustElement.create("seserver");

    @RequestMapping(value = "/json", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public SResponse handleSkyTrustCommand(@RequestBody SRequest skyTrustRequest) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        try {
            String requestJson = ow.writeValueAsString(skyTrustRequest);
            logger.error("Incoming request:" + new String(requestJson));

			SResponse skyTrustResponse = ((HTTPReceiver) element.getReceiver("HTTPReceiver")).forwardRequest(skyTrustRequest);

            String responseJson = ow.writeValueAsString(skyTrustResponse);
            logger.error("Created response: " + responseJson);

            return skyTrustResponse;
        } catch (IOException e) {
            logger.error("severe error while handling skytrust request", e);
        }
        return null;
    }
}
