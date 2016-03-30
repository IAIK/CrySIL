package org.crysil.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.crysil.instance.datastore.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all the connected CrySIL Android servers to the {@link ServerWebSocketHandler}
 */
public class WebSocketManager {

	private static Logger logger = LoggerFactory.getLogger(WebSocketManager.class);
	private static WebSocketManager instance;

	/**
	 * Maps a GCM (random) token to ServerInfo
	 */
	private Map<String, ServerInfo> serverMappings;

	/**
	 * Maps a GCM (random) token to a condition (waiting for a Android server to connect)
	 */
	private Map<String, Condition> serverConditions;

	/**
	 * Mapping from crysilId to token (GCM)
	 */
	private Map<Long, List<String>> reusableConnections;

	/**
	 * Lock used for {@link #serverMappings}
	 */
	private Lock lock;

	public static WebSocketManager getInstance() {
		if (instance == null) {
			instance = new WebSocketManager();
		}
		return instance;
	}

	private WebSocketManager() {
		serverMappings = new HashMap<String, ServerInfo>();
		serverConditions = new HashMap<String, Condition>();
		reusableConnections = new HashMap<Long, List<String>>();
		lock = new ReentrantLock();
	}

	/**
	 * Call when a new WebSocket connection from an Android server is established
	 */
	public void addMapping(String token, ServerInfo info) {
		lock.lock();
		serverMappings.put(token, info);
		logger.debug("Looking for token {} in map of serverConditions", token);
		if (serverConditions.containsKey(token)) {
			logger.debug("Signal for token {}", token);
			serverConditions.get(token).signalAll();
		}
		lock.unlock();
	}

	/**
	 * May return an existing mapping for this <code>token</code> or <b>null</b>
	 */
	public ServerInfo getMapping(String token) {
		ServerInfo info = null;
		lock.lock();
		if (serverMappings.containsKey(token)) {
			info = serverMappings.get(token);
		}
		lock.unlock();
		return info;
	}

	/**
	 * Waits until a serverInfo for the given token is available, that means the CrySIL server has connected via a
	 * WebSocket to us
	 */
	public synchronized ServerInfo waitForServerInfo(String token) {
		lock.lock();
		if (serverMappings.containsKey(token)) {
			lock.unlock();
			return serverMappings.get(token);
		} else {
			Condition condition = serverConditions.get(token);
			if (condition == null) {
				condition = lock.newCondition();
				logger.debug("Adding new condition for token {}", token);
				serverConditions.put(token, condition);
			}
			try {
				condition.await(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.error("Error on waiting for connection condition", e);
			}
			lock.unlock();
			return serverMappings.get(token);
		}
	}

	/**
	 * Call when the Android server closes its WebSocket connection
	 */
	public synchronized void removeMapping(String token) {
		serverMappings.remove(token);
	}

	/**
	 * Marks an existing WebSocket connection as reusable for a new request by a CrySIL client
	 */
	public synchronized void addReusableConnectionToken(Long crysilId, String token) {
		if (!reusableConnections.containsKey(crysilId)) {
			reusableConnections.put(crysilId, new ArrayList<String>());
		}
		reusableConnections.get(crysilId).add(token);
	}

	/**
	 * May return a token for a already opened WebSocket connection to the Android server
	 */
	public synchronized String getReusableConnectionToken(Long crysilId) {
		String token = null;
		List<String> list = reusableConnections.get(crysilId);
		if (list != null && list.size() > 0) {
			token = list.get(0);
			list.remove(0);
		}
		return token;
	}

}
