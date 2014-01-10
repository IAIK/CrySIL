package at.iaik.skytrust.element.actors.gatekeeper;

import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.UserBean;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationInfo;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.Feature;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.FeatureSet;
import at.iaik.skytrust.element.actors.gatekeeper.session.SessionManager;
import at.iaik.skytrust.element.actors.gatekeeper.session.SessionTokenBean;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;
import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.status.SPayloadStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * decides upon the authentication methods without any actor involvement.
 */
public class SimpleGateKeeper implements IGateKeeper {

	/**
	 * A stateful representation of an authentication/authorization process.
	 */
	class AuthorizationProcess {

		/** The original request. */
		SRequest originalRequest;

		/** The pending auth challenge. */
		SResponse pendingAuthChallenge;

		/** The next step. */
		private AuthorizationStep nextStep;

		/**
		 * Instantiates a new authorization process.
		 * 
		 * @param request
		 *            the original request to be authorized
		 */
		public AuthorizationProcess(SRequest request) {
			originalRequest = request;
			nextStep = new CheckUser();
		}

		/**
		 * Step.
		 * 
		 * @param sRequest
		 *            the s request
		 * @return the s request
		 * @throws AuthenticationRequiredException
		 *             the authentication required exception
		 */
		public SRequest procede(SRequest sRequest) throws AuthenticationRequiredException {
			try {
				do {
					nextStep.perform(sRequest);
				} while (null != (nextStep = nextStep.getNextStep()));

				return originalRequest;
			} finally {
				if (null != nextStep)
					nextStep = nextStep.getNextStep();
			}
		}

		/**
		 * Gets the id.
		 * 
		 * @return the id
		 */
		public String getId() {
			return pendingAuthChallenge.getHeader().getCommandId();
		}

		/**
		 * The Class AuthorizationStep.
		 */
		abstract class AuthorizationStep {

			/**
			 * Perform.
			 * 
			 * @param sRequest
			 *            the s request
			 * @return the s request
			 * @throws AuthenticationRequiredException
			 *             the authentication required exception
			 */
			abstract public void perform(SRequest sRequest) throws AuthenticationRequiredException;

			/**
			 * Gets the next step.
			 * 
			 * @return the next step
			 */
			abstract public AuthorizationStep getNextStep();
		}

		/**
		 * The Class CheckUser.
		 */
		class CheckUser extends AuthorizationStep {

			private AuthorizationStep nextStep = new CheckRequest();

			@Override
			public void perform(SRequest sRequest) throws AuthenticationRequiredException {
				try {
					// fetch sessionId from request
					String sessionId = sRequest.getHeader().getSessionId();

					// fetch session
					SessionTokenBean session = sessionManager.getSession(sessionId);

					// check if the user-part of the session is still valid
					if (!session.checkUser()) // will raise an exception if session is invalid
						throw new Exception();
				} catch (Exception e) {
					this.nextStep = new IdentifyUser();
				}
			}

			@Override
			public AuthorizationStep getNextStep() {
				return this.nextStep;
			}
		}

		/**
		 * This {@link AuthorizationStep} fetches the appropriate user authentication method from the configuration. Note that at this point we do not
		 * know which user is to be authenticated so we cannot fetch the appropriate information in the first place.
		 */
		class IdentifyUser extends AuthorizationStep {

			/** The auth info. */
			AuthenticationInfo authInfo;

			/** The next step. */
			private AuthorizationStep nextStep;

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#perform(at.iaik.skytrust.element
			 * .skytrustprotocol.SRequest)
			 */
			public void perform(SRequest sRequest) throws AuthenticationRequiredException {
					// prepare filter for asking the config
					FeatureSet filter = new FeatureSet(new Feature("user", "?"));

					// fetch authentication requirements
					try {
						authInfo = configuration.getAuthenticationInfo(filter);
					} catch (AuthenticationFailedException e) {
						throw new AuthenticationRequiredException(createAuthenticationFailedResponse(originalRequest.getHeader()));
					}

					// create AuthChallenge
					SResponse response = createAuthChallenge(sRequest.getHeader(), authInfo.getMethod());

				// if we do not need an authentication
				if (null == response) {
					nextStep = new CheckRequest();
					return;
				} else
					nextStep = new AuthenticateUser(authInfo);

					// memorize pending authentication challenge
					pendingAuthChallenge = response;

					// inform caller
					throw new AuthenticationRequiredException(response);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#getNextStep()
			 */
			public AuthorizationStep getNextStep() {
				return nextStep;
			}
		}

		/**
		 * This {@link AuthorizationStep} takes an {@link AuthChallengeReply} and fetches the necessary authentication information from the
		 * configuration.
		 */
		class AuthenticateUser extends AuthorizationStep {

			/** The auth info. */
			AuthenticationInfo authInfo;

			/** The user. */
			UserBean user;

			/**
			 * Instantiates a new authenticate user step.
			 * 
			 * @param authInfo
			 *            the auth info
			 */
			public AuthenticateUser(AuthenticationInfo authInfo) {
				this.authInfo = authInfo;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#perform(at.iaik.skytrust.element
			 * .skytrustprotocol.SRequest)
			 */
			@Override
			public void perform(SRequest sRequest) throws AuthenticationRequiredException {
				SPayloadRequest payloadRequest = sRequest.getPayload();

				if (payloadRequest instanceof SPayloadAuthRequest) {
					try {
						// authenticate the incoming authentication challenge answer
						String identifier = authInfo.getMethod().getReceivedIdentifier((SPayloadAuthRequest) payloadRequest);
						configuration.isUserValid(identifier);

						// the user is a valid user if we get to this point
						// create UserBean
						user = new UserBean();
						user.setUserId(identifier);

						// prepare filter for asking the config
						FeatureSet features = new FeatureSet(new Feature("user", user.getUserId()));

						// fetch authentication requirements
						configuration.getAuthenticationInfo(features); // throws AuthenticationFailedException
					} catch (AuthenticationFailedException e) {
						// something went wrong. Initiate AuthFailed response.
						throw new AuthenticationRequiredException(createAuthenticationFailedResponse(originalRequest.getHeader()));
					}

					SessionTokenBean session = sessionManager.getSession(sRequest.getHeader().getSessionId());
					if (null == session)
						session = sessionManager.createSession(user);
					session.addEntry(new FeatureSet(new Feature("user", ".")), authInfo.getDuration()); // TODO grauslich
						
					// set the fresh sessionId
					originalRequest.getHeader().setSessionId(session.getSessionID());
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#getNextStep()
			 */
			@Override
			public AuthorizationStep getNextStep() {
				return new CheckRequest();
			}

		}

		/**
		 * checks the authorization status of the request itself.
		 */
		class CheckRequest extends AuthorizationStep {

			private AuthorizationStep nextStep = null;

			@Override
			public void perform(SRequest sRequest) throws AuthenticationRequiredException {
				FeatureSet featureSet = null;
				try {
					// assemble featureset
					SPayloadRequest payloadRequest = originalRequest.getPayload();

					// TODO parse command and extract featureset
					featureSet = new FeatureSet(new Feature("operation", payloadRequest.getCommand()));

					// fetch sessionId from request
					String sessionId = sRequest.getHeader().getSessionId();

					// fetch session
					SessionTokenBean session = sessionManager.getSession(sessionId);

					// check if the user-part of the session is still valid
					if (!session.checkFeatureSet(featureSet)) // will raise an exception if session is invalid
						throw new Exception();
				} catch (Exception e) {
					this.nextStep = new AskForAuthorizeRequest(featureSet);
				}
			}

			@Override
			public AuthorizationStep getNextStep() {
				return this.nextStep;
			}
		}

		/**
		 * This {@link AuthorizationStep} fetches the appropriate user authentication method from the configuration. Note that at this point we do not
		 * know which user is to be authenticated so we cannot fetch the appropriate information in the first place.
		 */
		class AskForAuthorizeRequest extends AuthorizationStep {

			/** The feature set. */
			private FeatureSet featureSet;

			/** The auth info. */
			private AuthenticationInfo authInfo;

			/** The next step. */
			private AuthorizationStep nextStep;

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#perform(at.iaik.skytrust.element
			 * .skytrustprotocol.SRequest)
			 */
			public AskForAuthorizeRequest(FeatureSet featureSet) {
				this.featureSet = featureSet;
			}

			public void perform(SRequest sRequest) throws AuthenticationRequiredException {
				// fetch authentication requirements
				try {
					authInfo = configuration.getAuthenticationInfo(featureSet);
				} catch (AuthenticationFailedException e) {
					throw new AuthenticationRequiredException(createAuthenticationFailedResponse(originalRequest.getHeader()));
				}

				// create AuthChallenge
				SResponse response = createAuthChallenge(sRequest.getHeader(), authInfo.getMethod());

				// if we do not need an authentication
				if (null == response) {
					nextStep = null;
					return;
				} else
					nextStep = new AuthorizeRequest(featureSet, authInfo);

				// memorize pending authentication challenge
				pendingAuthChallenge = response;

				// inform caller
				throw new AuthenticationRequiredException(response);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#getNextStep()
			 */
			public AuthorizationStep getNextStep() {
				return nextStep;
			}
		}

		/**
		 * This {@link AuthorizationStep} takes an {@link AuthChallengeReply} and fetches the necessary authentication information from the
		 * configuration.
		 */
		class AuthorizeRequest extends AuthorizationStep {

			/** The feature set. */
			private FeatureSet featureSet;

			/** The auth info. */
			private AuthenticationInfo authInfo;

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#perform(at.iaik.skytrust.element
			 * .skytrustprotocol.SRequest)
			 */
			public AuthorizeRequest(FeatureSet featureSet, AuthenticationInfo authInfo) {
				this.featureSet = featureSet;
				this.authInfo = authInfo;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#perform(at.iaik.skytrust.element
			 * .skytrustprotocol.SRequest)
			 */
			@Override
			public void perform(SRequest sRequest) throws AuthenticationRequiredException {
				SPayloadRequest payloadRequest = sRequest.getPayload();

				if (payloadRequest instanceof SPayloadAuthRequest) {
					try {
						// authenticate the incoming authentication challenge answer
						authInfo.getMethod().authenticate((SPayloadAuthRequest) payloadRequest);
					} catch (AuthenticationFailedException e) {
						// something went wrong. Initiate AuthFailed response.
						throw new AuthenticationRequiredException(createAuthenticationFailedResponse(originalRequest.getHeader()));
					}

					SessionTokenBean session = sessionManager.getSession(originalRequest.getHeader().getSessionId());
					session.addEntry(featureSet, authInfo.getDuration());
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper.AuthorizationProcess.AuthorizationStep#getNextStep()
			 */
			@Override
			public AuthorizationStep getNextStep() {
				return null;
			}

		}
	}

	/** The command queue. Incoming commands are put to the queue when authentication is to be performed. */
    protected SessionManager sessionManager = SessionManager.getInstance();

	/** The logger. */
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/** The configuration data source for the gatekeeper. */
	private Configuration configuration;

	/** The pending authorization processes. */
	private Map<String, AuthorizationProcess> pendingAuthorizationProcesses = new HashMap<>();

	/**
	 * Instantiates a new simple gate keeper.
	 * 
	 * @param config
	 *            the config
	 */
	public SimpleGateKeeper(Configuration config) {
		configuration = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.IGateKeeper#authenticate(at.iaik.skytrust.element.skytrustprotocol.SRequest)
	 */
	@Override
	public SRequest process(SRequest sRequest) throws AuthenticationRequiredException {
		// first find the appropriate AuthorizationProcess
		AuthorizationProcess process;
		if (sRequest.getPayload() instanceof SPayloadAuthRequest) {
			// we received an AuthChallengeResponse so there is already a pending AuthChallenge

			// use the server-generated commandId to match the processes
			String commandId = sRequest.getHeader().getCommandId();

			// retrieve the appropriate process from the list of pending processes
			process = pendingAuthorizationProcesses.get(commandId);
		 } else {
			// we received a fresh command so create a new AuthorizationProcess
			process = new AuthorizationProcess(sRequest);
		} 

		try {
			// perform the next step in the AuthorizationProcess
			SRequest updatedOriginalRequest = process.procede(sRequest);

			// in case we got here, there is no more user-interaction necessary for authorization and thus, the process is completed
			// remove the process from the list of pending processes
			try {
				pendingAuthorizationProcesses.remove(process.getId());
			} catch (Exception e) {

			}

			// return the result to the caller
			return updatedOriginalRequest;
		} catch (AuthenticationRequiredException e) {
			// we came here because user-interaction is necessary to perform the authorization
			// thus, get the servergenerated commandId of the AuthChallenge package and memorize the according process
			pendingAuthorizationProcesses.put(process.getId(), process);

			// throw the exception to the next level in order to send the encapsulated response
			throw e;
		}
	}

	/**
	 * Gets the user.
	 * 
	 * @param authenticatedRequest
	 *            the authenticated request
	 * @return the user
	 */
	public String getUserIdentifier(SRequest authenticatedRequest) {
		String sessionId = authenticatedRequest.getHeader().getSessionId();
		return sessionManager.getSession(sessionId).getUser().getUserId();
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
	protected SResponse createAuthChallenge(SkyTrustHeader header, AuthPlugin authPlugin) {
        String commandId = UUID.randomUUID().toString();

        SResponse authChallengeRespone = new SResponse();
        authChallengeRespone.setHeader(new SkyTrustHeader());
        authChallengeRespone.getHeader().setSessionId("");
        authChallengeRespone.getHeader().setCommandId(commandId);
        authChallengeRespone.getHeader().setProtocolVersion("0.1");
		// TODO add routing info

		// create and return authentication challenge
        return authPlugin.generateAuthChallenge(authChallengeRespone);
    }

	/**
	 * Creates the authentication failed response.
	 * 
	 * @param header
	 *            the header
	 * @return the response
	 */
	protected SResponse createAuthenticationFailedResponse(SkyTrustHeader header) {
		SResponse response = new SResponse();
		response.setHeader(new SkyTrustHeader());
		response.getHeader().setCommandId(header.getCommandId());
		response.getHeader().setSessionId("");
		response.getHeader().setProtocolVersion("0.1");
		// TODO add routing info
		SPayloadStatus statusPayload = new SPayloadStatus();
		statusPayload.setCode(400);
		response.setPayload(statusPayload);
		return response;
	}
}
