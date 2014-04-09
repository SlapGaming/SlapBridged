package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedPlayerAfkLeaveEvent extends AbstractEvent {

	private OtherPlayer player;
	
	/**
	 * A player leaves AFK on a server
	 * @param server The server
	 * @param eventTime The time of this event on the other server
	 * @param player The player that went AFK
	 */
	public BridgedPlayerAfkLeaveEvent(OtherServer server, long eventTime, OtherPlayer player) {
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
