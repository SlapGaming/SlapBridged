package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class ModreqEvent extends AbstractEvent {

	private OtherPlayer player;
	private String request;
	
	private ModreqType type;
	
	private String byMod;
	
	/**
	 * Creates a new NewModreq event
	 * @param server The other server this took event took place
	 * @param eventTime The time this event happend on the other server
	 * @param player The player
	 * @param request The request
	 */
	public ModreqEvent(OtherServer server, long eventTime, ModreqType type, OtherPlayer player, String request) {
		super(server, eventTime);
		this.player = player;
		this.request = request;
		this.type = type;
	}
	
	public ModreqEvent(OtherServer server, long eventTime, ModreqType type, OtherPlayer player, String request, String byMod) {
		super(server, eventTime);
		this.player = player;
		this.request = request;
		this.type = type;
		this.byMod = byMod;
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
	
	/**
	 * Get the type
	 * @return the type
	 */
	public ModreqType getType() {
		return type;
	}
	
	/**
	 * Get the mod who finished/claimed the modreq
	 * @return the mod's name
	 */
	public String getByMod() {
		return byMod;
	}

	public enum ModreqType {
		NEW, CLAIM, DONE
	}
	
}
