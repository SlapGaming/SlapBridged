package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherServer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class AbstractEvent extends Event {

	//Event stuff
	private static final HandlerList handlers = new HandlerList();
	
	//Other server
	private OtherServer server;
	
	//Event time
	private long eventTime;
	
	public AbstractEvent(OtherServer server, long eventTime) {
		this.server = server;
		this.eventTime = eventTime;
	}
	
	/**
	 * Get the time this event took place on the other server
	 * @return time in millis since unix
	 */
	public long getEventTime() {
		return eventTime;
	}
	
	/**
	 * The other server this event happened on
	 * @return the server
	 */
	public OtherServer getServer() {
		return server;
	}
	
	//Event stuff
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
