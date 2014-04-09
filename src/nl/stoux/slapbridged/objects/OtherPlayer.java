package nl.stoux.slapbridged.objects;

import java.io.Serializable;

public class OtherPlayer implements Serializable {

	private static final long serialVersionUID = -3377432722893937068L;
	
	private String playername;
	private int rank;
	private String prefix;
	
	private boolean afk;
	private String afkReason;
	
	private boolean online;
	
	public OtherPlayer(String playername, int rank, String prefix) {
		this.playername = playername;
		this.rank = rank;
		this.prefix = prefix;
		
		online = true;
		
		afk = false;
		afkReason = null;
	}
	
	public OtherPlayer(String playername, int rank, String prefix, String afkReason) {
		this.playername = playername;
		this.rank = rank;
		this.prefix = prefix;
		
		online = true;
		
		afk = true;
		this.afkReason = afkReason;
	}
	
	/**
	 * Get the playername
	 * @return The player
	 */
	public String getPlayername() {
		return playername;
	}
	
	/**
	 * Get the prefix of this player on the other server
	 * This will probably not be ChatColor encoded
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Get the rank of this player
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}
	
	/**
	 * Check if the player is AFK
	 * @return is AFK
	 */
	public boolean isAfk() {
		return afk;
	}
	
	/**
	 * Get the AFK reason
	 * @return the reason, can be null
	 */
	public String getAfkReason() {
		return afkReason;
	}
	
	/**
	 * Player goes AFK
	 * @param afkReason
	 */
	public void goesAfk(String afkReason) {
		this.afkReason = afkReason;
	}
	
	/**
	 * Player leaves AFK
	 */
	public void leaveAFK() {
		this.afk = false;
		this.afkReason = null;
	}
	
	/**
	 * Check if the player is online
	 * @return is online
	 */
	public boolean isOnline() {
		return online;
	}
	
	/**
	 * Set online
	 * @param online
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	/**
	 * Copy this player
	 * @return the copy
	 */
	public OtherPlayer copy(OtherServer copyServer) {
		return new OtherPlayer(playername, rank, prefix);
	}

}
