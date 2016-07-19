package org.crysil.instance.demo;

import org.crysil.communications.http.HttpJsonTransmitter;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;

public class Demo_discover_keys {
	
	static KeyRepresentation[] get_keyrepresentations(){
		return new KeyRepresentation[] { 
				KeyRepresentation.HANDLE,
				KeyRepresentation.CERTIFICATE
		};
	}

  public static void main(final String[] args) throws CrySILException {
	
	for (KeyRepresentation key_representation : get_keyrepresentations()) {
		//request
	    final Request request = new Request();
	    
	    //create and set header
	    StandardHeader header = new StandardHeader();
	    header.setSessionId("sessionId");
	    request.setHeader(header);
	    
	    //create and set payload
	    final PayloadDiscoverKeysRequest payload_discover_keys = new PayloadDiscoverKeysRequest();
	    //payload_discover_keys.setRepresentation("handle");
	    payload_discover_keys.setRepresentation(key_representation);
	    request.setPayload(payload_discover_keys);
	    
	    //json transmitter (http)
	    HttpJsonTransmitter httpJsonTransmitter = new HttpJsonTransmitter();
	    httpJsonTransmitter.setTargetURI("http://localhost:8080/demo_webservice/json");
	    final Response authedRequest = httpJsonTransmitter.take(request);
	    
	    System.out.println(authedRequest.getPayload());
	}
   
  }

}
