package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedPlayerMessageEvent extends AbstractEvent {

	private String fromPlayer;
	private String toPlayer;
	private String message;
	private boolean colorMessage;
	
	public BridgedPlayerMessageEvent(OtherServer server, long eventTime, String fromPlayer, String toPlayer, String message, boolean colorMessage) {
		super(server, eventTime);
		this.fromPlayer = fromPlayer;
		this.toPlayer = toPlayer;
		this.message = message;
		this.colorMessage = colorMessage;
	}
	
	/**
	 * Get the player who send this message
	 * @return the playername
	 */
	public String getFromPlayer() {
		return fromPlayer;
	}
	
	/**
	 * Get the to player
	 * @return the playername
	 */
	public String getToPlayer() {
		return toPlayer;
	}
	
	/**
	 * Get the message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Check if the message should be colored
	 * @return colored
	 */
	public boolean isColorMessage() {
		return colorMessage;
	}

}
