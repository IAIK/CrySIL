/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package proxy;

import java.util.ArrayList;
import java.util.List;

import org.crysil.commons.Module;
import org.crysil.commons.OneToManyInterlink;
import org.crysil.errorhandling.CrySILException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

import element.CrysilElement;

/**
 * The router takes commands and forwards them to an appropriate actor.
 * <p>
 * The class design is aimed at easy use with minimal coding overhead. Making the {@link Router} a static class is a first step towards clean code.
 * <p>
 * The router may implement some fancy routing mechanisms in the future. For now we just take the very first actor we find.
 */
public class Router extends OneToManyInterlink {

    /**
     * Instantiates a new router.
     */
    public Router() {
    }
    
    public Response take(Request crysilRequest) {
        List<String> path = crysilRequest.getHeader().getRequestPath();
        if (path == null) {
            path = new ArrayList<>();
            crysilRequest.getHeader().setRequestPath(path);
        }
        path.add(CrysilElement.getName());

        Logger.debug("got request");
        
        try {
			return this.getAttachedModules().get(0).take(crysilRequest);
		} catch (CrySILException e) {
			e.printStackTrace();
		}
        
        return null;
    }

}
