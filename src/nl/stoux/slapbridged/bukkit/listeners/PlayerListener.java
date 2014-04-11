package nl.stoux.slapbridged.bukkit.listeners;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.bukkit.SlapBridged;
import nl.stoux.slapbridged.connection.Bridge;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.Chat;
import nl.stoux.slapbridged.objects.sendables.ExistingPlayer;
import nl.stoux.slapbridged.objects.sendables.NewPlayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerListener implements Listener {

	private SlapBridged slapBridge;
	
	public PlayerListener() {
		slapBridge = SlapBridged.getInstance();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Bridge bridge = slapBridge.getBridge();
				
		//Create new player
		PermissionUser user = PermissionsEx.getUser(event.getPlayer());
		OtherPlayer otherPlayer = new OtherPlayer(
			event.getPlayer().getName(),
			user.getGroups()[0].getRank(),
			user.getPrefix()
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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		Bridge bridge = slapBridge.getBridge();
		
		//Get player
		OtherPlayer player = bridge.getThisServer().getPlayers().get(event.getPlayer().getName());
		player.setOnline(false); //Set offline
		
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

}
