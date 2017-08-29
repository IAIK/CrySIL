package org.crysil.gatekeeperwithsessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.NotAcceptableException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;
import org.crysil.gatekeeperwithsessions.configuration.Key;
import org.crysil.gatekeeperwithsessions.configuration.Operation;
import org.crysil.gatekeeperwithsessions.session.SessionManager;
import org.crysil.gatekeeperwithsessions.session.SessionTokenBean;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.SessionHeader;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.status.PayloadStatus;

/**
 * decides upon the authentication methods without any actor involvement.
 */
public class Gatekeeper extends OneToOneInterlink implements Module
{

    /**
     * The command queue. Incoming commands are put to the queue when authentication is to be performed.
     */
    protected SessionManager sessionManager = SessionManager.getInstance();
    /**
     * The configuration data source for the gatekeeper.
     */
    private Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Gatekeeper() {
    }

    public Gatekeeper(Configuration configuration) {
        this.configuration = configuration;
    }

    private final Map<String, AuthorizationProcess> pendingAuthorizationProcesses = new HashMap<>();

	@Override
	public Response take(Request request) throws CrySILException {
    	// even firster, check if the header is appropriate for our use case
    	if(!(request.getHeader() instanceof SessionHeader))
			return createAuthenticationFailedResponse(request.getHeader(), new NotAcceptableException());
    	
        // first find the appropriate AuthorizationProcess
        AuthorizationProcess process;
        if (request.getPayload() instanceof PayloadAuthRequest) {
            // we received an AuthChallengeResponse so there is already a pending AuthChallenge

            // use the server-generated commandId to match the processes
            String commandId = request.getHeader().getCommandId();

            // retrieve the appropriate process from the list of pending processes
            process = pendingAuthorizationProcesses.remove(commandId);
        } else {
            // we received a fresh command so create a new AuthorizationProcess

            // extract features
            FeatureSet featureSet = extractFeatures(request);

            // check if we already have a session open
			SessionTokenBean session = sessionManager.getSession(((SessionHeader) request.getHeader()).getSessionId());

            // if yes, see if the command is already authorized
            if (session != null && session.checkFeatureSet(featureSet)) {

                // return the result to the caller
                return getAttachedModule().take(request);
            }

            // fetch required AuthorizationProcess from config
            try {
                process = configuration.getAuthorizationProcess(featureSet);
            } catch (AuthenticationFailedException e) {
                // this operation is simply not allowed. thus, report an unknown error.
                // TODO or should we state that the operation is not allowed? oracle?
				return createAuthenticationFailedResponse(request.getHeader(), new UnknownErrorException());
            }
        }

        try {
            // perform the next step in the AuthorizationProcess
            Result result = process.doNextAuthStep(request);
            Request updatedOriginalRequest = result.getOriginalRequest();

            // in case we got here, there is no more user-interaction necessary for authorization and thus, the process is completed
            // remove the process from the list of pending processes
            try {
                if (process.getCommandId() != null) {
                    pendingAuthorizationProcesses.remove(process.getCommandId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // create session
			SessionTokenBean session = sessionManager
					.getSession(((SessionHeader) updatedOriginalRequest.getHeader()).getSessionId());
            if (session == null) {
                session = sessionManager.createSession();
				((SessionHeader) updatedOriginalRequest.getHeader()).setSessionId(session.getSessionID());
            }
            session.addEntry(extractFeatures(updatedOriginalRequest), result.getPeriod());
            session.addEntry(new FeatureSet(result.getSessionFeatures()), result.getPeriod());

            // TODO recheck if the new info leads to more auth info gathering

            // return the result to the caller
			return getAttachedModule().take(updatedOriginalRequest);
        } catch (AuthenticationRequiredException e) {
            // we came here because user-interaction is necessary to perform the authorization
            // thus, get the servergenerated commandId of the AuthChallenge package and memorize the according process
            pendingAuthorizationProcesses.put(process.getCommandId(), process);

			return e.getResponse();
        }
    }

    /**
     * Extract features from a given request.
     *
     * @param Request
     *            the s request
     * @return the feature set
     */
    private FeatureSet extractFeatures(Request Request) {
        List<Feature> result = new ArrayList<>();

        PayloadRequest payload = Request.getPayload();

        // extract operation
        result.add(new Operation(payload.getType().replace("Request", "")));

        // extract keys
        if (payload instanceof PayloadWithKey) {
			for (org.crysil.protocol.payload.crypto.key.Key current : ((PayloadWithKey) payload).getKeys())
                result.add(new Key(current));
        }

        return new FeatureSet(result);
    }

    /**
     * Creates the authentication failed response.
     *
     * @param header
     *            the header
     * @return the response
     */
	static Response createAuthenticationFailedResponse(Header header, CrySILException exception) {
        Response crysilResponse = new Response();
        crysilResponse.setHeader(new StandardHeader());
        crysilResponse.getHeader().setCommandId(header.getCommandId());
		if (crysilResponse.getHeader() instanceof SessionHeader)
			((SessionHeader) crysilResponse.getHeader()).setSessionId("");

        PayloadStatus status = new PayloadStatus();
		status.setCode(exception.getErrorCode());
        crysilResponse.setPayload(status);

        return crysilResponse;
    }
}
