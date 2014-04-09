package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

import nl.stoux.slapbridged.objects.OtherPlayer;

public class NewPlayer implements Serializable {

	private static final long serialVersionUID = -5822277003886162390L;
	
	private String server;
	private OtherPlayer player;
	
	public NewPlayer(String server, OtherPlayer player) {
		this.server = server;
		this.player = player;
	}
	
	/**
	 * Get the player object
	 * @return the player
	 */
	public OtherPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Get the server this player is on
	 * @return the servername
	 */
	public String getServer() {
		return server;
	}

}
