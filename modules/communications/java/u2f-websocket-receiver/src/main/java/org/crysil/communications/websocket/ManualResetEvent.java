package org.crysil.communications.websocket;

/**
 * Used for synchronization of different Threads. One Thread calls waitOne(...) another Thread calls set(). The first
 * Thread will continue. The barrier is then open and waitOne(..) calls will not block until reset is called.
 * 
 * @author areiter
 */
public class ManualResetEvent {

	private final Object monitor = new Object();
	private volatile boolean open = false;

	public ManualResetEvent(boolean open) {
		this.open = open;
	}

	public void waitOne() throws InterruptedException {
		synchronized (monitor) {
			while (open == false) {
				monitor.wait();
			}
		}
	}

	public boolean waitOne(long milliseconds) throws InterruptedException {
		synchronized (monitor) {
			if (open)
				return true;
			monitor.wait(milliseconds);
			return open;
		}
	}

	public void set() {
		synchronized (monitor) {
			open = true;
			monitor.notifyAll();
		}
	}

	public void reset() {
		open = false;
	}
}
