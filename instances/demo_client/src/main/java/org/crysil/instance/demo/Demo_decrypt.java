package org.crysil.instance.demo;

import org.crysil.builders.PayloadBuilder;
import org.crysil.communications.http.HttpJsonTransmitter;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

public class Demo_decrypt {

  public static void main(final String[] args) throws CrySILException {
	  
	  //request
	  final Request request = new Request();

	  //create and set header
	  StandardHeader header = new StandardHeader();
	  header.setSessionId("sessionId");
	  request.setHeader(header);

	  //create and set payload
	  final PayloadRequest payload = PayloadBuilder.buildDecryptRequest(new KeyHandle(), "UgKSFG8R0meUoR3VbewR20MwrEJI3Nx2Qvdb2/htnPmTnTqx6+qacplg5jtMB6h8W4YaQZ1L3IEGJeLqI/fkPrvxoh95pIacpEJQzz3zyg1YGqtDo6NZARfMAYIj0COmpY2E1BcJmVbtUYW95DcqYt7Brsyse+lqZBkIo5WObpqBIhGkDcmw+5goRcY92/kpwV8YL8g8nMbqOJpq85vZfbmtR1rtoo1kWt+erUN4ThkQw7jaFvJmjSFAMpKql4OzdKb4NYrb28WA66VIKGqiHzcUDAJC5KGtp3a4UYKcAEW4mm+8vcMAjLwxbUGU7CPvRwyk3TQ+GCaVw5EA8ZopYw==");
	  request.setPayload(payload);

	  //json transmitter (http)
	  HttpJsonTransmitter httpJsonTransmitter = new HttpJsonTransmitter();
	  httpJsonTransmitter.setTargetURI("http://localhost:8080/demo_webservice/json");
	  final Response authedRequest = httpJsonTransmitter.take(request);

	  System.out.println(authedRequest.getPayload());
  }

}
