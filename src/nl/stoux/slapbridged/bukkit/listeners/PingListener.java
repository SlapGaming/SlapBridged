package nl.stoux.slapbridged.bukkit.listeners;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Random;

import nl.stoux.slapbridged.bukkit.SlapBridged;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {

	//Field
	private Field numPlayersField;
	
	//Data
	private int maxPlayers;
	private List<String> messages;
	
	//Random
	private Random random;
	private int max;
	
	public PingListener(int maxPlayers, List<String> messages) {
		this.maxPlayers = maxPlayers;
		this.messages = messages;
		
		random = new Random();
		max = messages.size();
		
		accesPlayers();
	}
	
	/**
	 * Access players field
	 */
	private void accesPlayers() {
		try {
			//Access numPlayers field
			Field f = ServerListPingEvent.class.getDeclaredField("numPlayers");
			f.setAccessible(true);
			
			//Access modifiers field
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			
			//Remove final from modifiers
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			
			//Set field
			numPlayersField = f;			
		} catch (Exception e) {
			System.out.println("Failed to edit players field in ping event");
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onServerPing(ServerListPingEvent event) {		
		if (numPlayersField != null) {
			int thesePlayers = SlapBridged.getInstance().getBridge().getThisServer().getNrOfPlayersOnline();
			int otherPlayers = SlapBridged.getAPI().getTotalPlayersOnline();
			
			try {
				numPlayersField.set(event, thesePlayers + otherPlayers);
			} catch (IllegalAccessException e) {}
		}
		
		//Max players
		event.setMaxPlayers(maxPlayers);
		event.setMotd(messages.get(random.nextInt(max)));		
	}

}
