package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedPlayerMeEvent extends AbstractEvent {
	
	private OtherPlayer player;
	private String message;
	
	public BridgedPlayerMeEvent(OtherServer server, long eventTime, OtherPlayer player, String meMessage) {
		super(server, eventTime);
		this.player = player;
		this.message = meMessage;
	}
	
	/**
	 * Get the player
	 * @return the player
	 */
	public OtherPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Get the message after /me
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	

}
