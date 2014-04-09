package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedServerDisconnectsEvent extends AbstractEvent {

	public BridgedServerDisconnectsEvent(OtherServer server, long eventTime) {
		super(server, eventTime);
	}

}
