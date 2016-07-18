package org.crysil.instance.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.crysil.instance.ServerWebSocketHandler;

/**
 * Lock used by {@link ServerWebSocketHandler} to wait for an answer over an WebSocket connection
 */
public class ConditionLock {

	private final Lock lock = new ReentrantLock();

	private final Condition ready = getLock().newCondition();

	private String response = null;

	public Lock getLock() {
		return lock;
	}

	public Condition getReady() {
		return ready;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}