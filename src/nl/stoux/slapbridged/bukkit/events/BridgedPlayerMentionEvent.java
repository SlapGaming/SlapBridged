package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedPlayerMentionEvent extends AbstractEvent {
	
	//Chat info
	private OtherPlayer player;
	private String message;
	
	public BridgedPlayerMentionEvent(OtherServer server, long eventTime, OtherPlayer player, String message) {
		super(server, eventTime);
		this.player = player;
		this.message = message;
	}

	/**
	 * Get the player who said the message
	 * @return
	 */
	public OtherPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Get the typed message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
