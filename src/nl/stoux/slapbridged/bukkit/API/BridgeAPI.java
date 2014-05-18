package nl.stoux.slapbridged.bukkit.API;

import java.util.List;

import nl.stoux.slapbridged.bukkit.API.callbacks.PlayerKnownCallback;
import nl.stoux.slapbridged.connection.Bridge;
import nl.stoux.slapbridged.objects.OtherServer;

public interface BridgeAPI {

	/**
	 * A player sends a message to another player
	 * @param fromPlayer The sending player
	 * @param toPlayer The receiving player
	 * @param message The send message
	 * @param colorMessaged The message should be colored
	 */
	public void playerSendsMsg(String fromPlayer, String toPlayer, String message, boolean colorMessaged);
	
	/**
	 * Broadcast a message to all the other servers
	 * @param message The message
	 * @param prependSendingServer The other server should prepend [Sending Server name]
	 * @param prependSlap The other servers should prepend [SLAP] (after sending server)
	 */
	public void broadcastMessage(String message, boolean prependSendingServer, boolean prependSlap);
	
	/**
	 * Send a new ChatChannel message to the grid
	 * @param channel The channel it was send in
	 * @param player The player who send the message
	 * @param message The message
	 */
	public void chatChannelMessage(String channel, String player, String message);
	
	
	/**
	 * Makes a request to all other servers asking if the player is known
	 * @param player The player
	 * @param callback The callback with the results of your request.
	 */
	public void isPlayerKnown(String player, PlayerKnownCallback callback);
	
	/**
	 * A player goes AFK
	 * @param player the player
	 * @param reason the reason
	 */
	public void playerGoesAFK(String player, String reason);
	
	
	/**
	 * A player leaves AFK
	 * @param player the player
	 */
	public void playerLeavesAFK(String player);
	
	
	/**
	 * A player waves to another player
	 * @param fromPlayer The name of the first player
	 * @param toPlayer The name of the second player
	 */
	public void playerWavesToPlayer(String fromPlayer, String toPlayer);
	
	/**
	 * A player waves to everyone
	 * @param fromPlayer The sending player
	 */
	public void playerWavesToEveryone(String fromPlayer);
	
	/**
	 * A player uses the /me command
	 * @param player The player
	 * @param message The message
	 */
	public void playerUsesMeCommand(String player, String message);
	
	/**
	 * A player @ Mentions another player
	 * @param player The player mentioning
	 * @param message The message
	 */
	public void playerUsesMention(String player, String message);
	
	/**
	 * Get the total number of players online, excluding this server
	 * @return the number of online players
	 */
	public int getTotalPlayersOnline();
	
	/**
	 * Get a list of all other servers connected
	 * @return list of all other servers
	 */
	public List<OtherServer> getOtherServers();
	
	/**
	 * Check if the Bridge is connected to the Grid
	 * @return is connected
	 */
	public boolean isConnected();
	
	/**
	 * Get the Bridge.
	 * Warning: This class can easily change.
	 * @return the bridge
	 */
	public Bridge getBridge();
	
}
