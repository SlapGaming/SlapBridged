package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedServerConnectsEvent extends AbstractEvent {

	public BridgedServerConnectsEvent(OtherServer server, long eventTime) {
		super(server, eventTime);
	}

}
