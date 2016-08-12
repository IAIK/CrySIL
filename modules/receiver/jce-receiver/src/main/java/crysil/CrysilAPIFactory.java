/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package crysil;

import element.CrysilElement;

import proxy.Router;
import receiver.Receiver;
import org.crysil.communications.http.HttpJsonTransmitter;
import org.crysil.receiver.jcereceiver.crysil.APIReceiver;
import org.crysil.receiver.jcereceiver.crysil.CrysilAPI;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilHighLevelAPI;

public class CrysilAPIFactory {
    private static CrysilAPI crysilAPI;

    private CrysilAPIFactory() {
    }

    /**
     * Initialize with auth active. Legacy.
     *
     * @param url
     *            the url
     */
    public static void initialize(String url) {
        initialize(url, true);
    }

    public static void initialize(String url, boolean activeAuth) {
    	
    	//Element
        CrysilElement.getBasicElement("java-api-instance");
        Router router = CrysilElement.get().getRouter();
        
        //Router
        HttpJsonTransmitter httpJsonTransmitter = new HttpJsonTransmitter();
 	    httpJsonTransmitter.setTargetURI(url);
    	router.attach(httpJsonTransmitter);

        //Receiver
    	Receiver receiver = new APIReceiver();
        receiver.setRouter(router);
        CrysilElement.get().registerReceiver("APIReceiver", receiver);


        crysilAPI = CrysilAPI.getInstance();
    }

    public static CrysilAPI getCrysilAPI() {
        return crysilAPI;
    }

    public static CrysilHighLevelAPI getCrysilHighLevelAPI() {
        return new CrysilHighLevelAPI(crysilAPI);
    }
}
