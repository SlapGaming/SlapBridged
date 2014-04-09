package nl.stoux.slapbridged.bukkit.API;

import java.util.List;

import nl.stoux.slapbridged.bukkit.API.callbacks.PlayerKnownCallback;
import nl.stoux.slapbridged.objects.OtherServer;

public interface BridgeAPI {

	/**
	 * Send a new ChatChannel message to the grid
	 * @param channel The channel it was send in
	 * @param player The player who send the message
	 * @param message The message
	 */
	public void ChatChannelMessage(String channel, String player, String message);
	
	
	/**
	 * Makes a request to all other servers asking if the player is known
	 * @param player The player
	 * @param callback The callback with the results of your request.
	 */
	public void IsPlayerKnown(String player, PlayerKnownCallback callback);
	
	/**
	 * A player goes AFK
	 * @param player the player
	 * @param reason the reason
	 */
	public void PlayerGoesAFK(String player, String reason);
	
	
	/**
	 * A player leaves AFK
	 * @param player the player
	 */
	public void PlayerLeavesAFK(String player);
	
	/**
	 * Get the total number of players online, including this server
	 * @return the number of online players
	 */
	public int getTotalPlayersOnline();
	
	/**
	 * Get a list of all other servers connected
	 * @return list of all other servers
	 */
	public List<OtherServer> getOtherServers();
	

}
