package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class Wave extends ExistingPlayer implements Serializable {

	private static final long serialVersionUID = -7224559829950770724L;

	private String otherPlayer;
	
	private boolean everyone;
	
	/**
	 * Wave to another player
	 * @param server The waving player's server
	 * @param player The waving player
	 * @param otherPlayer The waved to player
	 */
	public Wave(String server, String player, String otherServer, String otherPlayer) {
		super(server, player);
		this.otherPlayer = otherPlayer;
		everyone = false;
	}
	
	/**
	 * Wave to everyone
	 * @param server The player's server
	 * @param player The player
	 */
	public Wave(String server, String player) {
		super(server, player);
		everyone = true;
	}
	
	/**
	 * Is waving to everyone
	 * @return wave to everyone
	 */
	public boolean isWavingToEveryone() {
		return everyone;
	}
	
	/**
	 * Get the other player's name
	 * @return the player
	 */
	public String getOtherPlayer() {
		return otherPlayer;
	}

}
