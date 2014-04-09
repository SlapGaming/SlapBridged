package nl.stoux.slapbridged.bukkit;

import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

public class EventLauncher extends BukkitRunnable {

	private Event event;
	
	public EventLauncher(Event event) {
		this.event = event;
	}
	
	@Override
	public void run() {
		BukkitUtil.callEvent(event);
		System.out.println("Called event: " + event.getEventName());
	}

}
