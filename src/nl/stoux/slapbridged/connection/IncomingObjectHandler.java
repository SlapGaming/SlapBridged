package nl.stoux.slapbridged.connection;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import nl.stoux.slapbridged.bukkit.BukkitUtil;
import nl.stoux.slapbridged.bukkit.EventLauncher;
import nl.stoux.slapbridged.bukkit.API.callbacks.Callback;
import nl.stoux.slapbridged.bukkit.events.AbstractEvent;
import nl.stoux.slapbridged.bukkit.events.BridgedPlayerAfkEvent;
import nl.stoux.slapbridged.bukkit.events.BridgedPlayerAfkLeaveEvent;
import nl.stoux.slapbridged.bukkit.events.BridgedPlayerChatEvent;
import nl.stoux.slapbridged.bukkit.events.BridgedPlayerJoinEvent;
import nl.stoux.slapbridged.bukkit.events.BridgedPlayerQuitEvent;
import nl.stoux.slapbridged.bukkit.events.BridgedServerConnectsEvent;
import nl.stoux.slapbridged.bukkit.events.BridgedServerDisconnectsEvent;
import nl.stoux.slapbridged.bukkit.events.ChatChannelEvent;
import nl.stoux.slapbridged.bukkit.events.NewModreqEvent;
import nl.stoux.slapbridged.bukkit.events.ServerJoinGridEvent;
import nl.stoux.slapbridged.bukkit.events.ServerQuitGridEvent;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.AfkReason;
import nl.stoux.slapbridged.objects.sendables.Chat;
import nl.stoux.slapbridged.objects.sendables.ChatChannelMessage;
import nl.stoux.slapbridged.objects.sendables.ExistingPlayer;
import nl.stoux.slapbridged.objects.sendables.KnownPlayerRequest;
import nl.stoux.slapbridged.objects.sendables.Modreq;
import nl.stoux.slapbridged.objects.sendables.NewPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class IncomingObjectHandler extends BukkitRunnable {
	
	//Recieved container
	private SendableContainer container;
	
	//The bridge
	private Bridge bridge;
	
	/**
	 * Create a new Object handler
	 * @param bridge The bridge
	 * @param container The container containing the object
	 */
	public IncomingObjectHandler(Bridge bridge, SendableContainer container) {
		this.container = container;
		this.bridge = bridge;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		long time = container.getTime();
		
		AbstractEvent event; //Empty event
		OtherServer otherServer;
		OtherPlayer otherPlayer;
		
		AfkReason afkReason;
		
		switch (container.getType()) {
		//Grid events
		case GRID_WELCOME: //=> Connected, welcomed by the Grid
			Collection<OtherServer> serverCollection = (Collection<OtherServer>) container.getObject(); //Get the collection
			ConcurrentHashMap<String, OtherServer> serverMap = new ConcurrentHashMap<>();
			for (OtherServer server : serverCollection) { //Loop thru servers and to map
				serverMap.put(server.getName(), server);
			}
			bridge.setOtherServers(serverMap); //Set servers map
			bridge.setConnected(true); //We've been welcomed => Set connected
			
			//=> Bukkit events
			event = new ServerJoinGridEvent(bridge.getThisServer(), time); //Create Event
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast("We've joined the Grid!", true); //Message
			break;
			
		case GRID_SHUTDOWN:
			//Clear bridge
			bridge.getOtherServersMap().clear();
			bridge.getCallbackMap().clear();
			
			//=> Bukkit Events
			event = new ServerQuitGridEvent(bridge.getThisServer(), time); //Create event
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast("We've disconnected from the Grid!", true); //Message
			
			//Shutdown the grid
			bridge.setConnected(false);
			bridge.shutdown(true);
			break;
		
		//Server events
		case SERVER_CONNECT: //=> A server connected
			OtherServer newServer = (OtherServer) container.getObject();
			bridge.getOtherServersMap().put(newServer.getName(), newServer);
			
			//=> Bukkit events
			event = new BridgedServerConnectsEvent(newServer, time); //Create event
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast("We've been joined by another dimension!", true);
			BukkitUtil.broadcast("Welcome the players of " + ChatColor.translateAlternateColorCodes('&', newServer.getChatPrefix()), true); //Broadcast message
			break;
			
		case SERVER_DISCONNECT: //=> A server disconnects
			String serverName = (String) container.getObject();
			OtherServer disconnectedServer = bridge.getOtherServersMap().get(serverName); //Get the server
			if (disconnectedServer == null) {
				return; //Wasn't connected to it somehow, just ignore it
			}
			disconnectedServer.setConnected(false); //No longer connected to it
			bridge.getOtherServersMap().remove(serverName); //Remove from map
			
			//=> Bukkit events
			event = new BridgedServerDisconnectsEvent(disconnectedServer, time); //Create event
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast("The dimension " + translateColors(disconnectedServer.getChatPrefix()) + ChatColor.WHITE + " has disconnected & left the grid.", true); //message			
			break;
			
		//player events
		case PLAYER_AFK: //=> A bridged player goes AFK
			afkReason = (AfkReason) container.getObject();
			if ((otherServer = getServer(afkReason.getServer())) == null) return; //Get the server => If null return
			if ((otherPlayer = getPlayer(otherServer, afkReason.getPlayer())) == null) return; //Get the player => If null return
			otherPlayer.goesAfk(afkReason.getReason()); //Set to AFK
			
			//=> Bukkit Events
			event = new BridgedPlayerAfkEvent(otherServer, time, otherPlayer, afkReason.getReason()); //Create event
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			String broadcast = translateColors(otherServer.getChatPrefix()) + ChatColor.WHITE + " " + otherPlayer.getPlayername() + " is now AFK."; //Create the broadcast
			if (!afkReason.getReason().equals("AFK")) { //Add reason if not AFK
				broadcast += " Reason: " + afkReason.getReason();
			}
			BukkitUtil.broadcast(broadcast, false); //Broadcast
			break;
			
		case PLAYER_LEAVE_AFK: //=> A bridged player leaves AFK
			afkReason = (AfkReason) container.getObject();
			if ((otherServer = getServer(afkReason.getServer())) == null) return; //Get the server => If null return
			if ((otherPlayer = getPlayer(otherServer, afkReason.getPlayer())) == null) return; //Get the player => If null return
			otherPlayer.leaveAFK(); //Leave AFK
			
			//=> Bukkit Events
			event = new BridgedPlayerAfkLeaveEvent(otherServer, time, otherPlayer);
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast(translateColors(otherServer.getChatPrefix()) + ChatColor.WHITE + " " + afkReason.getPlayer() + " is no longer AFK.", false);
			break;
			
		case PLAYER_CHAT: //=> A bridged player chats
			Chat chat = (Chat) container.getObject();
			if ((otherServer = getServer(chat.getServer())) == null) return; //Get the server => If null return
			if ((otherPlayer = getPlayer(otherServer, chat.getPlayer())) == null) return; //Get the player => If null return
			
			//=> Bukkit events
			event = new BridgedPlayerChatEvent(otherServer, time, otherPlayer, chat.getChatMessage());
			BukkitUtil.callEvent(event);
			if (!BukkitUtil.isEventCancelled(event)) { //Check if not cancelled
				BukkitUtil.broadcast( //Broadcast the message
					translateColors(
						otherServer.getChatPrefix()) + 
						ChatColor.WHITE + "<" + 
						translateColors(otherPlayer.getPrefix() + otherPlayer.getPlayername()) + 
						ChatColor.WHITE + "> " + chat.getChatMessage(),
					false
				);
			}
			break;
			
		case PLAYER_JOIN: //=> A bridged player joins
			NewPlayer newPlayer = (NewPlayer) container.getObject();
			if ((otherServer = getServer(newPlayer.getServer())) == null) return; //Get the server => If null return
			
			//Get the player & Add it to the server map
			otherPlayer = newPlayer.getPlayer();
			otherServer.playerJoins(otherPlayer);
			
			//=> Bukkit events
			event = new BridgedPlayerJoinEvent(otherServer, time, otherPlayer);
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast(ChatColor.YELLOW + otherPlayer.getPlayername() + " has joined the dimension " + otherServer.getName(), false);
			break;
			
		case PLAYER_QUIT:
			ExistingPlayer quitingPlayer = (ExistingPlayer) container.getObject();
			if ((otherServer = getServer(quitingPlayer.getServer())) == null) return; //Get the server => If null return
			otherPlayer = getPlayer(otherServer, quitingPlayer.getPlayer()); //Get the player
			otherPlayer.setOnline(false);
			
			otherServer.playerQuits(otherPlayer.getPlayername());
			
			//=> Bukkit events
			event = new BridgedPlayerQuitEvent(otherServer, time, otherPlayer);
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast(ChatColor.YELLOW + otherPlayer.getPlayername() + " has left the dimension " + otherServer.getName(), false);
			break;
			
		//Modreq event
		case NEW_MODREQ:
			Modreq modreq = (Modreq) container.getObject();
			if ((otherServer = getServer(modreq.getServer())) == null) return; //Get the server => If null return
			if ((otherPlayer = getPlayer(otherServer, modreq.getPlayer())) == null) return; //Get the player => If null return
			
			//=> Bukkit events
			event = new NewModreqEvent(otherServer, time, otherPlayer, modreq.getModreq());
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			break;
			
		//Chat channel
		case CHAT_CHANNEL_MESSAGE:
			ChatChannelMessage ccMessage = (ChatChannelMessage) container.getObject();
			if ((otherServer = getServer(ccMessage.getServer())) == null) return; //Get the server => If null return
			if ((otherPlayer = getPlayer(otherServer, ccMessage.getPlayer())) == null) return; //Get the player => If null return
			
			//=> Bukkit events
			event = new ChatChannelEvent(otherServer, time, ccMessage.getChatChannel(), otherPlayer, ccMessage.getMessage());
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			break;
			
		//Requests
		case KNOWN_PLAYER_REQUEST:
			KnownPlayerRequest request = (KnownPlayerRequest) container.getObject();
			
			//Check if this is question or response to our question
			if (bridge.getThisServer().getName().equals(request.getServer())) {
				if ((otherServer = getServer(request.getServer())) == null) return; //Get the server => If null return			
				if ((otherPlayer = getPlayer(otherServer, request.getPlayer())) == null) return; //Get the player => If null return
				
				//=> Bukkit events
				Callback callback = bridge.getCallbackMap().get(container.getID());
				if (callback != null) {
					callback.Action(); //=> Action in ASync
				}
			} else { //Other server is asking for player
				//Get player & check if known
				OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(request.getPlayer());
				boolean known = (offPlayer == null ? false : (offPlayer.hasPlayedBefore() || offPlayer.getPlayer() != null));
				
				//Create response
				SendableContainer sendContainer = new SendableContainer(
					ObjectType.KNOWN_PLAYER_RESPONSE,
					known,
					container.getID()
				);
				bridge.getOutgoing().send(sendContainer);
			}
			break;
		
		case GRID_JOIN: case GRID_GOODBYE: case PING: //Shouldn't be reached
		default:
			return;
		}

	}

	/**
	 * Translate the & sign into ChatColors
	 * @param message The message
	 * @return the message
	 */
	private String translateColors(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	/**
	 * Get other server by its name
	 * @param name The name
	 * @return The server or null
	 */
	private OtherServer getServer(String name) {
		return bridge.getOtherServersMap().get(name);
	}
	
	/**
	 * Get a player from a certain server
	 * @param server The server
	 * @param player The playername
	 * @return the player object or null
	 */
	private OtherPlayer getPlayer(OtherServer server, String player) {
		return server.getPlayers().get(player);
	}
	
}
