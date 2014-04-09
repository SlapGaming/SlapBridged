package nl.stoux.slapbridged.grid.callback;

import nl.stoux.slapbridged.grid.connection.Peer;

public abstract class GridCallback {

	//Time info
	protected long created;
	protected long timeout;
	
	//General info
	protected String callbackID;
	protected Peer peer;
	protected Object handler;
	
	public GridCallback(String callbackID, Peer fromPeer, Object handler) {
		this.callbackID = callbackID;
		this.peer = fromPeer;
		this.handler = handler;
		created = System.currentTimeMillis();
		timeout = 5000;
	}
	
	public GridCallback(String callbackID, Peer fromPeer, Object handler, long timeout) {
		this.callbackID = callbackID;
		this.peer = fromPeer;
		this.handler = handler;
		created = System.currentTimeMillis();
		this.timeout = timeout;
	}
	
	/**
	 * Get the ID of this callback
	 * @return the ID
	 */
	public String getCallbackID() {
		return callbackID;
	}
	
	/**
	 * Get the time this callback was created
	 * @return the time
	 */
	public long getCreated() {
		return created;
	}
	
	/**
	 * Get the peer that created this callback
	 * @return the peer
	 */
	public Peer getPeer() {
		return peer;
	}
	
	/**
	 * Finish the callback
	 */
	public void finish() {
		peer.getGrid().getCallbacks().remove(callbackID); //=> Remove the Callback
		synchronized(handler) {
			handler.notify(); //=> Notify the handler
		}
	}
	
	/**
	 * Get timeout
	 * @return timeout
	 */
	public long getTimeout() {
		return timeout;
	}

}
