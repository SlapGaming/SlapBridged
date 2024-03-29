package nl.stoux.slapbridged.grid.connection;

import java.io.IOException;
import java.net.Socket;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.grid.Grid;
import nl.stoux.slapbridged.grid.service.PingService;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.OtherServer;
import nl.stoux.slapbridged.objects.SendableContainer;

public class Peer implements Runnable {

	//General
	private Grid grid;
	private Socket socket;
	private OtherServer thisServer;
	
	//Connected
	private boolean connected;
	private boolean dismissed;
	
	//Connections
	private Thread incomingThread;
	private PeerIncomingConnection incoming;
	private Thread outgoingThread;
	private PeerOutgoingConnection outgoing;
	
	//Last time we've had INCOMING contact
	private long lastContact;
	
	public Peer(Grid grid, Socket socket) {
		this.grid = grid;
		this.socket = socket;
		connected = false;
		dismissed = false;
	}

	@Override
	public void run() {
		//Create incoming & outgoing connections
		(incomingThread = new Thread(incoming = new PeerIncomingConnection(grid, this, socket))).start();
		(outgoingThread = new Thread(outgoing = new PeerOutgoingConnection(this, socket))).start();
		
		//Wait for them to start up
		while (!outgoing.isRunning() && !incoming.isRunning()) {
			if (outgoing.hasError() || incoming.hasError()) { //Cancel both threads if error
				incomingThread.interrupt();
				outgoingThread.interrupt();
				//Close socket
				try {
					socket.close();
				} catch (IOException e) {
					
				}
				Thread.currentThread().interrupt();
				return;
			}
			try {
				synchronized(this) {
					this.wait(); //=> Start waiting for them to startup
				}
			} catch (InterruptedException e) { } //=> If interrupted check again
		}
		
		//Setup complete, wait for Peer to contact the Grid
	}
	
	public void setLastContact(long lastContact) {
		this.lastContact = lastContact;
	}
	
	/**
	 * Get system time when this server was last was contacted
	 * @return the last ping
	 */
	public long getLastContact() {
		return lastContact;
	}
	
	/**
	 * Set the server this peer has
	 * @param thisServer the server
	 */
	public void setThisServer(OtherServer thisServer) {
		this.thisServer = thisServer;
	}
	
	/**
	 * Get the server object this peer has
	 * @return the server
	 */
	public OtherServer getThisServer() {
		return thisServer;
	}

	/**
	 * Send a container to this peer
	 * @param container the container
	 */
	public void send(SendableContainer container) {
		outgoing.send(container);
	}
	
	/**
	 * Set this servers connect status
	 * @param connected status
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * Check if this server is connected
	 * @return is connected
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * See if this Peer has been dismissed
	 * Meaning that the other servers have been told that this server has left
	 * @return is dismissed
	 */
	public boolean isDismissed() {
		return dismissed;
	}
	
	/**
	 * Set dismissed stats
	 * @param dismissed
	 */
	public void setDismissed(boolean dismissed) {
		this.dismissed = dismissed;
	}
	
	/**
	 * Get the grid
	 * @return the grid
	 */
	public Grid getGrid() {
		return grid;
	}
	
	/**
	 * Stop this peer
	 * => Running to false
	 * => Interrupts threads
	 */
	public void stopPeer(SendableContainer... containers) {
		//Set connected status
		setConnected(false);
		
		//Disable the running status
		incoming.setRunning(false);
		outgoing.setRunning(false);
		
		//If fully connected
		if (thisServer != null) {
			//Connected status
			thisServer.setConnected(false);
			
			//Remove from map
			grid.getServers().remove(thisServer.getName());
			PingService.removeServer(thisServer.getName());
			
			System.out.println("Stopping peer: " + thisServer.getName()); //Output
			
			//Dismiss it if not done yet
			if (!isDismissed()) {
				setDismissed(true);
				
				//Create & Send disconnect container
				SendableContainer disconnectContainer = new SendableContainer(
					ObjectType.SERVER_DISCONNECT,
					getThisServer().getName(),
					IdentifierGenerator.nextID()
				);
				grid.sendToGrid(disconnectContainer, getThisServer().getName());
			}
		}
		
		//Stop threads
		incomingThread.interrupt();
		outgoingThread.interrupt();
		
		//Send container if there are any
		if (containers.length > 0) {
			for (SendableContainer container : containers) {
				directSend(container);
			}
		}
		
		//Close socket
		try {
			socket.close(); //Try to close it
		} catch (IOException e) {}
	}
	
	/**
	 * Directly send an object to the peer
	 * @param container The container
	 */
	private void directSend(SendableContainer container) {
		outgoing.directSend(container);
	}
	
}
