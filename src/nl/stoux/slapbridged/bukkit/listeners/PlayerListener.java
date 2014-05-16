package nl.stoux.slapbridged.bukkit.listeners;

import java.util.Collection;
import java.util.Map;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.bukkit.SlapBridged;
import nl.stoux.slapbridged.connection.Bridge;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.Chat;
import nl.stoux.slapbridged.objects.sendables.ExistingPlayer;
import nl.stoux.slapbridged.objects.sendables.NewPlayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerListener implements Listener {

	private SlapBridged slapBridge;
	private String colorPermission;
	
	public PlayerListener(String colorPermission) {
		slapBridge = SlapBridged.getInstance();
		this.colorPermission = colorPermission;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Bridge bridge = slapBridge.getBridge();
				
		//Create new player
		PermissionUser user = PermissionsEx.getUser(event.getPlayer());
		OtherPlayer otherPlayer = new OtherPlayer(
			event.getPlayer().getName(),
			user.getGroups()[0].getRank(),
			user.getPrefix(),
			user.has(colorPermission)
		);
		
		//Add to server
		bridge.getThisServer().playerJoins(otherPlayer);
		
		//If not connected => Ignore
		if (!bridge.isConnected()) return;
		
		//Create & Send conatiner
		SendableContainer container = new SendableContainer(
			ObjectType.PLAYER_JOIN, 
			new NewPlayer(bridge.getThisServer().getName(), otherPlayer),
			IdentifierGenerator.nextID()
		);
		bridge.getOutgoing().send(container);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Bridge bridge = slapBridge.getBridge();
		String leavingPlayer = event.getPlayer().getName();
		
		//Get player
		Map<String, OtherPlayer> players = bridge.getThisServer().getPlayers();
		OtherPlayer player = players.get(leavingPlayer);
		player.setOnline(false); //Set offline
		
		//Remove player
		bridge.getThisServer().playerQuits(leavingPlayer);
		
		//If not connected => Ignore
		if (!bridge.isConnected()) return;
		
		//Create & Send container
		SendableContainer container = new SendableContainer(
			ObjectType.PLAYER_QUIT,
			new ExistingPlayer(bridge.getThisServer().getName(), event.getPlayer().getName()),
			IdentifierGenerator.nextID()
		);
		bridge.getOutgoing().send(container);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) return; //Check if event not cancelled
		
		//Get bridge & check if connected
		Bridge bridge = slapBridge.getBridge();
		if (!bridge.isConnected()) return;
		
		//Create & Send container
		SendableContainer container = new SendableContainer(
			ObjectType.PLAYER_CHAT,
			new Chat(bridge.getThisServer().getName(), event.getPlayer().getName(), event.getMessage()),
			IdentifierGenerator.nextID()
		);
		bridge.getOutgoing().send(container);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatTabComplete(PlayerChatTabCompleteEvent event) {
		if (!event.getTabCompletions().isEmpty()) { //Check if something else changed the tab completion
			return; //If that's the case, don't change it.
		}
		
		//Check if bridge is available & connected
		Bridge bridge = slapBridge.getBridge();
		if (!bridge.isConnected()) return;
		
		//Get first word
		String[] split = event.getChatMessage().split(" "); //Split it on spaces
		String completor = split[split.length - 1]; //We only need the last one, which is being completed
		
		//Create list of all players
		Collection<String> suggestions = event.getTabCompletions();
		
		//	=> Add players from this servers
		addPlayers(suggestions, bridge.getThisServer(), completor);
		
		// 	=> From other servers
		for (OtherServer server : bridge.getOtherServersMap().values()) {
			addPlayers(suggestions, server, completor);
		}
	}
	
	/**
	 * Add players from a server to the collection
	 * @param collection The collection with tab suggestions
	 * @param server The server with players
	 * @param nameBeginsWith The beginning of the name, that the player has entered
	 */
	private void addPlayers(Collection<String> collection, OtherServer server, String nameBeginsWith) {
		Collection<String> players = server.getPlayers().keySet();
		if (players.isEmpty()) return; //Skip server if no players
		
		int beginLength = nameBeginsWith.length();
		if (beginLength == 0) { //Check if there's a name given
			//	=> No name given. Add all
			collection.addAll(players);
		} else {
			//	=> Name given, filter names
			for (String player : players) {
				if (player.length() >= beginLength) { //If name is just as long or longer than nameBeginsWith 
					//=> Compare them
					if (player.substring(0, beginLength).equalsIgnoreCase(nameBeginsWith)) { //If start of name matches beginWith string
						//=> Add to collection
						collection.add(player);
					}
				}
			}
		}
	}

}
