package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherServer;

public class ServerQuitGridEvent extends AbstractEvent {

	public ServerQuitGridEvent(OtherServer server, long eventTime) {
		super(server, eventTime);
	}

}
