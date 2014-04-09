package nl.stoux.slapbridged.bukkit;

import java.util.ArrayList;
import java.util.List;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.bukkit.API.BridgeAPI;
import nl.stoux.slapbridged.bukkit.API.callbacks.PlayerKnownCallback;
import nl.stoux.slapbridged.connection.Bridge;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.AfkReason;
import nl.stoux.slapbridged.objects.sendables.ChatChannelMessage;
import nl.stoux.slapbridged.objects.sendables.KnownPlayerRequest;

public class BridgeApiImpl implements BridgeAPI {

	private Bridge bridge;
	
	public BridgeApiImpl(Bridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public void ChatChannelMessage(String channel, String player, String message) {
		if (!bridge.isConnected()) return; //Ignore if not connected
		
		//Get server & create new sendable
		OtherServer thisServer = bridge.getThisServer();
		SendableContainer container = new SendableContainer(
			ObjectType.CHAT_CHANNEL_MESSAGE,
			new ChatChannelMessage(thisServer.getName(), player, channel, message),
			IdentifierGenerator.nextID()
		);
		
		//Send the container
		bridge.getOutgoing().send(container);
	}

	@Override
	public void IsPlayerKnown(String player, PlayerKnownCallback callback) {
		if (!bridge.isConnected()) return; //Ignore if not connected
		
		//Get server & create new sendable
		OtherServer thisServer = bridge.getThisServer();
		SendableContainer container = new SendableContainer(
			ObjectType.KNOWN_PLAYER_REQUEST,
			new KnownPlayerRequest(thisServer.getName(), player),
			IdentifierGenerator.nextID()
		);
		
		//Save callback
		bridge.getCallbackMap().put(container.getID(), callback);
		
		//Send container
		bridge.getOutgoing().send(container);
	}

	@Override
	public void PlayerGoesAFK(String player, String reason) {
		//Get player => Set to AFK
		OtherPlayer otherPlayer = bridge.getThisServer().getPlayers().get(player);
		if (otherPlayer != null) {
			otherPlayer.goesAfk(reason);
		}
		
		if (!bridge.isConnected()) return; //Ignore if not connected
		
		//Create new container
		SendableContainer container = new SendableContainer(
			ObjectType.PLAYER_AFK,
			new AfkReason(bridge.getThisServer().getName(), otherPlayer.getPlayername(), reason),
			IdentifierGenerator.nextID()
		);
		
		//Send container
		bridge.getOutgoing().send(container);
	}

	@Override
	public void PlayerLeavesAFK(String player) {
		//Get player => Leave AFK
		OtherPlayer otherPlayer = bridge.getThisServer().getPlayers().get(player);
		if (otherPlayer != null) {
			otherPlayer.leaveAFK();
		}
		
		if (!bridge.isConnected()) return; //Ignore if not connected
		
		//Create new container
		SendableContainer container = new SendableContainer(
			ObjectType.PLAYER_LEAVE_AFK,
			new AfkReason(bridge.getThisServer().getName(), player, null),
			IdentifierGenerator.nextID()
		);
		
		//Send container
		bridge.getOutgoing().send(container);
	}

	@Override
	public int getTotalPlayersOnline() {
		if (!bridge.isConnected()) return 0; //Return 0 if not connected
		
		//Count players
		int players = 0;
		for (OtherServer server : bridge.getOtherServersMap().values()) {
			players += server.getNrOfPlayersOnline();
		}
		return players;
	}

	@Override
	public List<OtherServer> getOtherServers() {
		if (!bridge.isConnected()) return new ArrayList<OtherServer>(); //Not connected => Empty list
		return new ArrayList<OtherServer>(bridge.getOtherServersMap().values());
	}

	@Override
	public boolean isConnected() {
		return bridge.isConnected();
	}
	
}
