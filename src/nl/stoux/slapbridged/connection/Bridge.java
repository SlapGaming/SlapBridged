package nl.stoux.slapbridged.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.bukkit.BukkitUtil;
import nl.stoux.slapbridged.bukkit.EventLauncher;
import nl.stoux.slapbridged.bukkit.SlapBridged;
import nl.stoux.slapbridged.bukkit.API.callbacks.Callback;
import nl.stoux.slapbridged.bukkit.events.AbstractEvent;
import nl.stoux.slapbridged.bukkit.events.ServerQuitGridEvent;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.OtherServer;
import nl.stoux.slapbridged.objects.SendableContainer;

public class Bridge extends BukkitRunnable {

	//General info
	private String ip;
	private int port;
	private boolean connected;
	private OtherServer thisServer;
	
	private ConcurrentHashMap<String, OtherServer> otherServers;
	
	//Error info
	private boolean error;
	
	//Connection
	private Socket socket;
	private OutgoingConnection outgoing;
	private IncomingConnection incoming;
	
	//Callback map
	private ConcurrentHashMap<String, Callback> callbackMap;
	
	public Bridge(OtherServer thisServer, String gridIP, int gridPort) {
		this.thisServer = thisServer;
		this.ip = gridIP;
		this.port = gridPort;
		connected = false;
		error = false;
		
		callbackMap = new ConcurrentHashMap<>(); //Create new map
	}
	
	/**
	 * Connect the server
	 */
	public void connect() {
		connected = false;
		error = false;
		
		try {
			//Create socket & keep alive
			try {
				socket = new Socket(ip, port);
			} catch (IOException e) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(
					SlapBridged.getInstance(),
					new BukkitRunnable() {
						@Override
						public void run() {
							SlapBridged.getInstance().getBridge().connect(); //Try to connect again in a minute
						}
					}, 
					3600
				);
			}
			socket.setKeepAlive(true);
			
			//Create outgoing
			BukkitUtil.RunASync(outgoing = new OutgoingConnection(this, socket));
						
			//Create incoming
			BukkitUtil.RunASync(incoming = new IncomingConnection(this, socket));
			
			//Waiting for both threads to fully start working
			while (!outgoing.isRunning() && !incoming.isRunning()) {
				if (outgoing.hasError() || incoming.hasError()) { //Cancel both threads if error
					incoming.cancel();
					outgoing.cancel();
					this.cancel();
					
					error = true;
					return;
				}
				try {
					synchronized (this) {
						this.wait(); //=> Start waiting for them to startup
					}
				} catch (InterruptedException e) { } //=> If interrupted check again
			}
			
			//Both are running => Wait half a second to make sure the grid is also up & running
			try {
				synchronized(this) {
					this.wait(500);
				}
			} catch (InterruptedException e) {}
			
			//Let the Grid know who you are
			OtherServer copy = thisServer.copy(); //Copy the server object
			SendableContainer joinContainer = new SendableContainer(ObjectType.GRID_JOIN, copy, IdentifierGenerator.nextID()); //Create new container
			outgoing.send(joinContainer); //=> Send the join container
		} catch (IOException e) {
			error = true;
		}
	}
	
	/**
	 * Disconnect the bridge
	 */
	public void disconnect() {
		//Disconnect container
		SendableContainer container = new SendableContainer(
			ObjectType.GRID_GOODBYE,
			null,
			IdentifierGenerator.nextID()
		);
		
		//Direct send
		outgoing.directSend(container);
		
		//=> Bukkit Events
		if (SlapBridged.getInstance().isEnabled()) {
			AbstractEvent event = new ServerQuitGridEvent(getThisServer(), System.currentTimeMillis()); //Create event
			BukkitUtil.runSync(new EventLauncher(event)); //Launch event
			BukkitUtil.broadcast("We've disconnected the Grid!", true); //Message
		}
		
		//Shutdown bridge
		shutdown();
	}

	@Override
	public void run() {
		connect(); //Try to connect
	}
	
	/**
	 * The incoming connection
	 * @return the incoming con
	 */
	public IncomingConnection getIncoming() {
		return incoming;
	}
	
	/**
	 * Get the outgoing connection
	 * @return the outgoing con
	 */
	public OutgoingConnection getOutgoing() {
		return outgoing;
	}
	
	
	/*
	 * Others
	 */
	/**
	 * Set is connected
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * Check if an error occured
	 * @return has error
	 */
	public boolean hasError() {
		return error;
	}
	
	/**
	 * Check if the bridge correctly connected
	 * @return is connected
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Get this server
	 * @return the server
	 */
	public OtherServer getThisServer() {
		return thisServer;
	}
	
	/**
	 * Set other servers
	 * K: [Servername] => V: Other server
	 * @param otherServers 
	 */
	public void setOtherServers(ConcurrentHashMap<String, OtherServer> otherServers) {
		this.otherServers = otherServers;
	}
	
	/**
	 * Get the other servers
	 * K: [Servername] => V: Other server
	 * @return servers map
	 */
	public ConcurrentHashMap<String, OtherServer> getOtherServersMap() {
		return otherServers;
	}
	
	/**
	 * Get the map containg all Callbacks
	 * @return the Callback map
	 */
	public ConcurrentHashMap<String, Callback> getCallbackMap() {
		return callbackMap;
	}
	
	/**
	 * Shutdown bridge
	 */
	public void shutdown() {
		//clear map
		callbackMap.clear();
		
		//Connected is false => to make sure
		setConnected(false);
		
		//Incoming
		incoming.setRunning(false);
		synchronized(incoming) {
			incoming.notify();
		}
		
		//Outgoing
		outgoing.setRunning(false);
		synchronized(outgoing) {
			outgoing.notify();
		}
		
		try { //Wait
			synchronized(this) {
				wait(500);
			}
		} catch (InterruptedException e) {}
		
		//Cancel threads
		try {
			incoming.cancel();
			outgoing.cancel();
		} catch (IllegalStateException e) {}
		
		//Close socket
		try {
			socket.close();
		} catch (IOException e) {}
		
	}
	

}
