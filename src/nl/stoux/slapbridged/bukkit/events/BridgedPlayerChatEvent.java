package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

import org.bukkit.event.Cancellable;

public class BridgedPlayerChatEvent extends AbstractEvent implements Cancellable {

	//Cancelled status
	private boolean cancelled;
	
	//Chat info
	private OtherPlayer player;
	private String message;
	
	public BridgedPlayerChatEvent(OtherServer server, long eventTime, OtherPlayer player, String message) {
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
	
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

}
