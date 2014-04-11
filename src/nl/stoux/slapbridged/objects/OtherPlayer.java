package nl.stoux.slapbridged.objects;

import java.io.Serializable;

public class OtherPlayer implements Serializable {

	private static final long serialVersionUID = -3377432722893937068L;
	
	private String playername;
	private int rank;
	private String prefix;
	
	private boolean coloredChat;
	
	private boolean afk;
	private String afkReason;
	
	private boolean online;
	
	public OtherPlayer(String playername, int rank, String prefix, boolean coloredChat) {
		this.playername = playername;
		this.rank = rank;
		this.prefix = prefix;
		this.coloredChat = coloredChat;
		
		online = true;
		
		afk = false;
		afkReason = null;
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
	 * Check if the player is allowed to do colored chat messages
	 * @return is allowed
	 */
	public boolean hasColoredChat() {
		return coloredChat;
	}
	
	/**
	 * Copy this player
	 * @return the copy
	 */
	public OtherPlayer copy(OtherServer copyServer) {
		return new OtherPlayer(playername, rank, prefix, coloredChat);
	}

}
