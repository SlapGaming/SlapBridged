package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class ExistingPlayer implements Serializable {

	private static final long serialVersionUID = 3918252855298758051L;
	
	private String server;
	private String player;
	
	public ExistingPlayer(String server, String player) {
		this.server = server;
		this.player = player;
	}
	
	/**
	 * Get the player
	 * @return the playername
	 */
	public String getPlayer() {
		return player;
	}
	
	/**
	 * Get the server this player is from
	 * @return the servername
	 */
	public String getServer() {
		return server;
	}

}
