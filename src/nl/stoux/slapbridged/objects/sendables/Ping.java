package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class Ping implements Serializable {

	private static final long serialVersionUID = -6689144024366336703L;
	
	//ID to handle with
	private String callbackID;
	
	//Data
	private long send;
	
	public Ping(String ID) {
		callbackID = ID;
		send = System.currentTimeMillis();
	}
	
	/**
	 * Get the time this was send
	 * @return the time
	 */
	public long getSend() {
		return send;
	}
	
	/**
	 * Get the ID of this ping object
	 * @return the ID
	 */
	public String getCallbackID() {
		return callbackID;
	}
	
	
	
}
