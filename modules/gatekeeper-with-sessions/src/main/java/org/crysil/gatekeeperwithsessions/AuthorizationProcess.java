package org.crysil.gatekeeperwithsessions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.AuthenticationPeriod;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.SessionHeader;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;

/**
 * A stateful representation of an authentication/authorization process.
 */
public class AuthorizationProcess {
    private Request originalRequest = null;
    private Response pendingAuthChallenge = null;
    private AuthorizationStep nextStep;
    private final List<Feature> featureSet = new ArrayList<>();
    private final AuthenticationPeriod period;
    private final Queue<AuthPlugin> authInfoQueue;

    public AuthorizationProcess(AuthenticationPeriod timeout, List<AuthPlugin> authSteps) {
        authInfoQueue = new LinkedList<>(authSteps);
        nextStep = new ChallengeStep(authInfoQueue.poll());
        period = timeout;
    }

    public AuthorizationProcess(AuthenticationPeriod timeout, AuthPlugin... authSteps) {
        authInfoQueue = new LinkedList<>(Arrays.asList(authSteps));
        nextStep = new ChallengeStep(authInfoQueue.poll());
        period = timeout;
    }

    Result doNextAuthStep(Request Request) throws AuthenticationRequiredException {
        if (null == originalRequest)
            originalRequest = Request;

        try {
            do {
                nextStep.perform(Request);

                nextStep = nextStep.getNextStep();
                if (null == nextStep && null != authInfoQueue.peek())
                    // fetch next AuthInfo from queue and set enqueue it
                    nextStep = new ChallengeStep(authInfoQueue.poll());

            } while (null != nextStep);

            return new Result(originalRequest, featureSet, period);
        } finally {
            if (null != nextStep) {
                nextStep = nextStep.getNextStep();
            }
        }
    }

    public String getCommandId() {
        return ((pendingAuthChallenge != null) ? pendingAuthChallenge.getHeader().getCommandId() : null);
    }

    private abstract class AuthorizationStep {
        abstract public void perform(Request Request) throws AuthenticationRequiredException;

        abstract public AuthorizationStep getNextStep();
    }

     /**
     * This {@link AuthorizationStep} fetches the appropriate user authentication method from the configuration. Note that at this point we do not
     * know which user is to be authenticated so we cannot fetch the appropriate information in the first place.
     */
    private class ChallengeStep extends AuthorizationStep {
        private final AuthPlugin authMethod;
        private AuthorizationStep nextStep;

        public ChallengeStep(AuthPlugin authMethod) {
            this.authMethod = authMethod;
        }

        @Override
        public void perform(Request Request) throws AuthenticationRequiredException {

            // create AuthChallenge
            Response response = createAuthChallenge(originalRequest.getHeader(), authMethod);

            // if we do not need an authorization
            if (response == null) {
                nextStep = null;
                return;
            } else {
                nextStep = new ValidateStep(authMethod);
            }

            // memorize pending authentication challenge
            pendingAuthChallenge = response;

            // inform caller
            throw new AuthenticationRequiredException(response);
        }

        @Override
        public AuthorizationStep getNextStep() {
            return nextStep;
        }
    }

    /**
     * This {@link AuthorizationStep} takes an {@link AuthChallengeReply} and fetches the necessary authentication information from the
     * configuration.
     */
    private class ValidateStep extends AuthorizationStep {
        private final AuthPlugin authMethod;

        public ValidateStep(AuthPlugin authMethod) {
            this.authMethod = authMethod;
        }

        @Override
        public void perform(Request Request) throws AuthenticationRequiredException {
            PayloadRequest payloadRequest = Request.getPayload();

            if (payloadRequest instanceof PayloadAuthRequest) {
                try {
                    // authenticate the incoming authentication challenge answer
                    authMethod.authenticate((PayloadAuthRequest) payloadRequest);
                } catch (AuthenticationFailedException e) {
                    // something went wrong. Initiate AuthFailed response.
                    throw new AuthenticationRequiredException(Gatekeeper.createAuthenticationFailedResponse(originalRequest.getHeader(),
									new AuthenticationFailedException()));
                }

                featureSet.add(authMethod.getAuthenticationResult());
            }
        }

        @Override
        public AuthorizationStep getNextStep() {
            return null;
        }

    }

    /**
     * Derives an authChallenge from the supplied authPlugin.
     *
     * @param header
     *            the header
     * @param authPlugin
     *            the auth plugin
     * @return the s response
     */
    private Response createAuthChallenge(Header header, AuthPlugin authPlugin) {
        String commandId = UUID.randomUUID().toString();

        Response authChallengeRespone = new Response();
        authChallengeRespone.setHeader(new StandardHeader());
		((SessionHeader) authChallengeRespone.getHeader()).setSessionId("");
        authChallengeRespone.getHeader().setCommandId(commandId);
        // TODO add routing info

        // create and return authentication challenge
        return authPlugin.generateAuthChallenge(authChallengeRespone);
    }
}
