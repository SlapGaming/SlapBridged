package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class NewModreqEvent extends AbstractEvent {

	private OtherPlayer player;
	private String request;
	
	/**
	 * Creates a new NewModreq event
	 * @param server The other server this took event took place
	 * @param eventTime The time this event happend on the other server
	 * @param player The player
	 * @param request The request
	 */
	public NewModreqEvent(OtherServer server, long eventTime, OtherPlayer player, String request) {
		super(server, eventTime);
		this.player = player;
		this.request = request;
	}
	
	/**
	 * Get the name of the player who made the modreq
	 * @return the player
	 */
	public OtherPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Get the message of the request 
	 * @return the message
	 */
	public String getRequest() {
		return request;
	}

	
}
