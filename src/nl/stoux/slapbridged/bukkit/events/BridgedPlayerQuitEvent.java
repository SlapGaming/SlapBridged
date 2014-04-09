package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedPlayerQuitEvent extends AbstractEvent {

	private OtherPlayer player;
	
	/**
	 * A player quits another server
	 * @param server The server
	 * @param eventTime The time of this event on the other server
	 * @param player The player that quit
	 */
	public BridgedPlayerQuitEvent(OtherServer server, long eventTime, OtherPlayer player) {
		super(server, eventTime);
		this.player = player;
	}

	/**
	 * Get the player
	 * @return the player
	 */
	public OtherPlayer getPlayer() {
		return player;
	}
	
}
