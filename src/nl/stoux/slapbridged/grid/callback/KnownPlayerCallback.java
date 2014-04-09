package nl.stoux.slapbridged.grid.callback;

import nl.stoux.slapbridged.grid.connection.Peer;
import nl.stoux.slapbridged.grid.connection.PeerIncomingHandler;

public class KnownPlayerCallback extends GridCallback {
	
	//Requests
	private boolean known;
	private int requests;
	private int responses;
	
	public KnownPlayerCallback(String ID, Peer fromPeer, PeerIncomingHandler handler, int requestedServers) {
		super(ID, fromPeer, handler);
		this.peer = fromPeer;
		this.handler = handler;
		requests = requestedServers;
		known = false;
	}
	
	/**
	 * Got a response on this question
	 * @param known is known
	 */
	public void answer(boolean known) {
		if (known) {
			this.known = true;
		}
		responses++;
		if (responses == requests) {
			finish();
		}		
	}
	
	/**
	 * See if the player is known on other servers
	 * @return is known
	 */
	public boolean isKnown() {
		return known;
	}
}
