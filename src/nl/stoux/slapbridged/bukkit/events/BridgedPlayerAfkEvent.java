package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedPlayerAfkEvent extends AbstractEvent {

	private OtherPlayer player;
	private String afkReason;
	
	/**
	 * A player goes AFK on a server
	 * @param server The server
	 * @param eventTime The time of this event on the other server
	 * @param player The player that went AFK
	 * @param afkReason The AFK reason
	 */
	public BridgedPlayerAfkEvent(OtherServer server, long eventTime, OtherPlayer player, String afkReason) {
		super(server, eventTime);
		this.player = player;
		this.afkReason = afkReason;
	}

	/**
	 * Get the player
	 * @return the player
	 */
	public OtherPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Get the AFK reason
	 * @return the reason
	 */
	public String getAfkReason() {
		return afkReason;
	}
	
}
