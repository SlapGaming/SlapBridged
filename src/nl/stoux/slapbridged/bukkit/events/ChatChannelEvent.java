package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class ChatChannelEvent extends AbstractEvent {
	
	private OtherPlayer player;
	private String channel;
	private String message;
	
	/**
	 * Create a new chat channel event
	 * @param server The other server this took event took place
	 * @param eventTime The time this event took place on the other server
	 * @param channel The channel it was said in
	 * @param player The player who said it
	 * @param message The message
	 */
	public ChatChannelEvent(OtherServer server, long eventTime, String channel, OtherPlayer player, String message) {
		super(server, eventTime);
		this.channel = channel;
		this.player = player;
		this.message = message;
	}
	
	/**
	 * Get the chatchannel the message was said in
	 * @return the chatchannel
	 */
	public String getChannel() {
		return channel;
	}
	
	/**
	 * Get the message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Get the player that said the message
	 * @return the player
	 */
	public OtherPlayer getPlayerName() {
		return player;
	}

}
