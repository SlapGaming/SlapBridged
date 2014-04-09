package nl.stoux.slapbridged.objects;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OtherServer implements Serializable, Cloneable {

	private static final long serialVersionUID = 6162400939398416961L;

	private boolean connected;
	
	private String IP;
	private int port;
	private String name;
	private String tabName;
	private String chatPrefix;
	private ConcurrentHashMap<String, OtherPlayer> players;
	
	public OtherServer(String IP, int port, String name, String tabName, String chatPrefix, ConcurrentHashMap<String, OtherPlayer> players) {
		this.IP = IP;
		this.port = port;
		this.name = name;
		this.tabName = tabName;
		this.chatPrefix = chatPrefix;
		this.players = players;
		connected = true;
	}
	
	/**
	 * Get the IP of this server
	 * @return the IP
	 */
	public String getIP() {
		return IP;
	}
	
	/**
	 * Get the port of this server
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Get the chat prefix for this server
	 * @return the prefix
	 */
	public String getChatPrefix() {
		return chatPrefix;
	}
	
	/**
	 * Get the name for this server
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the number of players online on that server
	 * @return The number of players
	 */
	public int getNrOfPlayersOnline() {
		return players.size();
	}
	
	/**
	 * Get a map with all players on that server
	 * Key: Playername, Value: PlayerObject
	 * @return map with players
	 */
	public Map<String, OtherPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Set the players
	 * @param players
	 */
	public void setPlayers(ConcurrentHashMap<String, OtherPlayer> players) {
		this.players = players;
	}
	
	/**
	 * Get the name it should display in fancy [TAB]
	 * @return the tab name
	 */
	public String getTabName() {
		return tabName;
	}
	
	/**
	 * A player joins the server
	 * @param player The player
	 */
	public void playerJoins(OtherPlayer player) {
		players.put(player.getPlayername(), player);
	}
	
	/**
	 * A player leaves the server 
	 * @param player The player
	 */
	public void playerQuits(String player) {
		players.remove(player);
	}
	
	/**
	 * Set the connected state of this server
	 * @param connected is connected
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * See if that server is connected to the grid
	 * @return is connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Copy this Server object
	 * @return copy
	 */
	public OtherServer copy() {
		OtherServer copy = new OtherServer(IP, port, name, tabName, chatPrefix, null);		
		synchronized (this) {
			ConcurrentHashMap<String, OtherPlayer> copyMap = new ConcurrentHashMap<>();
			for (OtherPlayer p : copyMap.values()) {
				OtherPlayer copyPlayer = p.copy(copy);
				copyMap.put(copyPlayer.getPlayername(), copyPlayer);
			}
		}
		copy.setPlayers(players);
		copy.setConnected(this.connected);
		return copy;
	}

}
