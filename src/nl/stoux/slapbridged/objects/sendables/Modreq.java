package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class Modreq extends ExistingPlayer implements Serializable {

	private static final long serialVersionUID = 4405682746576554827L;

	private int followID;
	
	private String modreq;
	private String modname;
	
	public Modreq(String server, String player, int followID, String modreq) {
		super(server, player);
		this.modreq = modreq;
	}
	
	public Modreq(String server, String player, int followID, String modreq, String modname) {
		super(server, player);
		this.modreq = modreq;
		this.modname = modname;
	}	
	
	
	/**
	 * Get the Request ID of the modreq on the server
	 * @return the ID
	 */
	public int getFollowID() {
		return followID;
	}
	
	/**
	 * Get the modreq message
	 * @return the modreq
	 */
	public String getModreq() {
		return modreq;
	}
	
	/**
	 * Get the name of the mod who is assigned to this modreq
	 * @return The name or null
	 */
	public String getModname() {
		return modname;
	}

}
