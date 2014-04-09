package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherServer;

public class ServerJoinGridEvent extends AbstractEvent {

	public ServerJoinGridEvent(OtherServer server, long eventTime) {
		super(server, eventTime);
	}

}
